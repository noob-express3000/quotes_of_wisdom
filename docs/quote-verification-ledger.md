# Quote Verification Ledger

This ledger records why a candidate was promoted to or rejected from the production quote library.

A quote is not promoted merely because it appears in the source dataset. It must survive:

1. wording verification against a traceable source,
2. source-context review,
3. attribution review,
4. public-domain / redistribution-safety review,
5. realistic + truthful + motivational review,
6. controlled-category mapping.

The `jstet/quotes-500k` dataset is used only for candidate discovery.

## Production batch 1 — approved

All entries below are by authors whose underlying cited works are public-domain material. Links are verification references; the app stores only the short quote text, author, and category.

| ID | Author | Production text | Category | Source/context | Decision |
| ---: | --- | --- | --- | --- | --- |
| 1 | Frederick Douglass | If there is no struggle there is no progress. | resilience | 1857 West India Emancipation speech; Library of Congress reproduces the passage in *No Struggle, No Progress*. | KEEP — exact attribution, grounded claim about social progress, motivational. |
| 2 | Mark Twain | Habit is habit, and not to be flung out of the window by any man, but coaxed down-stairs a step at a time. | discipline | *The Tragedy of Pudd'nhead Wilson*, Chapter VI, Pudd'nhead Wilson's Calendar. Project Gutenberg #102. | KEEP — exact primary-work match; practical behavior-change metaphor. |
| 3 | Henry David Thoreau | If we will be quiet and ready enough, we shall find compensation in every disappointment. | resilience | Thoreau's Journal, Sept. 23, 1838; public-domain 1906 journal edition, Project Gutenberg #57393. | KEEP — exact journal match; framed as attention to possible value after disappointment rather than a guaranteed external reward. |
| 4 | William Shakespeare | Modest doubt is called the beacon of the wise. | learning | *Troilus and Cressida*, Act II, Scene 2. Public-domain text verified against Project Gutenberg; Folger confirms the line/context. | KEEP — counsel for prudent doubt; standalone sense survives context. Spelling lightly modernized from `call'd`. |
| 5 | Alfred, Lord Tennyson | I am a part of all that I have met. | growth | *Ulysses*. Public-domain poem; exact line verified in the poem. | KEEP — realistic statement about experience shaping identity. |
| 6 | Alfred, Lord Tennyson | Come, my friends, ’Tis not too late to seek a newer world. | hope | *Ulysses*. Public-domain poem; exact line verified in Project Gutenberg text. | KEEP — hopeful action without promising an outcome. |
| 7 | Francis Bacon | Histories make men wise; poets, witty; the mathematics, subtile; natural philosophy, deep; moral, grave; logic and rhetoric, able to contend. | learning | *Essays*, “Of Studies”; public-domain text, Project Gutenberg #575/#56463. | KEEP — advocates broad study; rhetorical generalization is contextual and not a guaranteed personal outcome. |
| 8 | Henry David Thoreau | The language of Friendship is not words, but meanings. | relationships | *A Week on the Concord and Merrimack Rivers*; public-domain text, Project Gutenberg #4232. | KEEP — exact source match; meaningful and broadly applicable. |
| 9 | George Eliot | What makes life dreary is the want of motive. | purpose | *Daniel Deronda*; public-domain text, Project Gutenberg #7469. | KEEP — exact source match; concise statement about purpose/motivation, not an absolute prescription for every cause of distress. |
| 10 | George Washington | I conceive a knowledge of books is the basis upon which other knowledge is to be built. | learning | George Washington correspondence; Library of Congress manuscript collection reproduces the passage. | KEEP — exact historical source; grounded endorsement of foundational learning. |
| 11 | Ralph Waldo Emerson | Bad times have a scientific value. These are occasions a good learner would not miss. | resilience | *The Conduct of Life*, “Considerations by the Way”; public-domain 1860 work, verified in Project Gutenberg #39827. | KEEP — explicitly frames adversity as material for learning, not as a promise that suffering is inherently good. |
| 12 | Anne Bradstreet | If we had no winter, the spring would not be so pleasant; if we did not sometimes taste of adversity, prosperity would not be so welcome. | resilience | Bradstreet's *Meditations Divine and Moral* (maxim XIV), preserved in public-domain editions. The dataset contained both a corrupted `"]` copy and a truncated duplicate. | KEEP — exact maxim recovered from source; scrape damage removed and archaic `tast` minimally modernized to `taste`. |

### Verification references

- Frederick Douglass / Library of Congress: https://www.loc.gov/item/2023632632/ and Library of Congress Law Day material reproducing the 1857 passage.
- Mark Twain / Project Gutenberg: https://www.gutenberg.org/files/102/old/102-h/102-h.htm
- Henry David Thoreau Journal / Project Gutenberg: https://www.gutenberg.org/cache/epub/57393/pg57393-images.html
- William Shakespeare / Project Gutenberg: https://www.gutenberg.org/files/1528/1528-h/1528-h.htm
- William Shakespeare / Folger context check: https://www.folger.edu/explore/shakespeares-works/troilus-and-cressida/read/2/2/
- Alfred, Lord Tennyson / Project Gutenberg: https://www.gutenberg.org/files/8601/8601-h/8601-h.htm
- Alfred, Lord Tennyson / Poetry Foundation context check: https://www.poetryfoundation.org/poems/45392/ulysses
- Francis Bacon / Project Gutenberg: https://www.gutenberg.org/files/575/575-h/575-h.htm
- Henry David Thoreau / Project Gutenberg: https://www.gutenberg.org/files/4232/4232-h/4232-h.htm
- George Eliot / Project Gutenberg: https://www.gutenberg.org/cache/epub/7469/pg7469-images.html
- George Washington / Library of Congress: https://tile.loc.gov/storage-services/service/mss/mgw/mgw5/116/116_0373_0628.pdf
- Ralph Waldo Emerson / Project Gutenberg: https://www.gutenberg.org/cache/epub/39827/pg39827-images.html
- Anne Bradstreet / public-domain source transcription: https://www.gutenberg.org/cache/epub/6854/pg6854.html

## Rejected during context / attribution verification

| Candidate | Dataset attribution | Reason for rejection |
| --- | --- | --- |
| A hero is no braver than an ordinary man, but he is brave five minutes longer. | Ralph Waldo Emerson | REJECT — widely repeated but no primary Emerson source located; specialist quotation research identifies it as spurious. |
| Kindness is a language which the deaf can hear and the blind can see. | Mark Twain | REJECT — Center for Mark Twain Studies documents it as apocryphal; the saying circulated before later Twain attribution. |
| It is better to keep your mouth closed and let people think you are a fool than to open it and remove all doubt. | Mark Twain | REJECT — no primary Twain source established; attribution disputed. |
| What seems to us as bitter trials are often blessings in disguise. | Oscar Wilde | REJECT — the wording is real but occurs as dialogue in *The Importance of Being Earnest* and is immediately played for irony; context makes it a poor standalone “truthful” maxim. |
| Small herbs have grace; great weeds do grow apace. | William Shakespeare | REJECT — exact line exists in *Richard III*, but it is a joking/sarcastic exchange about physical growth, not a motivational growth principle. |
| Each time we face our fear, we gain strength, courage, and confidence in the doing. | Theodore Roosevelt | HOLD/REJECT — repeated attribution found, but no traceable Roosevelt primary source established in this pass. It does not meet the app's attribution threshold. |
| My greatest concern is not whether you have failed, but whether you are concerned/content with your failure. | Abraham Lincoln | HOLD/REJECT — wording varies across quote sites and no primary Lincoln source was established in this pass. |
| Do not fear mistakes. You will know failure. Continue to reach out. | Benjamin Franklin | REJECT — modern quotation references explicitly report that no Franklin primary source has been traced; likely later attribution. |
| Keep your fears to yourself, but share your courage with others. | Robert Louis Stevenson | HOLD/REJECT — widely attributed online, but no traceable Stevenson work was established in this pass. |

## Rules learned from verification

- Exact attribution is not enough: context can disqualify a line.
- “Famous quote” websites are discovery hints, never final evidence.
- A quote can be historically authentic and still fail the app's realism/truthfulness/motivation test.
- Corrupted scraped text may be repaired only by returning to the underlying source; never by guessing the missing words.
- Spelling/punctuation may be minimally normalized for modern readability only when meaning is unchanged.
- No final ID is assigned before verification; production IDs are stable once assigned.
