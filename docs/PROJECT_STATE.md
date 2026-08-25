# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-08-25_

This is the handoff checkpoint. Read this file, then `docs/product-spec.md`, before changing product behavior.

## Working mode

This project is intentionally vibe-coded. The user/product owner defines behavior, tests real-device builds, reviews source, and proposes changes. ChatGPT performs most implementation, architecture, testing scaffolding, CI maintenance, and debugging.

Do not redesign the product idea unless explicitly asked.

## Repository

Canonical repo: `noob-express3000/quotes_of_wisdom`

GitHub is source of truth. GitHub Actions is the canonical clean Android build.

## Product summary

Android-only, local-first quote app using Android Text-to-Speech and RevenueCat for paid entitlement management.

No login, registration, custom backend, or user account system.

```text
local corpus
 -> global shuffled/no-repeat deck
 -> favorites lightly personalize classifications
 -> quote display
 -> TTS
 -> replay / next / direct text scroll
 -> favorites / share / themes / streak
```

## Frozen visual / interaction rules

- Exactly 3 base colors per theme; perceptual 60/30/10 dominant/secondary/accent.
- All Material roles collapse onto those same 3 colors.
- Supporting/unimportant copy uses secondary, not accent.
- Theme library is now **50 themes**: 2 Trial + 48 Pro.
- Include stronger/bolder palettes; do not make the collection uniformly soft.
- Settings gear: top-left, accent.
- Access label: top-right floating accent text with no background pill.
- Favorite: bottom-left inside quote card below author.
- Share: bottom-right inside quote card below author.
- Long quote text scrolls directly by touch; extra down-arrow control removed.
- No decorative quote marks around quote text.
- Custom three-color Q/quote launcher icon replaces generic bulb imagery.

## Settings hierarchy after physical-test review

1. Favorites launcher first.
2. Speech controls second.
3. Daily streak as lightweight text/illustration-style information, not another card.
4. Themes last among user-facing settings.
5. Debug-only access preview may follow.

Favorites now open in a dedicated screen with close control and per-item removal.

## Quote personalization

The production corpus remains globally shuffled with no repeats during a cycle. Favorites are additionally used as a lightweight taste signal:

- determine the user's top 3 favorited classifications;
- approximately 70% of eligible `Next` draws are biased toward those classifications;
- the same global deck is still consumed, preserving exploration and no-repeat behavior.

## Speech rules

Trial:

- one fixed English voice + 1.0x speed;
- trial voice prefers the best available **local/on-device** English voice using Android TTS quality/latency metadata;
- OEM TTS constructor, voice enumeration, and playback failures degrade to text instead of taking down the app.

Pro:

- installed English voices are selectable;
- labels indicate `local` versus `online`;
- rate 0.7x–1.4x;
- voice/rate persist in DataStore.

## Retention / paywall

- streak day = opening app at least once in local calendar day;
- multiple opens same day count once;
- missed day breaks streak;
- broken streak gets strong motivational opening quote;
- daily notifications still to be implemented;
- Trial launch paywall: dismissible;
- 3-day Grace launch paywall: dismissible, TTS off;
- Locked: paywall non-dismissible;
- Pro: no launch paywall.

Pricing targets:

- Weekly ≈ $0.50 — `Try It!`
- Monthly ≈ $1.00 — `Best Value!`
- Lifetime ≈ $29 — `Own It!`

Monthly keeps the thicker emphasis border. Pricing cards now carry the touch-spin interaction; the large Pro hero remains stable.

All products eventually grant one RevenueCat entitlement: `pro_access`.

## Access state machine

```text
DAY 0-30   TRIAL_ACTIVE
DAY 31-33  GRACE_TEXT_ONLY
DAY 34+    LOCKED

ANY STATE + active pro_access -> PRO
```

Debug Trial/Grace/Locked/Pro overrides are debug-only and now persist across app restarts for reliable demonstrations. Release builds ignore persisted debug overrides.

## Trial hardening plan for RevenueCat milestone

Use a deterministic opaque device-scoped App User ID:

```text
ANDROID_ID + package + signing certificate fingerprint
 -> SHA-256
 -> opaque RevenueCat App User ID
```

Never expose/transmit the raw Android ID as the RevenueCat identifier.

Layer trial clocks using persisted wall time, monotonic elapsed time, and RevenueCat first-seen/customer timing so clock rollback cannot extend the trial.

## M0 — COMPLETE

- Kotlin + Compose Android project
- API 36 toolchain
- Actions APK build
- physical-device launch validated

## M1 — COMPLETE

Merged PR #4.

- asset quote repository
- quote model
- global shuffled/no-repeat deck
- StateFlow ViewModel
- Next button
- strict three-color default theme

## PRODUCTION QUOTE CORPUS — COMPLETE

Merged PR #8, merge commit `1a2363e189951bf65c7cfc34654d0c2bc1f6e55e`.

- 1,063 shipping quotes
- 356 authors
- 12 categories, each >=20
- per-author cap 20
- exact schema `id`, `text`, `author`, `classification`
- provenance ledger for every shipped record
- Project Gutenberg / individually verified public-domain source strategy
- production validator and Android CI passed

Do not casually reopen the corpus gate.

## CURRENT — PR #9 PHYSICAL-DEVICE PRODUCT TEST

Branch: `m2-product-test`
PR: #9 `M2: physical-device product test build`

Originally tested build exposed the following physical-test feedback, now addressed on branch:

- remove down-arrow from quote card -> **done**; direct swipe remains;
- floating FREE/PRO text instead of background pill -> **done**;
- Settings hierarchy felt too busy -> **done**; Favorites first, Speech second, streak simplified, Themes last;
- Favorites should have own window -> **done**;
- add more/bolder themes -> **done**, 50 total;
- bias quotes toward classifications the user favorites -> **done**;
- default voice disliked -> **done**, local/high-quality voice preferred;
- identify local versus online voices -> **done**;
- generic/missing launcher icon -> **done**, custom Q/quote icon;
- spin pricing cards rather than hero -> **done**;
- preserve monthly thick border -> **kept**;
- debug/demo access state should survive restart -> **done**;
- locked-feature paywall behavior was liked -> **preserved**.

One hardware note remains to verify on-device: the handwritten test mentions the app not running on a `V50 Lite`. Public specs for the vivo V50 Lite are Android 15, so minSdk is not the obvious cause. TTS initialization is now hardened, but if the next APK still fails on that phone, capture the exact install error/crash behavior or log before changing compatibility settings blindly.

RevenueCat purchase buttons are still explicit placeholders in PR #9.

## Immediate next action

1. Run branch-head Android CI after these physical-test fixes.
2. Inspect actual compiler/build logs.
3. Download the new `quotes-of-wisdom-debug` artifact.
4. User installs/tests this revised APK, including V50 Lite if available.
5. Fix any remaining findings.
6. Merge PR #9 only after physical-device signoff.
7. Begin RevenueCat Test Store milestone.

## Later milestones

```text
M3  consolidate/polish persistence, favorites, themes and share UX prototyped in M2
M4  harden access/trial clocks/paywall shell prototyped in M2
M5  RevenueCat Test Store + deterministic ID + offerings/purchase/restore/pro_access
M6  notifications + streak/conversion flow + hardening/analytics
M7  Google Play/store release hardening
M8  license/README/demo/BuildInPublic + Next Gen submission polish
```

Galaxy Store remains optional/deprioritized.

## Development discipline

```text
spec -> implement -> CI -> physical-device test -> review -> merge
```

No giant framework expansion, backend creep, or unverified merges.
