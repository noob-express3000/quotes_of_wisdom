#!/usr/bin/env python3

import argparse
import difflib
import json
import re
import unicodedata
from collections import Counter, defaultdict
from pathlib import Path

WORD_RE = re.compile(r"\b[\w’'-]+\b", re.UNICODE)

CATEGORY_PATTERNS = {
    "discipline": (r"\bdisciplin\w*", r"\bdut(?:y|ies)\b", r"\bhabit\w*", r"\bpatien\w*", r"\bpersever\w*", r"\bpersist\w*", r"\bdiligen\w*", r"\bpractice\b", r"\brestraint\b", r"\bsteadfast\w*", r"\bconsisten\w*", r"\bself[- ]control\b"),
    "resilience": (r"\badvers\w*", r"\bstruggl\w*", r"\bendure\w*", r"\bendurance\b", r"\bhardship\w*", r"\bsuffer\w*", r"\bfail(?:ure|ed|ing)?\b", r"\bdefeat\w*", r"\bmisfortune\w*", r"\btrial\w*", r"\bgrief\b", r"\bsorrow\w*", r"\bpain\w*", r"\brecover\w*"),
    "courage": (r"\bcourage\w*", r"\bbrav\w*", r"\bfear\w*", r"\bdanger\w*", r"\bdar(?:e|ing|ed)\b", r"\bbold\w*", r"\bvalou?r\b", r"\bfortitude\b"),
    "growth": (r"\bgrow\w*", r"\bchange\w*", r"\bimprov\w*", r"\bprogress\w*", r"\bexperience\w*", r"\bbecom\w*", r"\bdevelop\w*"),
    "learning": (r"\blearn\w*", r"\bknowledge\b", r"\bwisdom\b", r"\bwise\b", r"\btruth\b", r"\breason\b", r"\bstud(?:y|ies|ied)\b", r"\bbooks?\b", r"\bthought\w*", r"\bunderstand\w*", r"\bignor\w*", r"\bmind\b", r"\bintelligen\w*"),
    "focus": (r"\battention\b", r"\bfocus\w*", r"\bheed\b", r"\bobserv\w*", r"\bwatch\w*", r"\bpresent\b", r"\bconcentrat\w*", r"\bresolve\w*"),
    "work": (r"\bwork\w*", r"\blabou?r\w*", r"\beffort\w*", r"\bdeeds?\b", r"\baction\w*", r"\bindustr\w*", r"\btasks?\b", r"\bpractice\b", r"\btoil\w*"),
    "self-mastery": (r"\bself(?:[- ]\w+)?\b", r"\bcharacter\b", r"\btemper\w*", r"\bcontrol\w*", r"\bcommand\w*", r"\bconscience\b", r"\bintegrity\b", r"\bhonou?r\b", r"\bwill\b", r"\bpassion\w*", r"\bmaster\w*", r"\bdesire\w*"),
    "perspective": (r"\blife\b", r"\btime\b", r"\bfortune\b", r"\bhappiness\b", r"\bjoy\b", r"\bsorrow\w*", r"\bnature\b", r"\bhuman\w*", r"\breality\b", r"\bworld\b", r"\bdeath\b"),
    "purpose": (r"\bpurpose\b", r"\baims?\b", r"\bgoals?\b", r"\bambition\w*", r"\bmeaning\b", r"\bmission\b", r"\bcalling\b", r"\bdirection\b"),
    "hope": (r"\bhope\w*", r"\bfuture\b", r"\bbetter\b", r"\bbegin\w*", r"\bdawn\b", r"\bpossib\w*", r"\bdespair\b", r"\blight\b"),
    "relationships": (r"\bfriends?\b", r"\bfriendship\b", r"\bkind\w*", r"\bcompassion\b", r"\btrust\w*", r"\bheart\b", r"\baffection\b", r"\bsympath\w*", r"\brespect\w*", r"\blove\w*"),
}
CATEGORY_ORDER = tuple(CATEGORY_PATTERNS)

ABBREVIATIONS = {
    "Apul": "Apuleius", "Arist": "Aristotle", "Aul. Gell": "Aulus Gellius",
    "Cæs": "Julius Caesar", "Catull": "Catullus", "Cic": "Cicero",
    "Claud": "Claudian", "Corn": "Pierre Corneille", "Curt": "Quintus Curtius Rufus",
    "Eurip": "Euripides", "Hom": "Homer", "Hor": "Horace", "Just": "Justinian",
    "Juv": "Juvenal", "Laber": "Laberius", "La Font": "Jean de La Fontaine",
    "La Roche": "François de La Rochefoucauld", "Liv": "Livy", "Luc": "Lucan",
    "Lucr": "Lucretius", "Lucret": "Lucretius", "Macrob": "Macrobius",
    "Mart": "Martial", "Mol": "Molière", "Per": "Persius", "Petron": "Petronius",
    "Phæd": "Phaedrus", "Phædr": "Phaedrus", "Plaut": "Plautus",
    "Pub. Syr": "Publilius Syrus", "Quinct": "Quintilian", "Sall": "Sallust",
    "Schill": "Friedrich Schiller", "Sen": "Seneca", "Sh": "William Shakespeare",
    "Soph": "Sophocles", "Stat": "Statius", "Sueton": "Suetonius", "Tac": "Tacitus",
    "Ter": "Terence", "Tibull": "Tibullus", "Virg": "Virgil",
}

FULL_NAMES = {
    "Bacon": "Francis Bacon", "Burke": "Edmund Burke", "Burns": "Robert Burns",
    "Carlyle": "Thomas Carlyle", "Cervantes": "Miguel de Cervantes",
    "Coleridge": "Samuel Taylor Coleridge", "Cowper": "William Cowper",
    "Dante": "Dante Alighieri", "Dryden": "John Dryden", "Emerson": "Ralph Waldo Emerson",
    "Franklin": "Benjamin Franklin", "Ben. Franklin": "Benjamin Franklin",
    "Goethe": "Johann Wolfgang von Goethe", "Goldsmith": "Oliver Goldsmith",
    "Hume": "David Hume", "Johnson": "Samuel Johnson", "Longfellow": "Henry Wadsworth Longfellow",
    "Lowell": "James Russell Lowell", "Milton": "John Milton", "Montaigne": "Michel de Montaigne",
    "Pascal": "Blaise Pascal", "Pope": "Alexander Pope", "Ruskin": "John Ruskin",
    "Schiller": "Friedrich Schiller", "Scott": "Walter Scott", "Swift": "Jonathan Swift",
    "Thoreau": "Henry David Thoreau", "Victor Hugo": "Victor Hugo",
    "Wm. Penn": "William Penn", "Wordsworth": "William Wordsworth",
    "Young": "Edward Young", "Diderot": "Denis Diderot", "Rousseau": "Jean-Jacques Rousseau",
    "Schopenhauer": "Arthur Schopenhauer", "Kant": "Immanuel Kant",
    "La Bruyère": "Jean de La Bruyère", "Macaulay": "Thomas Babington Macaulay",
    "Addison": "Joseph Addison", "Hazlitt": "William Hazlitt",
    "A. B. Alcott": "Amos Bronson Alcott", "Mrs. Carlyle": "Jane Welsh Carlyle",
}

GENERIC_SOURCE = re.compile(
    r"(?:^|[ .])(?:Pr|Proverb|M|Motto|Max|Saying|Old Play|Lit\.? Col|Law|Anon|Unknown)(?:$|[ .])",
    re.I,
)
RELIGIOUS_SOURCE = re.compile(
    r"(?:Apocrypha|Jesus|Scripture|Bible|St\.? John|St\.? Paul|St\.? Aug|Thomas à Kempis|Tertullian)",
    re.I,
)
SHAKESPEARE_SOURCE = re.compile(
    r"(?:Ham|Macb|Rom\.? and Jul|Hen\.|Love'?s L|Two Gent|As You Like|Temp|Othel|King Lear|Much Ado|Twelfth|Merchant|Measure|Richard|Coriolanus|Cymbeline|Winter'?s Tale|Midsummer|Taming)",
    re.I,
)
BLOCKED_TEXT = (
    r"\b(?:god|christ|jesus|heaven|hell|devil|satan|church|prayer|pray|sin|salvation|bible|gospel|saints?|providence|divine)\b",
    r"\b(?:king|queen|prince|princess|duke|earl|majesty|politic\w*)\b",
    r"\b(?:kill|slay|murder|blood|sword|dagger|battle|war|foe|enemy)\b",
    r"\b(?:wine|ale|drunk|tobacco)\b",
    r"\b(?:negro|savage|slave|slavery)\b",
    r"\b(?:women|woman|wives|wife|husband|sex)\b",
    r"\bfalsehood\b.{0,35}\bcommendable\b",
    r"\bbetter not to live\b|\bnot to live at all\b",
)
ALLOWED_MID_CAPS = {"Nature", "Truth", "Life", "Fortune", "Love", "Time", "Wisdom", "Reason", "Duty", "Hope", "Friendship"}


def normalize_key(text: str) -> str:
    folded = unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode().lower()
    folded = re.sub(r"^(?:a|an|the)\s+", "", folded)
    return re.sub(r"[^a-z0-9]+", "", folded)


def word_count(text: str) -> int:
    return len(WORD_RE.findall(text))


def resolve_author(source: str, candidate_author: str) -> str | None:
    source = re.sub(r",\s*[ivxlcdm]+\.?\s*\d+.*$", "", source.strip(" ."), flags=re.I)
    if GENERIC_SOURCE.search(source) or RELIGIOUS_SOURCE.search(source):
        return None
    if SHAKESPEARE_SOURCE.search(source):
        return "William Shakespeare"
    base = source.strip(" .")
    if base in ABBREVIATIONS:
        return ABBREVIATIONS[base]
    if base.rstrip(".") in ABBREVIATIONS:
        return ABBREVIATIONS[base.rstrip(".")]
    author = FULL_NAMES.get(base, FULL_NAMES.get(candidate_author, candidate_author.strip(" .")))
    if GENERIC_SOURCE.search(author) or RELIGIOUS_SOURCE.search(author):
        return None
    if "," in author and author != "François de La Rochefoucauld":
        return None
    if re.fullmatch(r"(?:[A-Z]\.?\s*){1,4}", author):
        return None
    if len(author) < 3 or len(author) > 60 or any(ch.isdigit() for ch in author):
        return None
    return author


def category_scores(text: str) -> dict[str, int]:
    lower = text.lower()
    scores = {}
    for category, patterns in CATEGORY_PATTERNS.items():
        score = sum(1 for pattern in patterns if re.search(pattern, lower))
        if score:
            scores[category] = score
    return scores


def release_score(row: dict) -> int:
    text = row["text"]
    lower = text.lower()
    wc = word_count(text)
    score = int(row.get("score", 0))
    if 6 <= wc <= 20:
        score += 2
    elif wc <= 28:
        score += 1
    else:
        score -= 1
    if re.search(r"\b(?:is|are|makes|becomes|teaches|requires|gives|keeps|must|should|can|cannot|better|best)\b", lower):
        score += 1
    if re.search(r"\b(?:I|me|my|mine)\b", text):
        score -= 1
    if re.search(r"\b(?:thou|thee|thy|thine|hath|doth|dost)\b", lower):
        score -= 1
    if text.startswith(("And ", "But ", "For ", "Yet ", "Oh ", "O ")):
        score -= 1
    if "!" in text:
        score -= 1
    return score


def release_ok(row: dict) -> tuple[bool, str | None, dict[str, int]]:
    text = row.get("text", "").strip()
    author = resolve_author(row.get("source", ""), row.get("author", ""))
    if not author:
        return False, None, {}
    if int(row.get("score", 0)) < 12 or release_score(row) < 13:
        return False, author, {}
    if word_count(text) < 5 or word_count(text) > 40 or len(text) > 330:
        return False, author, {}
    if "?" in text or text.endswith((",", ":", ";", "—")):
        return False, author, {}
    if any(marker in text for marker in ("...", "…", " / ", "_", "[", "]")):
        return False, author, {}
    if text.count('"') >= 2 or text.count("“") + text.count("”") >= 2:
        return False, author, {}
    if any(re.search(pattern, text, re.I) for pattern in BLOCKED_TEXT):
        return False, author, {}
    capitals = re.findall(r"(?<!^)\b[A-Z][a-z]{2,}\b", text)
    if any(token not in ALLOWED_MID_CAPS for token in capitals):
        return False, author, {}
    scores = category_scores(text)
    if not scores:
        return False, author, {}
    return True, author, scores


def near_duplicate(text: str, previous_keys: list[str]) -> bool:
    key = normalize_key(text)
    for other in previous_keys:
        if key == other:
            return True
        short = min(len(key), len(other))
        long = max(len(key), len(other))
        if short >= 30 and short / long >= 0.85 and difflib.SequenceMatcher(None, key, other).ratio() >= 0.93:
            return True
    return False


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("candidates_jsonl", type=Path)
    parser.add_argument("seed_quotes_json", type=Path)
    parser.add_argument("output_dir", type=Path)
    parser.add_argument("--author-cap", type=int, default=20)
    parser.add_argument("--minimum-release-size", type=int, default=1000)
    args = parser.parse_args()

    args.output_dir.mkdir(parents=True, exist_ok=True)
    candidates = [json.loads(line) for line in args.candidates_jsonl.read_text(encoding="utf-8").splitlines() if line.strip()]
    seeds = json.loads(args.seed_quotes_json.read_text(encoding="utf-8"))

    selected = []
    author_counts = Counter()
    category_counts = Counter()
    per_author_keys = defaultdict(list)
    global_keys = set()

    for seed in seeds:
        key = normalize_key(seed["text"])
        if key in global_keys:
            continue
        global_keys.add(key)
        author_counts[seed["author"]] += 1
        category_counts[seed["classification"]] += 1
        per_author_keys[seed["author"]].append(key)
        selected.append({
            "text": seed["text"],
            "author": seed["author"],
            "classification": seed["classification"],
            "source_collection": "individually_verified_seed",
            "source": "docs/quote-verification-ledger.md",
            "release_score": 99,
        })

    candidates.sort(key=lambda row: (-release_score(row), -int(row.get("score", 0)), word_count(row.get("text", "")), row.get("author", ""), row.get("text", "")))

    for row in candidates:
        ok, author, scores = release_ok(row)
        if not ok or not author:
            continue
        if author_counts[author] >= args.author_cap:
            continue
        text = row["text"].strip()
        key = normalize_key(text)
        if key in global_keys or near_duplicate(text, per_author_keys[author]):
            continue

        best = max(scores.values())
        plausible = [category for category, score in scores.items() if score >= best - 1]
        classification = min(plausible, key=lambda category: (category_counts[category], CATEGORY_ORDER.index(category)))

        global_keys.add(key)
        per_author_keys[author].append(key)
        author_counts[author] += 1
        category_counts[classification] += 1
        selected.append({
            "text": text,
            "author": author,
            "classification": classification,
            "source_collection": "James Wood — Dictionary of Quotations (1893), Project Gutenberg #48105",
            "source": row.get("source", ""),
            "release_score": release_score(row),
        })

    if len(selected) < args.minimum_release_size:
        raise SystemExit(f"production corpus too small: {len(selected)} < {args.minimum_release_size}")

    quotes = [
        {"id": index, "text": row["text"], "author": row["author"], "classification": row["classification"]}
        for index, row in enumerate(selected, start=1)
    ]
    quote_path = args.output_dir / "quotes.json"
    quote_path.write_text(json.dumps(quotes, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")

    ledger_path = args.output_dir / "production-source-ledger.jsonl"
    with ledger_path.open("w", encoding="utf-8") as handle:
        for index, row in enumerate(selected, start=1):
            record = {"id": index, **row}
            handle.write(json.dumps(record, ensure_ascii=False, separators=(",", ":")) + "\n")

    report = args.output_dir / "production-report.md"
    lines = [
        "# Production quote corpus report", "",
        f"- Shipping quotes: **{len(quotes):,}**",
        f"- Distinct authors: **{len(author_counts):,}**",
        f"- Per-author cap: **{args.author_cap}**", "",
        "## Categories", "",
    ]
    for category in CATEGORY_ORDER:
        lines.append(f"- `{category}`: {category_counts[category]:,}")
    lines += ["", "## Most represented authors", ""]
    for author, count in author_counts.most_common(30):
        lines.append(f"- {author}: {count:,}")
    report.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(report.read_text(encoding="utf-8"))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
