#!/usr/bin/env python3

import re
import sys
from pathlib import Path

# Bartlett sometimes inserts a page/footnote marker between the author's name
# and dates, e.g. `WILLIAM WORDSWORTH.[465-1] 1770-1850.`.
HEADER_WITH_MARKER = re.compile(
    r"^([A-Z][A-Z .,'&-]+\.)(?:\[\d+(?:-\d+)?\])([ \t]+(?:\d{1,4}|----))"
)

# Ancient-author headings use era markers such as
# `MARCUS AURELIUS ANTONINUS. 121-180 A. D.` or `8 B. C.-65 A. D.`.
# Normalize only the date annotation while preserving the terminal heading dot.
LIKELY_AUTHOR_HEADER = re.compile(r"^[A-Z][A-Z .,'&-]+\.\s+.*\d.*$")
BCE_BEFORE_HYPHEN = re.compile(r"\s+B\.\s*C\.(?=\s*-)")
ERA_AT_END = re.compile(r"\s+(?:A\.\s*D\.|B\.\s*C\.)(?=\s*(?:\r?\n)?$)")


def normalize_header(line: str) -> tuple[str, int]:
    changes = 0
    line, count = HEADER_WITH_MARKER.subn(r"\1\2", line)
    changes += count

    if LIKELY_AUTHOR_HEADER.match(line):
        line, count = BCE_BEFORE_HYPHEN.subn("", line)
        changes += count
        # Replace a terminal era marker with a single period instead of deleting
        # it; the section parser intentionally requires the heading terminator.
        line, count = ERA_AT_END.subn(".", line)
        changes += count

    return line, changes


def main() -> int:
    if len(sys.argv) != 3:
        print("usage: normalize_bartlett_headers.py INPUT OUTPUT", file=sys.stderr)
        return 2

    src = Path(sys.argv[1])
    dst = Path(sys.argv[2])
    replacements = 0
    output = []

    for raw_line in src.read_text(encoding="utf-8", errors="replace").splitlines(keepends=True):
        line, count = normalize_header(raw_line)
        replacements += count
        output.append(line)

    dst.write_text("".join(output), encoding="utf-8")
    print(f"Normalized Bartlett author headers: {replacements} marker/era token(s) normalized")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
