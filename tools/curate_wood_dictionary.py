#!/usr/bin/env python3

import argparse
import json
import re
import unicodedata
from collections import Counter
from pathlib import Path

WORD_RE = re.compile(r"\b[\w’'-]+\b", re.UNICODE)
SPACE_RE = re.compile(r"\s+")
PAGE_RE = re.compile(r"\s+\d+\s*$")
SOURCE_RE = re.compile(r"_([^_]+)_")

CATEGORIES = {
    "discipline": ("discipline", "duty", "habit", "patience", "persever", "steadfast", "temperance", "resolve", "practice", "restraint", "diligence", "industry"),
    "resilience": ("advers", "struggle", "endure", "hardship", "suffer", "failure", "defeat", "misfortune", "trial", "grief", "sorrow", "pain", "trouble"),
    "courage": ("courage", "brave", "fear", "danger", "dare", "bold", "valour", "valor", "fortitude"),
    "growth": ("grow", "growth", "change", "improve", "progress", "experience", "become", "develop"),
    "learning": ("learn", "knowledge", "wisdom", "wise", "truth", "reason", "study", "book", "thought", "understand", "ignorance", "mind", "intelligence"),
    "focus": ("attention", "heed", "observe", "watch", "present", "concentrat", "single purpose", "mind"),
    "work": ("work", "labour", "labor", "effort", "deed", "action", "industry", "task", "practice", "toil"),
    "self-mastery": ("self", "character", "temper", "control", "command", "conscience", "integrity", "honour", "honor", "will", "passion", "master"),
    "perspective": ("life", "time", "fortune", "happiness", "joy", "sorrow", "nature", "human", "truth", "reality", "world"),
    "purpose": ("purpose", "aim", "goal", "ambition", "meaning", "mission", "end", "calling", "direction"),
    "hope": ("hope", "future", "better", "begin", "dawn", "possib", "light", "despair"),
    "relationships": ("friend", "friendship", "kind", "kindness", "compassion", "trust", "heart", "affection", "sympathy", "respect", "love"),
}

CATEGORY_PRIORITY = (
    "discipline", "resilience", "courage", "growth", "learning", "focus", "work",
    "self-mastery", "purpose", "hope", "relationships", "perspective",
)

AUTHOR_EXPANSIONS = {
    "Bacon": "Francis Bacon",
    "Burke": "Edmund Burke",
    "Burns": "Robert Burns",
    "Byron": "Lord Byron",
    "Carlyle": "Thomas Carlyle",
    "Cervantes": "Miguel de Cervantes",
    "Cic.": "Cicero",
    "Coleridge": "Samuel Taylor Coleridge",
    "Confucius": "Confucius",
    "Cowper": "William Cowper",
    "Dante": "Dante Alighieri",
    "Dryden": "John Dryden",
    "Emerson": "Ralph Waldo Emerson",
    "Epictetus": "Epictetus",
    "Eurip.": "Euripides",
    "Franklin": "Benjamin Franklin",
    "Goethe": "Johann Wolfgang von Goethe",
    "Goldsmith": "Oliver Goldsmith",
    "Hor.": "Horace",
    "Hume": "David Hume",
    "Johnson": "Samuel Johnson",
    "La Roche.": "François de La Rochefoucauld",
    "Longfellow": "Henry Wadsworth Longfellow",
    "Lowell": "James Russell Lowell",
    "Milton": "John Milton",
    "Montaigne": "Michel de Montaigne",
    "Pascal": "Blaise Pascal",
    "Pope": "Alexander Pope",
    "Pub. Syr.": "Publilius Syrus",
    "Ruskin": "John Ruskin",
    "Schill.": "Friedrich Schiller",
    "Sen.": "Seneca",
    "Seneca": "Seneca",
    "Soph.": "Sophocles",
    "Spenser": "Edmund Spenser",
    "Swift": "Jonathan Swift",
    "Thackeray": "William Makepeace Thackeray",
    "Virg.": "Virgil",
    "Victor Hugo": "Victor Hugo",
    "Wordsworth": "William Wordsworth",
}

SHAKESPEARE_SOURCES = re.compile(
    r"^(?:Ham|Macb|Rom\. and Jul|Hen\.|Love's L\. Lost|As You Like It|Temp|Othel|King Lear|Much Ado|Twelfth Night|Merchant of Venice|Measure for Measure|Richard|Coriolanus|Cymbeline|Winter's Tale|Midsummer|Taming of the Shrew)",
    re.I,
)

REJECT_SOURCE = re.compile(
    r"\b(?:Pr\.|Proverb|M\.|Motto|Max\.|L\.|Law|Old Play|Unknown|Anon|Scripture|Bible|Ecclus|St\.)\b",
    re.I,
)

BLOCK = (
    r"\b(?:god|christ|jesus|heaven|hell|devil|satan|church|prayer|pray|sin|salvation|bible)\b",
    r"\b(?:king|queen|prince|princess|duke|earl|majesty)\b",
    r"\b(?:kill|slay|murder|blood|sword|dagger|battle|war|foe|enemy)\b",
    r"\b(?:wine|ale|drunk|tobacco)\b",
    r"\b(?:negro|savage|slave|slavery)\b",
    r"\b(?:women are|woman is|wives|wife's|husbands?)\b",
)


def normalize(text: str) -> str:
    text = unicodedata.normalize("NFKC", text)
    text = text.replace("\u00a0", " ")
    text = re.sub(r"\(_lit\._[^)]*\)", "", text, flags=re.I)
    text = text.replace("=", "")
    text = SPACE_RE.sub(" ", text).strip(" -—\t\r\n")
    return text


def word_count(text: str) -> int:
    return len(WORD_RE.findall(text))


def normalize_author(source: str) -> str | None:
    source = SPACE_RE.sub(" ", source).strip()
    source = re.sub(r",?\s+[ivxlcdm]+(?:\.\s*\d+)?\.?$", "", source, flags=re.I)
    if SHAKESPEARE_SOURCES.match(source):
        return "William Shakespeare"
    if REJECT_SOURCE.search(source):
        return None
    if source in AUTHOR_EXPANSIONS:
        return AUTHOR_EXPANSIONS[source]
    source = source.strip(" .,-")
    if not source or len(source) > 60 or any(ch.isdigit() for ch in source):
        return None
    if len(source.split()) > 6:
        return None
    return source


def english_side(raw: str) -> str:
    # Wood frequently prints a foreign original followed by -- and an English rendering.
    if "--" in raw:
        left, right = raw.split("--", 1)
        right_clean = normalize(right)
        english_markers = re.search(r"\b(?:the|a|an|to|of|is|are|be|man|one|we|you|it|not|good|life|truth)\b", right_clean, re.I)
        if english_markers and word_count(right_clean) >= 4:
            return right_clean
    return normalize(raw)


def category_scores(text: str) -> dict[str, int]:
    lower = text.lower()
    scores = {}
    for category, stems in CATEGORIES.items():
        score = sum(1 for stem in stems if stem in lower)
        if score:
            scores[category] = score
    return scores


def choose_category(scores: dict[str, int]) -> str | None:
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
    if 6 <= wc <= 20:
        score += 6
    elif wc <= 30:
        score += 4
    elif wc <= 40:
        score += 2
    score += min(5, sum(scores.values()))
    score += min(3, len(scores))
    lower = text.lower()
    if re.search(r"\b(?:better|must|should|can|cannot|truth|wisdom|courage|hope|duty|learn|work|life|friend|self|reason|experience)\b", lower):
        score += 2
    if re.search(r"\b(?:is|are|makes|becomes|teaches|requires|gives|keeps)\b", lower):
        score += 1
    if re.search(r"\b(?:always|never|everything|nothing|everybody|nobody)\b", lower):
        score -= 1
    if re.search(r"\b(?:I|me|my|mine)\b", text):
        score -= 1
    return score


def iter_entries(text: str):
    marker = "DICTIONARY OF QUOTATIONS."
    pos = text.find(marker)
    if pos >= 0:
        text = text[pos + len(marker):]
    for para in re.split(r"\n\s*\n", text):
        if "=" not in para:
            continue
        lines = [PAGE_RE.sub("", line.rstrip()) for line in para.splitlines()]
        joined = SPACE_RE.sub(" ", " ".join(lines)).strip()
        sources = SOURCE_RE.findall(joined)
        if not sources:
            continue
        source = sources[-1]
        quote_part = joined[: joined.rfind("_" + source + "_")].strip()
        quote_part = re.sub(r"\s+_?[ivxlcdm]+\.?(?:\s*\d+)?\.?$", "", quote_part, flags=re.I)
        yield quote_part, source


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input_text", type=Path)
    parser.add_argument("output_dir", type=Path)
    parser.add_argument("--minimum-score", type=int, default=10)
    args = parser.parse_args()

    args.output_dir.mkdir(parents=True, exist_ok=True)
    raw = args.input_text.read_text(encoding="utf-8", errors="replace")
    records = []
    rejections = Counter()
    categories = Counter()
    authors = Counter()
    seen = set()

    for raw_quote, raw_source in iter_entries(raw):
        author = normalize_author(raw_source)
        if not author:
            rejections["unresolved_or_non_author_source"] += 1
            continue
        text = english_side(raw_quote)
        wc = word_count(text)
        if wc < 5:
            rejections["too_short"] += 1
            continue
        if wc > 40 or len(text) > 330:
            rejections["too_long"] += 1
            continue
        if "?" in text or text.endswith((",", ":", ";", "—")):
            rejections["context_or_fragment"] += 1
            continue
        if any(re.search(pattern, text, re.I) for pattern in BLOCK):
            rejections["blocked_context"] += 1
            continue
        if re.search(r"https?://|www\.|project gutenberg", text, re.I):
            rejections["metadata"] += 1
            continue
        key = re.sub(r"[^a-z0-9]+", "", unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode().lower())
        if not key or key in seen:
            rejections["duplicate"] += 1
            continue
        scores = category_scores(text)
        primary = choose_category(scores)
        if not primary:
            rejections["no_category_signal"] += 1
            continue
        score = quality_score(text, scores)
        if score < args.minimum_score:
            rejections["low_quality_score"] += 1
            continue
        seen.add(key)
        records.append({
            "text": text,
            "author": author,
            "source": raw_source,
            "classification": primary,
            "candidate_categories": [c for c in CATEGORY_PRIORITY if c in scores],
            "score": score,
            "release_status": "wood_aphoristic_source_needs_product_review",
        })

    records.sort(key=lambda row: (-row["score"], word_count(row["text"]), row["author"], row["text"]))
    out = args.output_dir / "wood_candidates.jsonl"
    with out.open("w", encoding="utf-8") as handle:
        for row in records:
            handle.write(json.dumps(row, ensure_ascii=False, separators=(",", ":")) + "\n")
            categories[row["classification"]] += 1
            authors[row["author"]] += 1

    report = args.output_dir / "wood_report.md"
    lines = [
        "# Wood quotation candidate report",
        "",
        "Source: Project Gutenberg #48105, James Wood's *Dictionary of Quotations* (1893).",
        "",
        f"- Candidates retained: **{len(records):,}**",
        f"- Distinct authors/source labels: **{len(authors):,}**",
        "",
        "> Wood explicitly selected mostly context-independent, aphoristic quotations. Candidates still receive product-quality and diversity review before shipping.",
        "",
        "## Categories",
        "",
    ]
    for cat in CATEGORY_PRIORITY:
        lines.append(f"- `{cat}`: {categories[cat]:,}")
    lines += ["", "## Top authors", ""]
    for author, count in authors.most_common(30):
        lines.append(f"- {author}: {count:,}")
    lines += ["", "## Mechanical rejections", ""]
    for reason, count in rejections.most_common():
        lines.append(f"- `{reason}`: {count:,}")
    report.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print(json.dumps({
        "candidates": len(records),
        "categories": dict(categories),
        "top_authors": dict(authors.most_common(20)),
        "rejections": dict(rejections),
    }, indent=2, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
