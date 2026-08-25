# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-08-25_

This file is the handoff checkpoint for continuing development from any ChatGPT window, computer, Codespace, or local checkout. Read this file first, then `docs/product-spec.md`, before making product or architecture changes.

## Working mode

This project is intentionally vibe-coded.

The user/product owner defines product behavior, tests builds on real hardware, reads/reviews the source, and may propose improvements. ChatGPT is expected to perform the coding, architecture implementation, testing scaffolding, CI maintenance, and debugging.

The user/product owner intends to handle the public development/storytelling narrative personally, including how the AI-assisted implementation process is presented.

Do not redesign the product idea unless explicitly asked. Preserve the frozen concept and optimize execution.

## Repository

Canonical repo: `noob-express3000/quotes_of_wisdom`

GitHub is the source of truth. The project is set up to work from Windows, Linux/macOS, GitHub Codespaces/browser, and GitHub Actions. The canonical clean build is GitHub Actions.

## Product summary

Android-only, local-first quote application using Android Text-to-Speech and RevenueCat for paid entitlement management.

No login, registration, custom backend, or user account system.

Core loop:

```text
local quote corpus
    -> shuffled/no-repeat quote selection
    -> quote displayed
    -> TTS reads quote
    -> replay / next / text scroll
    -> repeated use
```

## Frozen visual / interaction rules

- Every theme uses exactly 3 base colors. No hidden fourth ink color.
- Perceptual 60/30/10 hierarchy: dominant / secondary / accent.
- v1 target: 20 themes.
- Dynamic/Material-generated colors must not undermine the three-color rule.
- Theme experimentation should prioritize visually striking, proven three-color combinations.
- Settings gear: top-left, using the theme accent/10 color.
- FREE/PRO chip: top-right.
- Share icon: bottom-right inside quote container below author.
- Favorite icon: bottom-left inside quote container below author.
- Opening quote displays immediately; TTS starts about 2 seconds later when available/allowed.
- No decorative quote marks are added around quote text.
- The quote-scroll control is a text-scroll control, not volume.
- The unfinished handwritten requirement beginning `Replay button should ...` is still unresolved; do not invent extra Replay-specific UI behavior.

## Frozen retention / paywall rules

- A streak day is completed by opening the app at least once during the local calendar day.
- Multiple openings in the same day count once.
- Missing a local calendar day breaks the streak.
- Broken-streak response uses a strong motivational quote rather than guilt-heavy messaging.
- Daily notifications are in scope and copy may be humorous where appropriate.
- During TRIAL_ACTIVE, show the upgrade/paywall experience on each cold app launch; it is dismissible.
- During the 3-day GRACE_TEXT_ONLY period, show the launch paywall; it remains dismissible, but TTS is disabled.
- During LOCKED, the paywall is non-dismissible until `pro_access` becomes active.
- Temporary Activity resumes/background returns do not count as a new cold launch.

## Frozen commercial/access model

Target prices, subject to store-supported localized price points:

- approximately USD 0.50 weekly — `Try It!`
- approximately USD 1.00 monthly — `Best Value!`
- approximately USD 29 lifetime — `Own It!`

All paid products grant RevenueCat entitlement `pro_access`.

The UI must display localized store/RevenueCat pricing rather than hardcoded currency values in the production RevenueCat build.

Access state machine:

```text
DAY 0-30
TRIAL_ACTIVE
- full quote browsing
- replay / next / scrolling
- TTS enabled
- 1 fixed TTS voice
- fixed TTS speed
- 2 themes

DAY 31-33
GRACE_TEXT_ONLY
- quote text remains accessible
- TTS disabled
- upgrade messaging/notifications

DAY 34+
LOCKED
- core app locked behind upgrade/paywall
- upgrade notifications continue for at least first 4 locked days
- combined post-trial upgrade reminder period >= 7 days

ANY STATE + active `pro_access`
PRO
- full access
- all 20 themes
- TTS
- multiple selectable voices
- adjustable TTS speed
```

The 30-day trial starts automatically on installation. No card/subscription enrollment is required to begin it.

## Trial-abuse hardening decision

Do not rely solely on a local installation timestamp.

When RevenueCat is integrated, use a deterministic opaque device-scoped App User ID rather than a random RevenueCat anonymous ID.

Planned identity concept:

```text
ANDROID_ID + app/package/signing context
    -> one-way hash
    -> opaque RevenueCat App User ID
```

Never transmit/store the raw Android ID unnecessarily.

Use RevenueCat customer history/first-seen information as an external anchor where suitable so ordinary reinstall does not trivially reset the trial.

Use layered time checks to resist manual wall-clock rollback:

- persisted last-seen wall time
- monotonic `SystemClock.elapsedRealtime()` while the boot session exists
- RevenueCat/external first-seen anchor when online

Known residual edge cases such as factory reset/new Android user/rooted-device manipulation are acceptable for v1.

## M0 — COMPLETE

Validated on a physical Android phone.

- Kotlin Android project
- Jetpack Compose + Material 3
- API 36 toolchain
- GitHub Actions APK build
- APK installs and launches on physical Android
- home-screen shell
- universal dev environment / Codespaces support

## M1 — COMPLETE

Merged PR #4: `M1: local quote engine`.

Implemented:

- local bundled `quotes.json`
- quote model with author + classification metadata
- asset-backed quote repository
- shuffle/no-repeat quote deck
- `HomeViewModel` using `StateFlow`
- working `Next` button
- strict three-base-color default theme
- trial identity/clock hardening specification

## PRODUCTION QUOTE CORPUS — COMPLETE

Merged PR #8 on 2026-08-25. Merge commit: `1a2363e189951bf65c7cfc34654d0c2bc1f6e55e`.

Final shipping corpus:

- **1,063 quotes**
- **356 distinct authors**
- per-author cap: **20**
- all 12 controlled categories represented with at least 20 entries
- exact Android schema preserved: `id`, `text`, `author`, `classification`
- contiguous IDs and normalized-text uniqueness validated

Final category counts:

- courage 69
- discipline 60
- focus 31
- growth 86
- hope 97
- learning 156
- perspective 117
- purpose 24
- relationships 126
- resilience 94
- self-mastery 109
- work 94

Release files:

- `app/src/main/assets/quotes.json`
- `docs/production-quote-sources.jsonl`
- `docs/quote-curation-results.md`
- `tools/verified_seed_quotes.json`

Source strategy:

- `jstet/quotes-500k` remains discovery-only and is not the shipping source database.
- 12 individually source-verified public-domain quotes seed deterministic generation.
- the large production lane uses James Wood, *Dictionary of Quotations* (1893), Project Gutenberg eBook #48105.
- every shipped record has a matching provenance-ledger row with a traceable source URL.
- final semantic policy rejects unresolved source labels, fragments/editorial artifacts, product-inappropriate stereotypes, sectarian/supernatural claims framed as advice, narrow political/legal material, and nihilistic/death-prescriptive lines.

The quote corpus release gate is closed. Do not reopen it casually; future corpus edits must preserve validation and provenance.

## CURRENT MILESTONE — M2 PHYSICAL-DEVICE PRODUCT TEST

Branch: `m2-product-test`
PR: #9 `M2: physical-device product test build`

Implemented on this branch:

- Android `TextToSpeech` controller with asynchronous initialization and lifecycle shutdown.
- fixed trial/default English voice and fixed trial speed.
- Pro mode enumerates selectable English voices installed on the device.
- Pro speech-speed adjustment from 0.7x to 1.4x.
- Pro voice/speed preferences persisted in DataStore.
- current quote speaks automatically about 2 seconds after display when speech is allowed.
- Replay restarts current quote using `QUEUE_FLUSH`.
- Next stops active speech, advances the no-repeat deck, then normal auto-speak timing applies.
- graceful text fallback if TTS is unavailable.
- Android 11+ TTS service query declaration.
- Settings gear top-left using accent color.
- local Favorite/Bookmark persistence.
- Android share sheet for quote + author.
- quote-text scroll button for long quotes.
- 20 strict three-color themes; 2 free and 18 Pro-gated.
- Material 3 color roles are explicitly collapsed onto the active three-color palette to avoid hidden fourth colors.
- Settings screen with theme gallery, streak display, favorites list, and Pro speech controls.
- daily-open streak persistence.
- deterministic startup ordering so a broken streak can select an especially motivational opening quote.
- Trial/Grace/Locked local access shell.
- launch paywall in Trial/Grace and non-dismissible paywall in Locked.
- target paywall labels/prices are visible only as product-test placeholders until RevenueCat supplies localized values.
- touch-triggered Pro-card spin treatment.
- debug-only state selector for Trial/Grace/Locked/Pro; choosing a non-Pro state opens the corresponding paywall immediately for physical testing.

RevenueCat purchases are deliberately not connected in PR #9. Purchase buttons must remain clearly marked as test placeholders until the RevenueCat Test Store milestone.

Do not merge PR #9 until its final branch-head Android CI is green and the debug APK has been exercised on the user's physical Android device.

## Next milestones

```text
M2  finish CI + physical-device test + fix findings + merge PR #9
M3  consolidate/polish persistence, favorites, themes and share-card UX already prototyped in M2
M4  harden access-state controller, trial clocks and paywall UX already prototyped in M2
M5  RevenueCat Test Store + deterministic opaque ID + offerings/purchase/restore/pro_access
M6  notifications + streak/conversion flow + hardening/analytics
M7  store/release hardening
M8  submission polish: license, README, demo video, BuildInPublic evidence
```

Galaxy Store work is optional/deprioritized; do not let seller-verification bureaucracy block the primary Next Gen / BuildInPublic path.

## Development discipline

Use a branch/PR per meaningful milestone or subsystem. Run GitHub Actions before merging. Do not merge known-broken builds.

Preferred loop:

```text
spec -> implement small subsystem -> CI -> physical-device test -> review -> merge -> next subsystem
```

Avoid unnecessary frameworks, backend expansion, or architecture ceremony. The project is intentionally small.
