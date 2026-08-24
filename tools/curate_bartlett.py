#!/usr/bin/env python3

import argparse
import json
import re
import unicodedata
from collections import Counter
from pathlib import Path

# Handles ordinary headings (1564-1616), ancient headings (8 B. C.-65 A. D.),
# and open-ended headings used for authors who were alive in an earlier edition (1809- ----).
AUTHOR_RE = re.compile(
    r"^([A-Z][A-Z .,'&-]+)\.\s+"
    r"(\d{1,4})(?:\s+B\.\s*C\.)?\s*-\s*"
    r"(?:(\d{1,4})|----)(?:\s+A\.\s*D\.)?\.\s*$"
)
SOURCE_RE = re.compile(r"^_(.+)_\s*$")
FOOTNOTE_RE = re.compile(r"\[\d+(?:-\d+)?\]")
WORD_RE = re.compile(r"\b[\w’'-]+\b", re.UNICODE)
SPACE_RE = re.compile(r"\s+")

KNOWN_DEATH_YEARS = {
    "ALFRED TENNYSON": 1892,
    "RUDYARD KIPLING": 1936,
    "THOMAS HARDY": 1928,
    "GEORGE MEREDITH": 1909,
    "MARK TWAIN": 1910,
    "SAMUEL L. CLEMENS": 1910,
    "ROBERT LOUIS STEVENSON": 1894,
    "OSCAR WILDE": 1900,
    "ARTHUR CONAN DOYLE": 1930,
    "GEORGE BERNARD SHAW": 1950,
    "H. G. WELLS": 1946,
}

CATEGORY_TERMS = {
    "discipline": ("duty", "habit", "patience", "persever", "steadfast", "industry", "temperance", "resolve", "practice", "self-command", "restraint"),
    "resilience": ("advers", "struggle", "suffer", "misfortune", "trial", "endure", "grief", "sorrow", "failure", "defeat", "pain", "hardship"),
    "courage": ("courage", "brave", "fear", "danger", "dare", "bold", "valour", "valor", "coward"),
    "growth": ("grow", "growth", "change", "improve", "progress", "experience", "become", "development"),
    "learning": ("learn", "knowledge", "wisdom", "wise", "truth", "reason", "study", "book", "thought", "understand", "ignorance", "mind"),
    "focus": ("attention", "heed", "observe", "watch", "purpose", "single", "present", "mind"),
    "work": ("work", "labour", "labor", "effort", "deed", "action", "industry", "task", "practice", "toil"),
    "self-mastery": ("self", "character", "temper", "control", "command", "conscience", "integrity", "honour", "honor", "will", "passion", "master"),
    "perspective": ("life", "death", "world", "time", "fortune", "happiness", "joy", "sorrow", "nature", "human", "truth", "reality"),
    "purpose": ("purpose", "aim", "goal", "ambition", "meaning", "mission", "end", "calling", "direction"),
    "hope": ("hope", "despair", "future", "better", "begin", "dawn", "possib", "light"),
    "relationships": ("friend", "friendship", "love", "kind", "kindness", "compassion", "trust", "heart", "affection", "sympathy", "respect"),
}

CATEGORY_PRIORITY = (
    "discipline", "resilience", "courage", "growth", "learning", "focus", "work",
    "self-mastery", "purpose", "hope", "relationships", "perspective",
)

BLOCK_PATTERNS = (
    r"\b(?:god|christ|jesus|heaven|hell|satan|devil)\b",
    r"\b(?:murder|kill|slay)\b",
)


def normalize(text: str) -> str:
    text = unicodedata.normalize("NFKC", text)
    text = FOOTNOTE_RE.sub("", text)
    text = text.replace("--", "—")
    text = SPACE_RE.sub(" ", text).strip()
    return text


def word_count(text: str) -> int:
    return len(WORD_RE.findall(text))


def display_author(raw: str) -> str:
    small = {"OF", "AND", "THE", "DE", "DU", "VON"}
    parts = []
    for token in raw.split():
        if token in small and parts:
            parts.append(token.lower())
        elif token in {"I", "II", "III", "IV", "V"}:
            parts.append(token)
        else:
            parts.append(token.title())
    return " ".join(parts)


def category_scores(text: str) -> dict[str, int]:
    lower = text.lower()
    scores = {}
    for category, stems in CATEGORY_TERMS.items():
        score = sum(1 for stem in stems if stem in lower)
        if score:
            scores[category] = score
    return scores


def choose_primary(scores: dict[str, int]) -> str | None:
    if not scores:
        return None
    best = max(scores.values())
    for category in CATEGORY_PRIORITY:
        if scores.get(category) == best:
            return category
    return None


def quality_score(text: str, scores: dict[str, int]) -> int:
    wc = word_count(text)
    score = 0
    if 6 <= wc <= 18:
        score += 6
    elif wc <= 28:
        score += 4
    elif wc <= 40:
        score += 2
    score += min(5, sum(scores.values()))
    score += min(3, len(scores))
    lower = text.lower()
    if re.search(r"\b(?:better|must|shall|can|cannot|truth|wisdom|courage|hope|duty|learn|work|life|friend|self)\b", lower):
        score += 2
    if ";" in text or ":" in text:
        score += 1
    if re.search(r"\b(?:always|never|everything|nothing|every man|all men)\b", lower):
        score -= 1
    return score


def resolve_death_year(author: str, raw_death: str | None) -> int | None:
    if raw_death:
        return int(raw_death)
    return KNOWN_DEATH_YEARS.get(author)


def parse_bartlett(path: Path):
    lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    current_author = None
    death_year = None
    quote_lines = []
    in_footnotes = False

    def flush(source=None):
        nonlocal quote_lines
        if current_author and quote_lines and not in_footnotes:
            text = normalize(" ".join(line.strip() for line in quote_lines))
            quote_lines = []
            if text:
                return {
                    "text": text,
                    "author": display_author(current_author),
                    "death_year": death_year,
                    "work": normalize(source or "") or None,
                }
        quote_lines = []
        return None

    for line in lines:
        author_match = AUTHOR_RE.match(line)
        if author_match:
            maybe = flush()
            if maybe:
                yield maybe
            current_author = author_match.group(1)
            death_year = resolve_death_year(current_author, author_match.group(3))
            in_footnotes = False
            continue

        if current_author is None:
            continue

        if line.strip() == "FOOTNOTES:":
            quote_lines = []
            in_footnotes = True
            continue

        if in_footnotes:
            continue

        source_match = SOURCE_RE.match(line.strip())
        if source_match:
            record = flush(source_match.group(1))
            if record:
                yield record
            continue

        if line.startswith("    ") and line.strip():
            quote_lines.append(line)
        elif not line.strip():
            continue
        elif quote_lines:
            quote_lines = []

    maybe = flush()
    if maybe:
        yield maybe


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input_text", type=Path)
    parser.add_argument("output_dir", type=Path)
    parser.add_argument("--minimum-score", type=int, default=8)
    parser.add_argument("--max-death-year", type=int, default=1955)
    parser.add_argument("--max-candidates", type=int, default=3000)
    args = parser.parse_args()

    args.output_dir.mkdir(parents=True, exist_ok=True)
    candidates = []
    rejections = Counter()
    categories = Counter()
    authors = Counter()
    seen = set()

    for record in parse_bartlett(args.input_text):
        text = record["text"]
        wc = word_count(text)
        if record["death_year"] is None:
            rejections["unknown_death_year"] += 1
            continue
        if record["death_year"] > args.max_death_year:
            rejections["author_not_global_pd_priority"] += 1
            continue
        if wc < 5:
            rejections["too_short"] += 1
            continue
        if wc > 45 or len(text) > 360:
            rejections["too_long"] += 1
            continue
        if any(re.search(p, text, re.IGNORECASE) for p in BLOCK_PATTERNS):
            rejections["blocked_context"] += 1
            continue
        if text.startswith(("Note ", "See ", "Compare ")):
            rejections["editorial_note"] += 1
            continue
        key = re.sub(r"[^a-z0-9]+", "", unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode().lower())
        if not key or key in seen:
            rejections["duplicate"] += 1
            continue
        scores = category_scores(text)
        primary = choose_primary(scores)
        if not primary:
            rejections["no_category_signal"] += 1
            continue
        qscore = quality_score(text, scores)
        if qscore < args.minimum_score:
            rejections["low_quality_score"] += 1
            continue
        seen.add(key)
        record.update({
            "classification": primary,
            "candidate_categories": [c for c in CATEGORY_PRIORITY if c in scores],
            "score": qscore,
            "release_status": "bartlett_source_verified_needs_context_review",
        })
        candidates.append(record)

    candidates.sort(key=lambda r: (-r["score"], word_count(r["text"]), r["author"], r["text"]))
    candidates = candidates[: args.max_candidates]

    out = args.output_dir / "bartlett_candidates.jsonl"
    with out.open("w", encoding="utf-8") as handle:
        for row in candidates:
            handle.write(json.dumps(row, ensure_ascii=False, separators=(",", ":")) + "\n")
            categories[row["classification"]] += 1
            authors[row["author"]] += 1

    report = args.output_dir / "bartlett_report.md"
    lines = [
        "# Bartlett public-domain quotation report",
        "",
        "Source: Project Gutenberg #27889, John Bartlett's *Familiar Quotations*, Ninth Edition (1905).",
        "",
        f"- Candidate quotations: **{len(candidates):,}**",
        f"- Author death-year ceiling: **{args.max_death_year}**",
        f"- Minimum quality score: **{args.minimum_score}**",
        "",
        "> Footnote comparison quotations are excluded. Author headings, source citations, and public-domain priority are resolved before product review.",
        "",
        "## Categories",
        "",
    ]
    for cat in CATEGORY_PRIORITY:
        lines.append(f"- `{cat}`: {categories[cat]:,}")
    lines += ["", "## Most represented authors", ""]
    for author, count in authors.most_common(30):
        lines.append(f"- {author}: {count:,}")
    lines += ["", "## Rejections", ""]
    for reason, count in rejections.most_common():
        lines.append(f"- `{reason}`: {count:,}")
    report.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print(json.dumps({
        "candidates": len(candidates),
        "categories": dict(categories),
        "top_authors": dict(authors.most_common(20)),
        "rejections": dict(rejections),
    }, indent=2, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
