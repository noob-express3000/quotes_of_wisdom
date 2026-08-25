#!/usr/bin/env python3

import argparse
import json
from collections import Counter, defaultdict
from pathlib import Path

# This is a prioritization list, not a legal declaration that every work/translation
# by an author is public domain everywhere. Final inclusion remains work/source-specific.
ENGLISH_PUBLIC_DOMAIN_PRIORITY = {
    "Abraham Lincoln",
    "Alexander Pope",
    "Alfred Tennyson",
    "Anne Bradstreet",
    "Benjamin Franklin",
    "Booker T. Washington",
    "Charles Dickens",
    "Edgar Allan Poe",
    "Emily Dickinson",
    "Francis Bacon",
    "Frederick Douglass",
    "George Eliot",
    "George Washington",
    "Henry David Thoreau",
    "Herman Melville",
    "Jane Austen",
    "John Ruskin",
    "John Stuart Mill",
    "Mark Twain",
    "Mary Shelley",
    "Nathaniel Hawthorne",
    "Oscar Wilde",
    "Ralph Waldo Emerson",
    "Robert Louis Stevenson",
    "Samuel Johnson",
    "Theodore Roosevelt",
    "Thomas Jefferson",
    "Walt Whitman",
    "William James",
    "William Shakespeare",
    "William Wordsworth",
}

# The original authors are old/public-domain candidates, but the exact English wording
# may come from a modern translation. These are kept in a separate, lower-priority lane.
TRANSLATION_VERIFICATION_PRIORITY = {
    "Aristotle",
    "Confucius",
    "Epictetus",
    "Fyodor Dostoevsky",
    "Johann Wolfgang von Goethe",
    "Leo Tolstoy",
    "Marcus Aurelius",
    "Michel de Montaigne",
    "Plato",
    "Seneca",
    "Socrates",
    "Victor Hugo",
    "Voltaire",
}

HARD_REVIEW_FLAGS = {
    "absolute_language_review",
    "outcome_claim_review",
    "causal_claim_review",
}

CATEGORY_ORDER = (
    "discipline", "resilience", "courage", "growth", "learning", "focus", "work",
    "self-mastery", "perspective", "purpose", "hope", "relationships",
)


def base_author(author: str) -> str:
    return author.split(",", 1)[0].strip()


def lane_for(author: str) -> str | None:
    base = base_author(author)
    if base in ENGLISH_PUBLIC_DOMAIN_PRIORITY:
        return "english_public_domain_priority"
    if base in TRANSLATION_VERIFICATION_PRIORITY:
        return "translation_verification_required"
    return None


def queue_score(record: dict, lane: str) -> int:
    score = int(record.get("score", 0)) * 10
    flags = set(record.get("review_flags", []))

    if lane == "english_public_domain_priority":
        score += 20
    else:
        score += 5

    if "author_field_includes_source_or_title" in flags:
        # A source title can help verification even though it is not proof.
        score += 4
    if "first_person" in flags:
        score -= 3

    return score


def main() -> None:
    parser = argparse.ArgumentParser(description="Build a small attribution/rights verification queue")
    parser.add_argument("input_jsonl", type=Path)
    parser.add_argument("output_dir", type=Path)
    parser.add_argument("--max-per-category", type=int, default=15)
    parser.add_argument("--minimum-score", type=int, default=8)
    args = parser.parse_args()

    args.output_dir.mkdir(parents=True, exist_ok=True)
    output_jsonl = args.output_dir / "verification_queue.jsonl"
    output_md = args.output_dir / "verification_queue.md"

    eligible = []
    input_count = 0
    excluded_flags = 0
    excluded_author = 0
    excluded_score = 0

    with args.input_jsonl.open("r", encoding="utf-8") as source:
        for line in source:
            if not line.strip():
                continue
            input_count += 1
            record = json.loads(line)
            lane = lane_for(str(record.get("author", "")))
            if lane is None:
                excluded_author += 1
                continue
            if int(record.get("score", 0)) < args.minimum_score:
                excluded_score += 1
                continue
            flags = set(record.get("review_flags", []))
            if flags & HARD_REVIEW_FLAGS:
                excluded_flags += 1
                continue

            record["verification_lane"] = lane
            record["release_status"] = "verification_queue_not_release_approved"
            record["verification_priority"] = queue_score(record, lane)
            eligible.append(record)

    eligible.sort(
        key=lambda r: (
            -int(r["verification_priority"]),
            CATEGORY_ORDER.index(r["classification"]) if r["classification"] in CATEGORY_ORDER else 999,
            base_author(r["author"]).lower(),
            int(r["source_index"]),
        )
    )

    per_category = Counter()
    selected = []
    for record in eligible:
        category = record["classification"]
        if per_category[category] >= args.max_per_category:
            continue
        selected.append(record)
        per_category[category] += 1

    with output_jsonl.open("w", encoding="utf-8") as out:
        for record in selected:
            out.write(json.dumps(record, ensure_ascii=False, separators=(",", ":")) + "\n")

    lane_counts = Counter(r["verification_lane"] for r in selected)
    author_counts = Counter(base_author(r["author"]) for r in selected)

    lines = [
        "# Quote attribution / rights verification queue",
        "",
        f"- High-precision input: **{input_count:,}**",
        f"- Historical-author eligible before category cap: **{len(eligible):,}**",
        f"- Selected for active verification: **{len(selected):,}**",
        f"- English public-domain-priority lane: **{lane_counts['english_public_domain_priority']:,}**",
        f"- Translation-verification lane: **{lane_counts['translation_verification_required']:,}**",
        "",
        "> This queue is NOT a shipping database. Every line needs an exact source/attribution check and a work-specific rights check before receiving a production ID.",
        "",
        "## Queue",
        "",
    ]

    for category in CATEGORY_ORDER:
        category_records = [r for r in selected if r["classification"] == category]
        if not category_records:
            continue
        lines += [f"### {category}", ""]
        for record in category_records:
            flags = ", ".join(record.get("review_flags", [])) or "none"
            lines.append(
                f"- **{record['author']}** — {record['text']}  "
            )
            lines.append(
                f"  lane={record['verification_lane']}; priority={record['verification_priority']}; flags={flags}; source_index={record['source_index']}"
            )
        lines.append("")

    lines += ["## Most represented authors", ""]
    for author, count in author_counts.most_common(20):
        lines.append(f"- {author}: {count}")

    output_md.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print(json.dumps({
        "high_precision_input": input_count,
        "historical_author_eligible": len(eligible),
        "selected_for_verification": len(selected),
        "lanes": dict(lane_counts),
        "category_counts": dict(per_category),
        "excluded_nonpriority_author": excluded_author,
        "excluded_review_flags": excluded_flags,
        "excluded_score": excluded_score,
    }, indent=2))


if __name__ == "__main__":
    main()
