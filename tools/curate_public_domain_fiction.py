#!/usr/bin/env python3

import argparse
import json
import re
import unicodedata
import zipfile
from collections import Counter, defaultdict
from pathlib import Path

SOURCES = {
    "austen-emma.txt": ("Jane Austen", "Emma"),
    "austen-persuasion.txt": ("Jane Austen", "Persuasion"),
    "austen-sense.txt": ("Jane Austen", "Sense and Sensibility"),
    "blake-poems.txt": ("William Blake", "Poems"),
    "carroll-alice.txt": ("Lewis Carroll", "Alice's Adventures in Wonderland"),
    "chesterton-ball.txt": ("G. K. Chesterton", "The Ball and the Cross"),
    "chesterton-brown.txt": ("G. K. Chesterton", "The Wisdom of Father Brown"),
    "chesterton-thursday.txt": ("G. K. Chesterton", "The Man Who Was Thursday"),
    "edgeworth-parents.txt": ("Maria Edgeworth", "The Parent's Assistant"),
    "melville-moby_dick.txt": ("Herman Melville", "Moby-Dick"),
    "milton-paradise.txt": ("John Milton", "Paradise Lost"),
    "shakespeare-caesar.txt": ("William Shakespeare", "Julius Caesar"),
    "shakespeare-hamlet.txt": ("William Shakespeare", "Hamlet"),
    "shakespeare-macbeth.txt": ("William Shakespeare", "Macbeth"),
    "whitman-leaves.txt": ("Walt Whitman", "Leaves of Grass"),
}

CATEGORY_TERMS = {
    "discipline": {
        "habit", "duty", "discipline", "resolve", "resolved", "patience", "patient",
        "persevere", "perseverance", "steadfast", "steady", "constancy", "practice",
        "self-command", "self control", "self-control",
    },
    "resilience": {
        "adversity", "struggle", "endure", "endurance", "hardship", "suffer", "suffering",
        "failure", "failed", "defeat", "wound", "grief", "sorrow", "storm", "trial",
        "trouble", "misfortune", "pain",
    },
    "courage": {
        "courage", "brave", "bravery", "fear", "fearful", "danger", "dare", "daring",
        "coward", "bold", "boldly", "valor", "valour",
    },
    "growth": {
        "grow", "growth", "change", "changed", "become", "becoming", "improve",
        "experience", "progress", "develop", "development", "mature",
    },
    "learning": {
        "learn", "learning", "knowledge", "wisdom", "wise", "ignorance", "ignorant",
        "truth", "understand", "understanding", "thought", "think", "mind", "book",
        "books", "study", "education", "reason",
    },
    "focus": {
        "attention", "attend", "heed", "watch", "observe", "present", "concentrate",
        "single", "purpose", "resolve", "mind",
    },
    "work": {
        "work", "labour", "labor", "effort", "task", "duty", "deed", "action", "act",
        "doing", "practice", "craft", "industry",
    },
    "self-mastery": {
        "character", "temper", "control", "command", "desire", "conscience", "integrity",
        "honour", "honor", "self", "passion", "will", "master", "mastery",
    },
    "perspective": {
        "life", "death", "world", "happiness", "happy", "sorrow", "joy", "time", "truth",
        "reality", "fortune", "fate", "human", "nature", "meaning",
    },
    "purpose": {
        "purpose", "aim", "goal", "ambition", "meaning", "mission", "end", "intend",
        "intention", "calling", "direction",
    },
    "hope": {
        "hope", "despair", "future", "tomorrow", "possible", "possibility", "dawn",
        "light", "better", "begin", "beginning",
    },
    "relationships": {
        "friend", "friends", "friendship", "love", "kindness", "kind", "compassion",
        "trust", "family", "heart", "affection", "fellow", "fellowship", "sympathy",
        "respect",
    },
}

CATEGORY_PRIORITY = (
    "discipline", "resilience", "courage", "growth", "learning", "focus", "work",
    "self-mastery", "purpose", "hope", "relationships", "perspective",
)

BLOCK_PATTERNS = (
    r"\bchapter\b",
    r"\bvolume\b",
    r"\bproject gutenberg\b",
    r"\bhttp[s]?://",
    r"\bwww\.",
)

DIALOGUE_TAG = re.compile(
    r"\b(?:said|asked|replied|cried|answered|exclaimed|whispered)\s+(?:he|she|i|they|[A-Z][a-z]+)\b|"
    r"\b(?:he|she|i|they|[A-Z][a-z]+)\s+(?:said|asked|replied|cried|answered|exclaimed|whispered)\b"
)

WORD_RE = re.compile(r"\b[\w’'-]+\b", re.UNICODE)
SPACE_RE = re.compile(r"\s+")
SPLIT_RE = re.compile(r"(?<=[.!?])\s+(?=(?:[A-Z]|[“‘\"']))")


def normalize(text: str) -> str:
    text = unicodedata.normalize("NFKC", text)
    text = text.replace("\ufeff", " ").replace("_", "")
    text = SPACE_RE.sub(" ", text).strip()
    pairs = (("“", "”"), ('"', '"'), ("‘", "’"), ("'", "'"))
    changed = True
    while changed and len(text) >= 2:
        changed = False
        for left, right in pairs:
            if text.startswith(left) and text.endswith(right):
                text = text[len(left):-len(right)].strip()
                changed = True
                break
    return text


def words(text: str) -> list[str]:
    return WORD_RE.findall(text)


def category_scores(text: str) -> dict[str, int]:
    lower = text.lower()
    tokens = {token.lower() for token in words(text)}
    scores = {}
    for category, terms in CATEGORY_TERMS.items():
        score = 0
        for term in terms:
            if " " in term or "-" in term:
                if term in lower:
                    score += 2
            elif term in tokens:
                score += 1
        if score:
            scores[category] = score
    return scores


def quality_score(text: str, cat_scores: dict[str, int]) -> int:
    wc = len(words(text))
    score = 0
    if 7 <= wc <= 24:
        score += 5
    elif 5 <= wc <= 34:
        score += 3
    elif wc <= 42:
        score += 1

    score += min(5, sum(cat_scores.values()))
    score += min(3, len(cat_scores))

    lower = text.lower()
    if re.search(r"\b(?:must|can|cannot|shall|will|better|wisdom|truth|courage|hope|duty|learn|life|friend|work)\b", lower):
        score += 2
    if ";" in text or ":" in text:
        score += 1
    if re.search(r"\b(?:always|never|nothing|everything|all men|all women)\b", lower):
        score -= 1
    if DIALOGUE_TAG.search(text):
        score -= 3
    if text.count('"') + text.count("“") + text.count("”") >= 4:
        score -= 4
    if "..." in text or "…" in text:
        score -= 2
    if re.search(r"\b(?:mr|mrs|miss|sir|lady|lord)\.?(?:\s+[A-Z])", text):
        score -= 1
    return score


def primary_category(cat_scores: dict[str, int]) -> str | None:
    if not cat_scores:
        return None
    best = max(cat_scores.values())
    for category in CATEGORY_PRIORITY:
        if cat_scores.get(category) == best:
            return category
    return None


def iter_source_text(zip_path: Path):
    with zipfile.ZipFile(zip_path) as archive:
        names = set(archive.namelist())
        for filename, (author, work) in SOURCES.items():
            candidates = [filename, f"gutenberg/{filename}"]
            member = next((name for name in candidates if name in names), None)
            if not member:
                continue
            raw = archive.read(member).decode("utf-8", errors="replace")
            yield filename, author, work, raw


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("gutenberg_zip", type=Path)
    parser.add_argument("output_dir", type=Path)
    parser.add_argument("--minimum-score", type=int, default=8)
    parser.add_argument("--max-candidates", type=int, default=2400)
    args = parser.parse_args()

    args.output_dir.mkdir(parents=True, exist_ok=True)
    output = args.output_dir / "fiction_candidates.jsonl"
    report_path = args.output_dir / "fiction_report.md"

    records = []
    rejection = Counter()
    source_counts = Counter()
    category_counts = Counter()
    seen = set()

    for filename, author, work, raw in iter_source_text(args.gutenberg_zip):
        text = normalize(raw)
        for sentence in SPLIT_RE.split(text):
            sentence = normalize(sentence)
            wc = len(words(sentence))
            if wc < 5:
                rejection["too_short"] += 1
                continue
            if wc > 42 or len(sentence) > 330:
                rejection["too_long"] += 1
                continue
            if any(re.search(pattern, sentence, re.IGNORECASE) for pattern in BLOCK_PATTERNS):
                rejection["metadata_or_url"] += 1
                continue
            if sentence.count("[") + sentence.count("]") > 0:
                rejection["editorial_fragment"] += 1
                continue
            normalized_key = re.sub(r"[^a-z0-9]+", "", unicodedata.normalize("NFKD", sentence).encode("ascii", "ignore").decode().lower())
            if not normalized_key or normalized_key in seen:
                rejection["duplicate"] += 1
                continue
            cat_scores = category_scores(sentence)
            primary = primary_category(cat_scores)
            if primary is None:
                rejection["no_category_signal"] += 1
                continue
            score = quality_score(sentence, cat_scores)
            if score < args.minimum_score:
                rejection["low_quality_score"] += 1
                continue
            seen.add(normalized_key)
            record = {
                "text": sentence,
                "author": author,
                "work": work,
                "source_file": filename,
                "classification": primary,
                "candidate_categories": [c for c in CATEGORY_PRIORITY if c in cat_scores],
                "score": score,
                "dialogue_flag": bool(DIALOGUE_TAG.search(sentence)),
                "release_status": "needs_standalone_context_review",
            }
            records.append(record)

    records.sort(key=lambda row: (-row["score"], len(words(row["text"])), row["author"], row["text"]))
    records = records[: args.max_candidates]

    with output.open("w", encoding="utf-8") as handle:
        for row in records:
            handle.write(json.dumps(row, ensure_ascii=False, separators=(",", ":")) + "\n")
            source_counts[f"{row['author']} — {row['work']}"] += 1
            category_counts[row["classification"]] += 1

    lines = [
        "# Public-domain fiction candidate report",
        "",
        "Source corpus: NLTK Project Gutenberg Selections (`gutenberg`), published by NLTK as public domain.",
        "",
        f"- Candidates retained: **{len(records):,}**",
        f"- Minimum quality score: **{args.minimum_score}**",
        "",
        "> These are authentic public-domain source lines, but they still require standalone-context review before production promotion.",
        "",
        "## Categories",
        "",
    ]
    for category in CATEGORY_PRIORITY:
        lines.append(f"- `{category}`: {category_counts[category]:,}")
    lines += ["", "## Source works", ""]
    for source, count in source_counts.most_common():
        lines.append(f"- {source}: {count:,}")
    lines += ["", "## Mechanical rejections", ""]
    for reason, count in rejection.most_common():
        lines.append(f"- `{reason}`: {count:,}")
    report_path.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print(json.dumps({
        "candidates": len(records),
        "categories": dict(category_counts),
        "sources": dict(source_counts),
        "rejections": dict(rejection),
    }, indent=2, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
