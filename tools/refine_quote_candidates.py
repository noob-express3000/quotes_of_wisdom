#!/usr/bin/env python3

import argparse
import json
import re
import unicodedata
from collections import Counter, defaultdict
from pathlib import Path

STRONG_CATEGORY_TAGS = {
    "discipline": {
        "discipline", "self-discipline", "habit", "habits", "consistency",
        "commitment", "responsibility", "self-control", "persistence", "perseverance",
    },
    "resilience": {
        "resilience", "adversity", "hardship", "failure", "failures", "mistakes",
        "strength", "endurance", "recovery", "overcoming", "perseverance", "persistence",
    },
    "courage": {
        "courage", "bravery", "brave", "fear", "risk", "daring", "boldness",
    },
    "growth": {
        "growth", "change", "improvement", "progress", "development", "potential",
        "transformation",
    },
    "learning": {
        "learning", "education", "knowledge", "experience", "curiosity", "intelligence",
        "mistakes", "teach", "teaching",
    },
    "focus": {
        "focus", "attention", "concentration", "priorities", "priority", "simplicity",
        "mindfulness", "present", "awareness",
    },
    "work": {
        "work", "hard-work", "effort", "action", "productivity", "labor", "career",
        "achievement", "practice", "craft",
    },
    "self-mastery": {
        "character", "integrity", "self-control", "confidence", "responsibility",
        "self-respect", "self-knowledge", "attitude", "habits", "habit",
    },
    "perspective": {
        "philosophy", "perspective", "acceptance", "gratitude", "reflection", "attitude",
        "stoicism", "stoic",
    },
    "purpose": {
        "purpose", "meaning", "goals", "goal", "ambition", "leadership", "service",
        "mission", "calling", "direction",
    },
    "hope": {
        "hope", "optimism", "motivation", "motivational", "encouragement",
    },
    "relationships": {
        "friendship", "friends", "family", "kindness", "compassion", "empathy",
        "relationships", "relationship", "trust", "community", "cooperation", "respect",
    },
}

CATEGORY_PRIORITY = (
    "discipline", "resilience", "courage", "growth", "learning", "focus", "work",
    "self-mastery", "purpose", "hope", "relationships", "perspective",
)

# These topics are not necessarily bad in general; they are excluded because the app's
# production tone is grounded, broadly applicable motivation rather than romance,
# theology, partisan messaging, or mystical certainty.
BLOCKED_SOURCE_TAGS = {
    "love", "romance", "romantic", "dating", "marriage", "heartbreak", "soulmate",
    "sex", "sexuality", "erotica", "pornography",
    "god", "faith", "religion", "religious", "spirituality", "spiritual",
    "christianity", "islam", "hinduism", "buddhism", "atheism",
    "politics", "political", "elections", "partisan",
    "astrology", "horoscope", "zodiac", "occult", "magic", "manifestation",
    "suicide", "self-harm",
}

BLOCKED_TEXT_PATTERNS = (
    r"\b(?:boyfriend|girlfriend|husband|wife|soulmate|lover|romance|romantic|kiss|kissing)\b",
    r"\b(?:god|jesus|allah|prayer|pray|heaven|hell|angel|divine intervention)\b",
    r"\b(?:astrology|horoscope|zodiac|chakra|manifest(?:ing|ation)?|law of attraction)\b",
    r"\b(?:quantum healing|raise your vibration|positive vibrations?)\b",
    r"\b(?:kill yourself|better off dead)\b",
)

PROFANITY = re.compile(
    r"\b(?:fuck|fucking|shit|bullshit|bitch|bastard|asshole|motherfucker)\b",
    re.IGNORECASE,
)

# Strong signals of scraped dialogue or excerpts rather than standalone aphorisms.
DIALOGUE_OR_EXCERPT_PATTERNS = (
    r"\b[a-zA-Z][a-zA-Z'-]{1,20} said\b",
    r"\bsaid [A-Z][a-z]+\b",
    r"\b[a-zA-Z][a-zA-Z'-]{1,20} asked\b",
    r"\basked [A-Z][a-z]+\b",
    r"\b[a-zA-Z][a-zA-Z'-]{1,20} replied\b",
    r"\breplied [A-Z][a-z]+\b",
)

FALSE_CERTAINTY_PATTERNS = (
    r"\banything is possible\b",
    r"\byou can do anything\b",
    r"\beverything happens for a reason\b",
    r"\bthe universe (?:will|wants|has)\b",
    r"\bif you (?:just )?believe,? (?:you|it|everything) will\b",
    r"\bguaranteed to\b",
)

ENGLISH_MARKERS = {
    "the", "a", "an", "and", "or", "but", "if", "to", "of", "in", "on", "for",
    "with", "without", "from", "is", "are", "be", "being", "you", "your", "we",
    "our", "they", "their", "it", "that", "this", "what", "when", "how", "not",
    "do", "does", "can", "will", "should", "as", "by", "than", "into",
}

WORD_RE = re.compile(r"\b[\w’'-]+\b", re.UNICODE)


def words(text: str) -> list[str]:
    return [w.lower() for w in WORD_RE.findall(text)]


def mapped_categories(tags: set[str]) -> list[str]:
    return [category for category in CATEGORY_PRIORITY if tags & STRONG_CATEGORY_TAGS[category]]


def has_any_pattern(text: str, patterns: tuple[str, ...]) -> bool:
    return any(re.search(pattern, text, re.IGNORECASE) for pattern in patterns)


def likely_english(text: str) -> bool:
    tokens = words(text)
    if len(tokens) < 6:
        return False
    ascii_letters = sum(ch.isascii() and ch.isalpha() for ch in text)
    letters = sum(ch.isalpha() for ch in text)
    if letters and ascii_letters / letters < 0.90:
        return False
    return any(token in ENGLISH_MARKERS for token in tokens)


def malformed_spacing(text: str) -> bool:
    # Catches scrape damage such as "Keep GoingYour hardest" while ignoring acronyms.
    return bool(re.search(r"[a-z]{2}[A-Z][a-z]", text))


def refinement_rejection(record: dict, minimum_score: int) -> tuple[str | None, list[str], list[str]]:
    text = str(record.get("text", "")).strip()
    tags = {str(tag).lower() for tag in record.get("source_tags", [])}
    score = int(record.get("score", 0))
    categories = mapped_categories(tags)
    tokens = words(text)
    flags = list(record.get("review_flags", []))

    if score < minimum_score:
        return "score_below_high_precision_threshold", categories, flags
    if not categories:
        return "no_strong_category_signal", categories, flags
    if tags & BLOCKED_SOURCE_TAGS:
        return "blocked_high_precision_topic", categories, flags
    if has_any_pattern(text, BLOCKED_TEXT_PATTERNS):
        return "blocked_text_topic", categories, flags
    if has_any_pattern(text, FALSE_CERTAINTY_PATTERNS):
        return "false_certainty", categories, flags
    if PROFANITY.search(text):
        return "profanity", categories, flags
    if has_any_pattern(text, DIALOGUE_OR_EXCERPT_PATTERNS):
        return "dialogue_or_excerpt", categories, flags
    if "..." in text or "…" in text:
        return "ellipsis_fragment", categories, flags
    if malformed_spacing(text):
        return "malformed_spacing", categories, flags
    if not likely_english(text):
        return "likely_non_english", categories, flags
    if len(tokens) > 34:
        return "over_high_precision_length", categories, flags

    # High-precision candidates may still have absolute wording; retain it only as an
    # explicit human-review flag rather than silently treating the claim as factual.
    if re.search(r"\b(?:always|never|everyone|nobody|everything|nothing|only way)\b", text, re.IGNORECASE):
        if "absolute_language_review" not in flags:
            flags.append("absolute_language_review")

    if re.search(r"\b(?:will make you|will make|will bring|will lead to|will become)\b", text, re.IGNORECASE):
        flags.append("causal_claim_review")

    return None, categories, flags


def main() -> None:
    parser = argparse.ArgumentParser(description="Apply a high-precision gate to mechanically filtered quote candidates")
    parser.add_argument("input_jsonl", type=Path)
    parser.add_argument("output_dir", type=Path)
    parser.add_argument("--minimum-score", type=int, default=7)
    parser.add_argument("--sample-per-category", type=int, default=15)
    args = parser.parse_args()

    args.output_dir.mkdir(parents=True, exist_ok=True)
    output_path = args.output_dir / "high_precision_candidates.jsonl"
    report_json = args.output_dir / "high_precision_report.json"
    report_md = args.output_dir / "high_precision_report.md"

    total = 0
    kept = 0
    rejections = Counter()
    categories = Counter()
    flagged = Counter()
    samples = defaultdict(list)

    with args.input_jsonl.open("r", encoding="utf-8") as source, output_path.open("w", encoding="utf-8") as out:
        for line in source:
            if not line.strip():
                continue
            total += 1
            record = json.loads(line)
            reason, mapped, flags = refinement_rejection(record, args.minimum_score)
            if reason:
                rejections[reason] += 1
                continue

            primary = mapped[0]
            record["classification"] = primary
            record["candidate_categories"] = mapped
            record["review_flags"] = sorted(set(flags))
            record["release_status"] = "high_precision_candidate_needs_manual_verification"
            out.write(json.dumps(record, ensure_ascii=False, separators=(",", ":")) + "\n")
            kept += 1
            categories[primary] += 1
            flagged.update(record["review_flags"])
            if len(samples[primary]) < args.sample_per_category:
                samples[primary].append(record)

    report = {
        "input_mechanical_candidates": total,
        "high_precision_candidates": kept,
        "retention_rate": round(kept / total, 6) if total else 0,
        "rejections": dict(rejections.most_common()),
        "categories": {category: categories[category] for category in CATEGORY_PRIORITY},
        "review_flags": dict(flagged.most_common()),
        "warning": "These are still review candidates, not release-approved quotes. Attribution and rights verification remain mandatory.",
    }
    report_json.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

    lines = [
        "# High-precision quote candidate report",
        "",
        f"- Mechanical input: **{total:,}**",
        f"- High-precision candidates: **{kept:,}**",
        f"- Retention from mechanical pool: **{report['retention_rate']:.2%}**",
        "",
        "> Still not release-approved: attribution, rights, realism, and factual framing require manual verification.",
        "",
        "## Second-stage rejections",
        "",
    ]
    for reason, count in rejections.most_common():
        lines.append(f"- `{reason}`: {count:,}")

    lines += ["", "## High-precision counts by category", ""]
    for category in CATEGORY_PRIORITY:
        lines.append(f"- `{category}`: {categories[category]:,}")

    lines += ["", "## Human-review sample", ""]
    for category in CATEGORY_PRIORITY:
        if not samples[category]:
            continue
        lines += [f"### {category}", ""]
        for record in samples[category]:
            text = record["text"].replace("\n", " ")
            author = record["author"].replace("\n", " ")
            flags = ", ".join(record["review_flags"]) or "none"
            lines.append(f"- **{author}** — {text}  ")
            lines.append(f"  score={record['score']}; flags={flags}; source_index={record['source_index']}")
        lines.append("")

    report_md.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(json.dumps(report, indent=2, ensure_ascii=False))


if __name__ == "__main__":
    main()
