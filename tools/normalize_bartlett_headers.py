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
# The curation parser only needs a stable author boundary and a conservative
# death-year number, so normalize the era labels out of heading lines only.
LIKELY_AUTHOR_HEADER = re.compile(r"^[A-Z][A-Z .,'&-]+\.\s+.*\d.*$")
ERA_MARKER = re.compile(r"\s+(?:B|A)\.\s*D?C?\.?")
# Explicit forms are safer than relying on the permissive helper above.
ERA_FORMS = (
    re.compile(r"\s+B\.\s*C\."),
    re.compile(r"\s+A\.\s*D\."),
)


def normalize_header(line: str) -> tuple[str, int]:
    changes = 0
    line, count = HEADER_WITH_MARKER.subn(r"\1\2", line)
    changes += count

    if LIKELY_AUTHOR_HEADER.match(line):
        for pattern in ERA_FORMS:
            line, count = pattern.subn("", line)
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
    print(f"Normalized Bartlett author headers: {replacements} marker/era token(s) removed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
