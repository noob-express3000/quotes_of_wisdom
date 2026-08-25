# Quotes of Wisdom — Quote Curation Policy

## Goal

The production corpus should feel grounded, memorable, and worth returning to. Fiction is explicitly allowed: novels, plays, poetry, speeches, letters, essays, and dialogue can all produce valid Quotes of Wisdom material.

Every retained quote must be:

1. **Grounded** — useful or psychologically/emotionally truthful when read on its own. Fiction does not need to be a literal factual claim, but it must not become misleading or nonsensical outside its source scene.
2. **Truthful enough to stand alone** — no demonstrably false factual claim, pseudoscience, magical guarantee, or deceptive certainty presented as reality.
3. **Motivational or insight-bearing** — should encourage constructive action, endurance, learning, perspective, responsibility, courage, purpose, self-mastery, or healthy connection. A line may qualify because it is powerful and clarifying even when it is not cheerful.
4. **Classifiable** — must fit at least one approved category below.
5. **Attribution-safe** — the author attribution must be credible enough to ship. The internal verification ledger may also record the work and character/speaker where useful, while the app schema remains simple and displays the author.
6. **Redistribution-safe** — the project must have a defensible right to include the text in a public, monetized app/repository.

Fail any required rule -> reject.

## Corpus size and browsing model

The corpus is intended for heavy browsing, not merely a once-per-day quote experience.

- **Release minimum: 1,000 production-approved quotes.**
- Prefer roughly **2,000–3,000+** if enough material survives the quality rules.
- There is **no fixed upper cap**. Quality, attribution, rights safety, and usefulness determine whether additional quotes are included.
- Do not dilute the corpus merely to reach a number. The minimum is a release gate; additional approved material is desirable.
- The app may be used for extended or effectively endless quote browsing, so author/category diversity and low repetition matter.
- Selection/build tooling should avoid over-representing one prolific author or category merely because the source corpus contains more material from them.
- The runtime shuffle/no-repeat system should operate over the full production corpus rather than a small daily subset.

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

A quote may reasonably fit several categories; the app stores one primary category for v1.

## Fiction policy

Fiction is first-class source material.

KEEP fictional material when:

- the line makes sense without needing the surrounding scene;
- it delivers a strong insight, challenge, warning, or motivational idea;
- its meaning remains defensible when presented as a standalone quotation;
- the author/source attribution can be established;
- the text is public domain, permissively licensed, or otherwise cleared for redistribution.

REJECT fictional material only when:

- it is a fragment that depends on missing dialogue/context;
- irony, sarcasm, villainous intent, or plot context reverses the apparent meaning;
- it is merely exposition, romance dialogue, or scene-specific chatter with no broader value;
- it fails the same quality, attribution, or rights rules as non-fiction material.

Dialogue is therefore **not automatically rejected** simply because it is dialogue.

## Copyright and attribution

The app displays the author because that is the clean product experience we want. Internally we retain source/work information when available for verification.

Author attribution and copyright permission are separate questions. Crediting an author is good attribution, but it does not by itself grant permission to redistribute a copyrighted passage in a commercial app. Therefore the production corpus prioritizes:

1. public-domain fiction and non-fiction;
2. permissively licensed material compatible with commercial redistribution;
3. original Quotes of Wisdom material that we own.

Modern copyrighted fiction can be considered only if we have a defensible license/permission path. We do not rely on attribution alone as a rights strategy.

## Automatic rejection signals

Reject by default when an entry is:

- `attributed-no-source`, `widely-misattributed`, or otherwise attribution-uncertain;
- a dialogue fragment that cannot stand on its own;
- primarily celebrity gossip, partisan campaigning, sectarian promotion, or promotional copy without broader constructive value;
- an empty platitude promising success merely for believing;
- based on magical thinking, pseudoscience, guaranteed destiny, or false certainty;
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

## Source strategy

### jstet/quotes-500k

The Hugging Face dataset is a useful **candidate/discovery pool**, not an automatic redistribution source.

Its card traces the data to `ShivaliGoel/Quotes-500K`, whose README says the corpus was scraped from multiple quote websites and made available for educational/research purposes. No repository `LICENSE` file was found during our review. Because Quotes of Wisdom is intended to be public and monetized, we do not blindly copy the dataset into the app.

### Public-domain fiction corpus

Public-domain novels, plays, poetry, and essays are now a preferred source lane. Exact text can be extracted from known public-domain editions, then filtered for standalone value and mapped into the controlled categories. The internal ledger records the work/source so that a future audit does not depend on memory.

## Review pipeline

```text
raw candidate
    -> normalize whitespace/punctuation
    -> reject malformed/duplicate material
    -> standalone-context check
    -> grounded/truthfulness check
    -> motivational-or-insight-value check
    -> category assignment
    -> attribution verification
    -> rights/source verification
    -> final corpus
```

Maintain rejected/uncertain candidates outside the shipping `quotes.json` so questionable material never silently returns to production.
