#!/usr/bin/env python3

import argparse
import json
import re
import unicodedata
from collections import Counter
from pathlib import Path

AUTHOR_ALIASES = {
    "Shakespeare": "William Shakespeare",
    "Meas. for Meas": "William Shakespeare",
    "Tit. Andron": "William Shakespeare",
    "Jul. Cæs": "William Shakespeare",
    "Tennyson": "Alfred, Lord Tennyson",
    "Lincoln": "Abraham Lincoln",
    "Washington": "George Washington",
    "South": "Robert South",
    "Bp. Patrick": "Simon Patrick",
    "Byron": "Lord Byron",
    "Disraeli": "Benjamin Disraeli",
    "Ward Beecher": "Henry Ward Beecher",
    "Amiel": "Henri-Frédéric Amiel",
    "Bulwer Lytton": "Edward Bulwer-Lytton",
    "Sterne": "Laurence Sterne",
    "Colton": "Charles Caleb Colton",
    "Schiller": "Friedrich Schiller",
    "Schopenhauer": "Arthur Schopenhauer",
    "Wm. Blake": "William Blake",
    "Dickens": "Charles Dickens",
    "Locke": "John Locke",
    "Sir P. Sidney": "Philip Sidney",
    "Earl of Roscommon": "Wentworth Dillon",
    "Carlyle to his mother": "Thomas Carlyle",
    "J. Burroughs": "John Burroughs",
    "J. G. Holland": "Josiah Gilbert Holland",
    "H. Ballou": "Hosea Ballou",
    "D'Urfey": "Thomas d'Urfey",
    "Froude": "James Anthony Froude",
    "Chapin": "Edwin Hubbell Chapin",
    "Bovee": "Christian Nestell Bovee",
    "Rivarol": "Antoine de Rivarol",
    "Seume": "Johann Gottfried Seume",
    "Landor": "Walter Savage Landor",
    "Salis": "Johann Gaudenz von Salis-Seewis",
    "Howell": "James Howell",
    "Prof. Blackie": "John Stuart Blackie",
}

REJECT_AUTHORS = {
    "Junius",
    "Motte",
    "Spectator",
    "Joineriana",
    "Hitopadesa",
    "Paul",
    "Saying",
    "Unknown",
    "Anonymous",
    "Anon",
}

REJECT_AUTHOR_PATTERNS = (
    r"(?:^|\.)\s*Pr\.?$",
    r"\bProverb\b",
    r"\bLit\.?\s*Col\b",
    r"^[A-Z]\.?\s*[A-Z]?\.?$",
    r"^(?:Dr|General|Bp)\.?(?:\s|$)",
)

REJECT_TEXT_PATTERNS = (
    # Sectarian, supernatural, or mythological claims presented as standalone advice.
    r"\b(?:god|gods|goddess|christ|jesus|heaven|hell|devil|satan|church|prayer|pray|sin|salvation|bible|gospel|saint|saints|providence|divine|religion|religious|worship|spiritually|immortality)\b",
    # Gender generalizations are poor universal wisdom and age badly.
    r"\b(?:female|male|women|woman|wives|wife|husband|husbands|sexes?)\b",
    # Product-inappropriate violence, racial language, intoxication, and crime/punishment material.
    r"\b(?:negro|savage|slave|slavery)\b",
    r"\b(?:kill|slay|murder|dagger|bloodshed)\b",
    r"\b(?:wine|ale|drunk|drunken|tobacco)\b",
    r"\b(?:crime|criminal|offender)\b",
    r"\bconquered race\b",
    # Explicitly nihilistic, fatalistic, or death-prescriptive aphorisms.
    r"\bbetter to die\b",
    r"\bdeath is better\b",
    r"\bhonou?rable death is better\b",
    r"\bnot to live(?: at all)?\b",
    r"\bhappiness is a chim[aæ]ra\b",
    r"\bpain is the positive element in life\b",
    r"\bonly error is life\b.*\bknowledge is death\b",
    r"\blife is (?:only |nothing but )?(?:misery|suffering)\b",
    r"\blittle hope of release\b",
    # Claims that depend on obsolete hierarchy or broad stereotypes.
    r"\bsuperiors?\b.{0,40}\brule\b.{0,40}\binferiors?\b",
    r"\bwarlike people\b",
    # Very narrow factual/political/legal material is not the app's purpose.
    r"\b(?:national morality|political party|constitution|legislature|parliament)\b",
)

# Historical editorial artifacts and context fragments that should never reach the UI.
FORBIDDEN_MARKERS = ("...", "…", "_", "[", "]", "(", ")", " / ", "--")


def key(text: str) -> str:
    folded = unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode().lower()
    return re.sub(r"[^a-z0-9]+", "", folded)


def normalize_author(author: str) -> str:
    author = re.sub(r"\s+", " ", author).strip(" .")
    return AUTHOR_ALIASES.get(author, author)


def reject(author: str, text: str) -> str | None:
    if author in REJECT_AUTHORS:
        return "unresolved_or_collection_attribution"
    if any(re.search(pattern, author, re.I) for pattern in REJECT_AUTHOR_PATTERNS):
        return "unresolved_or_collection_attribution"
    if any(marker in text for marker in FORBIDDEN_MARKERS):
        return "editorial_or_context_artifact"
    for pattern in REJECT_TEXT_PATTERNS:
        if re.search(pattern, text, re.I):
            return "product_policy_semantic_rejection"
    return None


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("production_dir", type=Path)
    parser.add_argument("--minimum-release-size", type=int, default=1000)
    parser.add_argument("--author-cap", type=int, default=20)
    parser.add_argument("--minimum-per-category", type=int, default=20)
    args = parser.parse_args()

    quote_path = args.production_dir / "quotes.json"
    ledger_path = args.production_dir / "production-source-ledger.jsonl"

    quotes = json.loads(quote_path.read_text(encoding="utf-8"))
    ledger = [json.loads(line) for line in ledger_path.read_text(encoding="utf-8").splitlines() if line.strip()]
    ledger_by_id = {row["id"]: row for row in ledger}

    kept = []
    kept_ledger = []
    seen = set()
    author_counts = Counter()
    category_counts = Counter()
    rejection_counts = Counter()

    for quote in quotes:
        author = normalize_author(quote["author"])
        text = quote["text"].strip()
        reason = reject(author, text)
        if reason:
            rejection_counts[reason] += 1
            continue

        normalized = key(text)
        if not normalized or normalized in seen:
            rejection_counts["duplicate_after_final_normalization"] += 1
            continue
        if author_counts[author] >= args.author_cap:
            rejection_counts["author_cap_after_alias_normalization"] += 1
            continue

        original_ledger = ledger_by_id.get(quote["id"], {})
        new_id = len(kept) + 1
        kept.append({
            "id": new_id,
            "text": text,
            "author": author,
            "classification": quote["classification"],
        })
        kept_ledger.append({
            **original_ledger,
            "id": new_id,
            "text": text,
            "author": author,
            "classification": quote["classification"],
        })
        seen.add(normalized)
        author_counts[author] += 1
        category_counts[quote["classification"]] += 1

    if len(kept) < args.minimum_release_size:
        raise SystemExit(f"final production corpus too small: {len(kept)} < {args.minimum_release_size}")

    underfilled = {
        category: count
        for category, count in category_counts.items()
        if count < args.minimum_per_category
    }
    expected_categories = {
        "discipline", "resilience", "courage", "growth", "learning", "focus",
        "work", "self-mastery", "perspective", "purpose", "hope", "relationships",
    }
    missing = expected_categories - set(category_counts)
    if missing or underfilled:
        raise SystemExit(f"category coverage failed: missing={sorted(missing)}, underfilled={underfilled}")

    quote_path.write_text(json.dumps(kept, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    with ledger_path.open("w", encoding="utf-8") as handle:
        for row in kept_ledger:
            handle.write(json.dumps(row, ensure_ascii=False, separators=(",", ":")) + "\n")

    report_path = args.production_dir / "finalization-report.md"
    lines = [
        "# Final production semantic pass", "",
        f"- Quotes retained: **{len(kept):,}**",
        f"- Distinct authors: **{len(author_counts):,}**",
        f"- Author cap: **{args.author_cap}**", "",
        "## Categories", "",
    ]
    for category in sorted(expected_categories):
        lines.append(f"- `{category}`: {category_counts[category]:,}")
    lines += ["", "## Final-pass rejections", ""]
    for reason, count in rejection_counts.most_common():
        lines.append(f"- `{reason}`: {count:,}")
    lines += ["", "## Most represented authors", ""]
    for author, count in author_counts.most_common(30):
        lines.append(f"- {author}: {count:,}")
    report_path.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(report_path.read_text(encoding="utf-8"))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
