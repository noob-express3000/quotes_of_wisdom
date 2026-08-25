#!/usr/bin/env python3

import json
import sys
from pathlib import Path

EXPECTED = (
    ("That best portion of a good man's life", "William Wordsworth"),
    ("Self-reverence, self-knowledge, self-control", "Alfred Tennyson"),
    ("Knowledge comes, but wisdom lingers", "Alfred Tennyson"),
    ("The universe is change; our life is what our thoughts make it", "Marcus Aurelius Antoninus"),
)


def main() -> int:
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("build/bartlett-curation/bartlett_candidates.jsonl")
    rows = [json.loads(line) for line in path.read_text(encoding="utf-8").splitlines() if line.strip()]
    errors = []

    for needle, expected_author in EXPECTED:
        matches = [row for row in rows if needle.lower() in row.get("text", "").lower()]
        if not matches:
            errors.append(f"missing regression quote: {needle!r}")
            continue
        authors = sorted({row.get("author") for row in matches})
        if expected_author not in authors:
            errors.append(
                f"wrong author for {needle!r}: expected {expected_author!r}, found {authors!r}"
            )

    if errors:
        print("Bartlett attribution regression check FAILED:")
        for error in errors:
            print(f"- {error}")
        return 1

    print("Bartlett attribution regression check passed")
    for needle, expected_author in EXPECTED:
        print(f"- {expected_author}: {needle}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
