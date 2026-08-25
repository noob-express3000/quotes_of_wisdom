# Production Quote Curation Results

This document is generated from the deterministic production curation pipeline.

## Source strategy

- The original `jstet/quotes-500k` dataset is discovery-only and is not redistributed as the shipping database.
- Twelve individually source-verified public-domain quotes seed the production corpus.
- The large production lane is James Wood, *Dictionary of Quotations* (1893), Project Gutenberg eBook #48105.
- Every shipped record has a matching row in `docs/production-quote-sources.jsonl`, including a traceable source URL.
- Fiction and historical material remain eligible when authentic, context-independent, grounded enough to stand alone, motivational/insight-bearing, and redistribution-safe.
- The final pass rejects unresolved source labels, context fragments, editorial artifacts, sectarian/supernatural claims presented as advice, broad stereotypes, narrow political/legal material, and product-inappropriate nihilistic or death-prescriptive lines.

## Mechanical production report

# Production quote corpus report

- Shipping quotes: **1,215**
- Distinct authors: **420**
- Per-author cap: **20**

## Categories

- `discipline`: 65
- `resilience`: 109
- `courage`: 86
- `growth`: 96
- `learning`: 170
- `focus`: 40
- `work`: 104
- `self-mastery`: 120
- `perspective`: 140
- `purpose`: 30
- `hope`: 110
- `relationships`: 145

## Most represented authors

- William Shakespeare: 20
- Francis Bacon: 20
- Ralph Waldo Emerson: 20
- Cicero: 20
- Samuel Johnson: 20
- Johann Wolfgang von Goethe: 20
- John Ruskin: 20
- Oliver Goldsmith: 20
- Thomas Carlyle: 20
- Horace: 20
- Seneca: 20
- Friedrich Schiller: 20
- Jean Paul: 20
- Ovid: 19
- Arthur Schopenhauer: 19
- Virgil: 17
- Amiel: 16
- François de La Rochefoucauld: 16
- Bulwer Lytton: 15
- Hitopadesa: 14
- Ward Beecher: 14
- Henry David Thoreau: 13
- James Russell Lowell: 11
- Jean de La Bruyère: 11
- Byron: 10
- South: 10
- Sir P. Sidney: 10
- Sterne: 10
- Jean-Jacques Rousseau: 9
- Colton: 9

## Final semantic release pass

# Final production semantic pass

- Quotes retained: **1,063**
- Distinct authors: **356**
- Author cap: **20**

## Categories

- `courage`: 69
- `discipline`: 60
- `focus`: 31
- `growth`: 86
- `hope`: 97
- `learning`: 156
- `perspective`: 117
- `purpose`: 24
- `relationships`: 126
- `resilience`: 94
- `self-mastery`: 109
- `work`: 94

## Final-pass rejections

- `product_policy_semantic_rejection`: 82
- `unresolved_or_collection_attribution`: 46
- `editorial_or_context_artifact`: 19
- `author_cap_after_alias_normalization`: 5

## Most represented authors

- William Shakespeare: 20
- Cicero: 20
- Johann Wolfgang von Goethe: 20
- Francis Bacon: 19
- Samuel Johnson: 19
- Thomas Carlyle: 19
- Seneca: 19
- Ovid: 18
- Oliver Goldsmith: 18
- Horace: 18
- Ralph Waldo Emerson: 17
- Friedrich Schiller: 17
- Jean Paul: 17
- Arthur Schopenhauer: 17
- John Ruskin: 16
- Virgil: 16
- François de La Rochefoucauld: 16
- Henri-Frédéric Amiel: 15
- Edward Bulwer-Lytton: 14
- Henry Ward Beecher: 13
- Henry David Thoreau: 12
- Philip Sidney: 10
- Jean de La Bruyère: 10
- Lord Byron: 9
- James Russell Lowell: 9
- Robert South: 9
- Jean-Jacques Rousseau: 9
- Laurence Sterne: 9
- Charles Caleb Colton: 9
- Blaise Pascal: 9
