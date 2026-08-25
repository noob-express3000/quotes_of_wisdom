#!/usr/bin/env python3

import json
import re
import sys
import unicodedata
from pathlib import Path

ALLOWED_CATEGORIES = {
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
}

OUTER_QUOTE_PAIRS = (
    ("“", "”"),
    ('"', '"'),
    ("‘", "’"),
    ("'", "'"),
)

WORD_RE = re.compile(r"\b[\w’'-]+\b", re.UNICODE)
PUNCT_RE = re.compile(r"[^\w]+", re.UNICODE)
SPACE_RE = re.compile(r"\s+")


def normalized_key(text: str) -> str:
    folded = unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode()
    return PUNCT_RE.sub("", folded.lower())


def fail(errors: list[str], message: str) -> None:
    errors.append(message)


def main() -> int:
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("app/src/main/assets/quotes.json")
    errors: list[str] = []

    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:
        print(f"ERROR: cannot parse {path}: {exc}")
        return 1

    if not isinstance(data, list):
        print("ERROR: top-level quote database must be a JSON array")
        return 1

    if not data:
        fail(errors, "database must contain at least one quote")

    ids: set[int] = set()
    text_keys: set[str] = set()

    for index, item in enumerate(data):
        where = f"row {index + 1}"
        if not isinstance(item, dict):
            fail(errors, f"{where}: entry must be an object")
            continue

        expected = {"id", "text", "author", "classification"}
        actual = set(item)
        if actual != expected:
            fail(errors, f"{where}: schema must be exactly {sorted(expected)}, found {sorted(actual)}")

        quote_id = item.get("id")
        text = item.get("text")
        author = item.get("author")
        category = item.get("classification")

        if not isinstance(quote_id, int) or isinstance(quote_id, bool) or quote_id <= 0:
            fail(errors, f"{where}: id must be a positive integer")
        elif quote_id in ids:
            fail(errors, f"{where}: duplicate id {quote_id}")
        else:
            ids.add(quote_id)

        if not isinstance(text, str) or not text.strip():
            fail(errors, f"{where}: text must be a non-empty string")
            continue

        if text != text.strip():
            fail(errors, f"{where}: text has leading/trailing whitespace")
        if SPACE_RE.search(text) and "  " in text:
            fail(errors, f"{where}: text contains repeated spaces")
        if "\n" in text or "\r" in text or "\t" in text:
            fail(errors, f"{where}: text must be single-line")

        for left, right in OUTER_QUOTE_PAIRS:
            if len(text) >= 2 and text.startswith(left) and text.endswith(right):
                fail(errors, f"{where}: decorative outer quotation marks are forbidden")
                break

        words = len(WORD_RE.findall(text))
        if words < 4:
            fail(errors, f"{where}: quote is too short ({words} words)")
        if words > 45:
            fail(errors, f"{where}: quote exceeds 45-word production maximum ({words} words)")
        if len(text) > 360:
            fail(errors, f"{where}: quote exceeds 360-character production maximum")

        key = normalized_key(text)
        if key in text_keys:
            fail(errors, f"{where}: duplicate quote text after normalization")
        elif key:
            text_keys.add(key)

        if not isinstance(author, str) or not author.strip():
            fail(errors, f"{where}: author must be a non-empty string")
        elif author != author.strip():
            fail(errors, f"{where}: author has leading/trailing whitespace")

        if category not in ALLOWED_CATEGORIES:
            fail(errors, f"{where}: unsupported classification {category!r}")

    if ids:
        expected_ids = set(range(1, len(data) + 1))
        if ids != expected_ids:
            missing = sorted(expected_ids - ids)
            extra = sorted(ids - expected_ids)
            fail(errors, f"IDs must be contiguous 1..{len(data)}; missing={missing}, out_of_range={extra}")

    if errors:
        print(f"Production quote validation FAILED with {len(errors)} issue(s):")
        for error in errors:
            print(f"- {error}")
        return 1

    category_counts = {}
    for row in data:
        category_counts[row["classification"]] = category_counts.get(row["classification"], 0) + 1

    print(f"Production quote validation passed: {len(data)} quotes")
    for category in sorted(category_counts):
        print(f"- {category}: {category_counts[category]}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
