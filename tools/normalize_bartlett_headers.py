#!/usr/bin/env python3

import re
import sys
from pathlib import Path

# Bartlett sometimes inserts a page/footnote marker between the author's name
# and dates, e.g. `WILLIAM WORDSWORTH.[465-1] 1770-1850.`.  The marker is not
# part of the name and can cause a section parser to miss the author boundary.
HEADER_WITH_MARKER = re.compile(
    r"^([A-Z][A-Z .,'&-]+\.)(?:\[\d+(?:-\d+)?\])([ \t]+(?:\d{1,4}|----))"
)


def main() -> int:
    if len(sys.argv) != 3:
        print("usage: normalize_bartlett_headers.py INPUT OUTPUT", file=sys.stderr)
        return 2

    src = Path(sys.argv[1])
    dst = Path(sys.argv[2])
    replacements = 0
    output = []

    for raw_line in src.read_text(encoding="utf-8", errors="replace").splitlines(keepends=True):
        line, count = HEADER_WITH_MARKER.subn(r"\1\2", raw_line)
        replacements += count
        output.append(line)

    dst.write_text("".join(output), encoding="utf-8")
    print(f"Normalized Bartlett author headers: {replacements} marker(s) removed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
