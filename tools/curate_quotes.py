#!/usr/bin/env python3

import argparse
import csv
import hashlib
import json
import re
import unicodedata
from collections import Counter, defaultdict
from pathlib import Path

CONTROLLED_CATEGORIES = (
    "discipline",
    "resilience",
    "courage",
    "growth",
    "learning",
    "focus",
    "work",
    "self-mastery",
    "perspective",
    "purpose",
    "hope",
    "relationships",
)

CATEGORY_TAGS = {
    "discipline": {
        "discipline", "self-discipline", "habit", "habits", "consistency",
        "commitment", "responsibility", "self-control", "persistence",
        "perseverance",
    },
    "resilience": {
        "resilience", "adversity", "hardship", "failure", "failures",
        "mistakes", "strength", "endurance", "recovery", "overcoming",
        "perseverance", "persistence",
    },
    "courage": {
        "courage", "bravery", "brave", "fear", "risk", "daring",
        "boldness",
    },
    "growth": {
        "growth", "change", "improvement", "progress", "development",
        "potential", "transformation",
    },
    "learning": {
        "learning", "education", "knowledge", "wisdom", "experience",
        "curiosity", "intelligence", "mistakes", "teach", "teaching",
    },
    "focus": {
        "focus", "attention", "concentration", "priorities", "priority",
        "simplicity", "mindfulness", "present", "awareness",
    },
    "work": {
        "work", "hard-work", "effort", "action", "productivity", "labor",
        "career", "achievement", "practice", "craft",
    },
    "self-mastery": {
        "character", "integrity", "self-control", "confidence", "identity",
        "responsibility", "self-respect", "self-knowledge", "attitude",
        "habits", "habit",
    },
    "perspective": {
        "life", "philosophy", "perspective", "truth", "reality", "attitude",
        "acceptance", "gratitude", "happiness", "wisdom", "reflection",
    },
    "purpose": {
        "purpose", "meaning", "goals", "goal", "ambition", "leadership",
        "service", "mission", "calling", "direction",
    },
    "hope": {
        "hope", "optimism", "inspirational", "motivation", "motivational",
        "encouragement", "possibility",
    },
    "relationships": {
        "friendship", "friends", "family", "kindness", "compassion",
        "empathy", "relationships", "relationship", "trust", "community",
        "cooperation", "respect",
    },
}

CATEGORY_PRIORITY = (
    "discipline", "resilience", "courage", "growth", "learning", "focus",
    "work", "self-mastery", "purpose", "hope", "relationships", "perspective",
)

UNCERTAIN_ATTRIBUTION_TAGS = {
    "attributed-no-source",
    "widely-misattributed",
    "misattributed",
    "misattribution",
}

BLOCKED_TAGS = {
    "erotica", "sex", "sexuality", "pornography", "romance", "romantic",
    "celebrity", "gossip", "politics", "political", "partisan", "elections",
    "religion", "religious", "atheism", "christianity", "islam", "hinduism",
    "astrology", "horoscope", "zodiac", "occult", "suicide", "self-harm",
}

MAGICAL_OR_FALSE_CERTAINTY = (
    r"\banything is possible\b",
    r"\byou can do anything\b",
    r"\beverything happens for a reason\b",
    r"\bthe universe (?:will|wants|has)\b",
    r"\bmanifest(?:ing|ation)?\b",
    r"\blaw of attraction\b",
    r"\braise your vibration\b",
    r"\bpositive vibrations?\b",
    r"\bquantum (?:energy|healing|manifest)\b",
    r"\bdestiny (?:will|has|wants)\b",
    r"\bmeant to be\b",
)

HARMFUL_PATTERNS = (
    r"\bkill yourself\b",
    r"\bbetter off dead\b",
    r"\brevenge is\b",
    r"\bhurt (?:them|him|her)\b",
)

DIALOGUE_PATTERNS = (
    r"\b(?:he|she|i|they) said\b",
    r"\b(?:he|she|i|they) asked\b",
    r"\b(?:he|she|i|they) replied\b",
    r"\bsaid (?:he|she|i|they)\b",
)

WORD_RE = re.compile(r"\b[\w’'-]+\b", re.UNICODE)
PUNCT_RE = re.compile(r"[^\w]+", re.UNICODE)
SPACE_RE = re.compile(r"\s+")


def normalize_text(value: str) -> str:
    value = unicodedata.normalize("NFKC", value or "")
    value = value.replace("\u00a0", " ")
    value = SPACE_RE.sub(" ", value).strip()

    # Strip decorative quotation marks only when they wrap the whole field.
    pairs = (("“", "”"), ('"', '"'), ("‘", "’"), ("'", "'"))
    changed = True
    while changed and len(value) >= 2:
        changed = False
        for left, right in pairs:
            if value.startswith(left) and value.endswith(right):
                value = value[len(left):-len(right)].strip()
                changed = True
                break

    return value


def normalize_author(value: str) -> str:
    value = unicodedata.normalize("NFKC", value or "")
    return SPACE_RE.sub(" ", value).strip(" -\t\r\n")


def parse_tags(value: str) -> set[str]:
    if not value:
        return set()
    return {
        SPACE_RE.sub(" ", tag.strip().lower()).replace("_", "-")
        for tag in value.split(",")
        if tag.strip()
    }


def word_count(text: str) -> int:
    return len(WORD_RE.findall(text))


def dedupe_key(text: str) -> str:
    folded = unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode()
    return PUNCT_RE.sub("", folded.lower())


def has_pattern(text: str, patterns: tuple[str, ...]) -> bool:
    lowered = text.lower()
    return any(re.search(pattern, lowered, re.IGNORECASE) for pattern in patterns)


def mapped_categories(tags: set[str]) -> list[str]:
    found = []
    for category in CATEGORY_PRIORITY:
        if tags & CATEGORY_TAGS[category]:
            found.append(category)
    return found


def author_is_unknown(author: str) -> bool:
    return author.strip().lower() in {
        "", "unknown", "anonymous", "anon", "n/a", "none", "null",
    }


def looks_like_dialogue(text: str) -> bool:
    if has_pattern(text, DIALOGUE_PATTERNS):
        return True
    # Multiple alternating quote marks are a strong scraped-fiction signal.
    quote_marks = sum(text.count(ch) for ch in ('"', "“", "”"))
    return quote_marks >= 4


def score_candidate(text: str, author: str, tags: set[str], categories: list[str]) -> tuple[int, list[str]]:
    score = 0
    flags = []
    words = word_count(text)

    if 8 <= words <= 30:
        score += 3
    elif 6 <= words <= 45:
        score += 1

    if {"inspirational", "motivation", "motivational", "wisdom"} & tags:
        score += 2

    score += min(3, len(categories))

    if "," not in author:
        score += 1
    else:
        flags.append("author_field_includes_source_or_title")

    if not re.search(r"\b(?:i|me|my|mine)\b", text.lower()):
        score += 1
    else:
        flags.append("first_person")

    if re.search(r"\b(?:always|never|everyone|nobody|everything|nothing)\b", text.lower()):
        flags.append("absolute_language_review")

    if re.search(r"\b(?:success|successful|rich|wealth|money)\b", text.lower()):
        flags.append("outcome_claim_review")

    return score, flags


def reject_reason(text: str, author: str, tags: set[str], categories: list[str]) -> str | None:
    words = word_count(text)

    if not text or not author:
        return "missing_text_or_author"
    if author_is_unknown(author):
        return "unknown_author"
    if words < 6:
        return "too_short"
    if words > 45:
        return "too_long"
    if len(text) > 360:
        return "too_long_for_ui"
    if tags & UNCERTAIN_ATTRIBUTION_TAGS:
        return "uncertain_attribution"
    if tags & BLOCKED_TAGS:
        return "blocked_topic"
    if not categories:
        return "no_controlled_category"
    if has_pattern(text, MAGICAL_OR_FALSE_CERTAINTY):
        return "magical_or_false_certainty"
    if has_pattern(text, HARMFUL_PATTERNS):
        return "harmful_content"
    if looks_like_dialogue(text):
        return "context_dependent_dialogue"
    if re.search(r"https?://|www\.", text.lower()):
        return "embedded_url"
    if text.count("…") + text.count("...") >= 2:
        return "fragment_or_excerpt"
    return None


def choose_primary(categories: list[str]) -> str:
    return categories[0]


def read_rows(path: Path):
    # The source has occasional malformed rows. utf-8-sig handles a possible BOM;
    # errors=replace avoids aborting the entire pass for one damaged byte sequence.
    with path.open("r", encoding="utf-8-sig", errors="replace", newline="") as handle:
        reader = csv.DictReader(handle)
        if not reader.fieldnames:
            raise RuntimeError("CSV has no header")

        lower_to_actual = {name.strip().lower(): name for name in reader.fieldnames if name}
        required = {"quote", "author", "category"}
        missing = required - set(lower_to_actual)
        if missing:
            raise RuntimeError(
                f"CSV missing expected columns {sorted(missing)}; found {reader.fieldnames}"
            )

        q_col = lower_to_actual["quote"]
        a_col = lower_to_actual["author"]
        c_col = lower_to_actual["category"]

        for index, row in enumerate(reader, start=1):
            yield index, row.get(q_col, ""), row.get(a_col, ""), row.get(c_col, "")


def main() -> None:
    parser = argparse.ArgumentParser(description="Filter Quotes-500K into a human-review candidate pool")
    parser.add_argument("input_csv", type=Path)
    parser.add_argument("output_dir", type=Path)
    parser.add_argument("--sample-per-category", type=int, default=12)
    parser.add_argument("--minimum-score", type=int, default=4)
    args = parser.parse_args()

    args.output_dir.mkdir(parents=True, exist_ok=True)
    candidates_path = args.output_dir / "candidates.jsonl"
    report_json_path = args.output_dir / "report.json"
    report_md_path = args.output_dir / "report.md"

    rejection_counts = Counter()
    category_counts = Counter()
    score_counts = Counter()
    samples: dict[str, list[dict]] = defaultdict(list)
    seen = set()
    total = 0
    retained = 0
    duplicate_count = 0

    with candidates_path.open("w", encoding="utf-8") as out:
        for source_index, raw_text, raw_author, raw_tags in read_rows(args.input_csv):
            total += 1
            text = normalize_text(raw_text)
            author = normalize_author(raw_author)
            tags = parse_tags(raw_tags)
            categories = mapped_categories(tags)

            reason = reject_reason(text, author, tags, categories)
            if reason:
                rejection_counts[reason] += 1
                continue

            key = dedupe_key(text)
            if not key or key in seen:
                duplicate_count += 1
                rejection_counts["duplicate"] += 1
                continue
            seen.add(key)

            score, flags = score_candidate(text, author, tags, categories)
            if score < args.minimum_score:
                rejection_counts["low_motivational_quality_score"] += 1
                continue

            primary = choose_primary(categories)
            record = {
                "source_index": source_index,
                "text": text,
                "author": author,
                "classification": primary,
                "candidate_categories": categories,
                "source_tags": sorted(tags),
                "score": score,
                "review_flags": flags,
                "release_status": "needs_attribution_and_rights_verification",
                "fingerprint": hashlib.sha256(key.encode("utf-8")).hexdigest()[:16],
            }
            out.write(json.dumps(record, ensure_ascii=False, separators=(",", ":")) + "\n")
            retained += 1
            category_counts[primary] += 1
            score_counts[score] += 1

            if len(samples[primary]) < args.sample_per_category:
                samples[primary].append(record)

    report = {
        "source_rows": total,
        "mechanical_candidates": retained,
        "candidate_rate": round(retained / total, 6) if total else 0,
        "duplicate_rows_removed": duplicate_count,
        "rejections": dict(rejection_counts.most_common()),
        "categories": {category: category_counts[category] for category in CONTROLLED_CATEGORIES},
        "scores": {str(score): count for score, count in sorted(score_counts.items())},
        "warning": (
            "Mechanical candidates are NOT approved for release. Every retained row still requires "
            "attribution and redistribution-rights verification, plus human realism/truthfulness review."
        ),
    }
    report_json_path.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

    lines = [
        "# Quotes-500K mechanical curation report",
        "",
        f"- Source rows: **{total:,}**",
        f"- Mechanical candidates: **{retained:,}**",
        f"- Candidate rate: **{report['candidate_rate']:.2%}**",
        f"- Duplicates removed: **{duplicate_count:,}**",
        "",
        "> Mechanical candidates are not release-approved. They still require attribution, rights, realism, and truthfulness verification.",
        "",
        "## Rejections",
        "",
    ]
    for reason, count in rejection_counts.most_common():
        lines.append(f"- `{reason}`: {count:,}")

    lines += ["", "## Candidate counts by primary category", ""]
    for category in CONTROLLED_CATEGORIES:
        lines.append(f"- `{category}`: {category_counts[category]:,}")

    lines += ["", "## Review sample", ""]
    for category in CONTROLLED_CATEGORIES:
        if not samples[category]:
            continue
        lines.append(f"### {category}")
        lines.append("")
        for record in samples[category]:
            safe_text = record["text"].replace("\n", " ")
            safe_author = record["author"].replace("\n", " ")
            flags = ", ".join(record["review_flags"]) or "none"
            lines.append(f"- **{safe_author}** — {safe_text}  ")
            lines.append(f"  score={record['score']}; flags={flags}; source_index={record['source_index']}")
        lines.append("")

    report_md_path.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(json.dumps(report, indent=2, ensure_ascii=False))


if __name__ == "__main__":
    main()
