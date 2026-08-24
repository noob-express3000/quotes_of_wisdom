# Quotes of Wisdom — Quote Curation Policy

## Goal

The production corpus should feel grounded rather than cheesy. Every retained quote must be:

1. **Realistic** — compatible with ordinary life; no magical guarantees, deterministic promises, or toxic positivity.
2. **Truthful** — not demonstrably false, misleading, pseudoscientific, or presented as a universal fact when it is merely wishful thinking.
3. **Motivational** — should encourage constructive action, endurance, learning, perspective, responsibility, or healthy connection.
4. **Classifiable** — must fit at least one approved category below.
5. **Attribution-safe** — attribution must be credible enough to ship. Entries marked or suspected as misattributed are rejected unless independently verified.
6. **Redistribution-safe** — the project must have a defensible right to include the text in a public, monetized app/repository.

Fail any required rule -> reject.

## Controlled categories

Each quote receives one primary `classification` from this list:

- `discipline`
- `resilience`
- `courage`
- `growth`
- `learning`
- `focus`
- `work`
- `self-mastery`
- `perspective`
- `purpose`
- `hope`
- `relationships`

The taxonomy can be expanded deliberately later, but raw source tags are not copied directly into the app.

## Automatic rejection signals

Reject by default when an entry is:

- `attributed-no-source`, `widely-misattributed`, or otherwise attribution-uncertain;
- dialogue/fragments that only make sense inside a novel, film, or scene;
- primarily romantic, sexual, celebrity gossip, partisan, sectarian, or promotional content without broader constructive value;
- an empty platitude (for example, promising success merely for believing);
- advice based on magical thinking, pseudoscience, guaranteed destiny, or false certainty;
- demeaning, hateful, coercive, abusive, or glorifying self-destruction;
- malformed, duplicated, badly encoded, or obviously scraped incorrectly;
- so long that it becomes an excerpt rather than a concise quote.

## Length and display

- Prefer concise entries that work well visually and with TTS.
- Target roughly 6–45 words.
- Longer entries require unusually strong value and clean readability.
- The stored `text` contains the quote itself without decorative opening/closing quotation marks; the bordered UI provides the framing.

## Schema

Keep the app schema simple:

```json
{
  "id": 1,
  "text": "Quote text",
  "author": "Author",
  "classification": "resilience"
}
```

IDs are stable integers assigned only after an entry passes final review.

## Source: jstet/quotes-500k

The Hugging Face dataset is a useful **candidate/discovery pool**, not an automatic redistribution source.

Its card traces the data to `ShivaliGoel/Quotes-500K`, whose README says the corpus was scraped from multiple quote websites and made available for educational/research purposes. No repository `LICENSE` file was found during our review. Because Quotes of Wisdom is intended to be public and monetized, we do not blindly copy the dataset into the app.

Use this dataset to discover candidate quotes/categories. Before final inclusion, prefer one of these routes:

1. independently verify the quote in a public-domain source/work;
2. use text with a clear redistribution license compatible with the app;
3. use original Quotes of Wisdom material that we own.

For public-domain candidates, favor old works/authors and verify the actual source text rather than trusting scraper attribution.

## Review pipeline

```text
raw candidate
    -> normalize whitespace/punctuation
    -> reject malformed/duplicate material
    -> realism check
    -> truthfulness check
    -> motivational-value check
    -> category assignment
    -> attribution verification
    -> rights/source verification
    -> final corpus
```

Maintain rejected/uncertain candidates outside the shipping `quotes.json` so questionable material never silently returns to production.
