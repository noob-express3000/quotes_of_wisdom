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

## Frozen visual rules

- Every theme uses exactly 3 base colors. No hidden fourth ink color.
- Perceptual 60/30/10 hierarchy:
  - 60% dominant
  - 30% secondary
  - 10% accent
- v1 target: 20 themes.
- Dynamic/material-generated colors must not undermine the three-color rule.
- Theme experimentation should prioritize visually striking, proven three-color combinations while preserving the rule.

## Newly frozen product decisions

- A Settings button/control is required from the home experience; exact placement is finalized during UI prototyping.
- Opening behavior: show the quote immediately, then begin TTS approximately 2 seconds after app launch when speech is available and permitted.
- Share icon: bottom-right inside the quote container, below the author line.
- Favorite/Bookmark icon: opposite bottom-left inside the quote container, below the author line.
- Favorites are local-first and persisted on-device.
- Daily quote notifications are part of the retention loop.
- A daily-engagement streak mechanic is in scope; exact streak-day/grace rules still need to be frozen before implementation.
- Broken-streak response should use an especially strong motivational quote, not guilt-heavy messaging.
- Notification copy should be distinctive and may use humor when appropriate.
- Pro presentation should include polished themes plus a subtle touch-triggered Pro-card animation such as a small flip or spin; exact motion is chosen through visual testing.
- Paywall labels:
  - weekly: `Try It!`
  - monthly: `Best Value!`
  - lifetime: `Own It!`
- Accessibility baseline: respect font scaling, TalkBack/content descriptions for icon-only controls, adequate touch targets and contrast, reduced-motion behavior, and text fallback when TTS is unavailable.

## Frozen commercial/access model

Target prices, subject to store-supported localized price points:

- approximately USD 0.50 weekly
- approximately USD 1.00 monthly
- approximately USD 29 lifetime

All paid products grant RevenueCat entitlement `pro_access`.

The UI must display localized store/RevenueCat pricing rather than hardcoded currency values.

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

Do not add a custom backend, login system, invasive hardware identifiers, or Play Integrity unless real abuse later justifies the complexity.

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

Merged PR: #4 `M1: local quote engine`

CI passed before merge.

Implemented:

- local bundled `quotes.json`
- quote model with author + classification metadata
- asset-backed quote repository
- shuffle/no-repeat quote deck
- `HomeViewModel` using `StateFlow`
- working `Next` button
- strict three-base-color default theme
- trial identity/clock hardening specification

Replay is intentionally still unwired until TTS exists.

## CURRENT RELEASE GATE — PRODUCTION QUOTE CORPUS

Do not begin M2 until the production quote corpus is complete, validated, provenance-ledgered, committed into the Android asset, CI-green, and PR #8 is merged.

PR #8 (`Quotes: cloud curation pipeline`) is currently open on branch `quotes-database`.

The curation pipeline now includes public-domain source lanes, deterministic selection, attribution cleanup, semantic/product-policy finalization, validation, and provenance output. The latest confirmed finalized cloud run before the most recent documentation-only commits retained 1,134 quotes and passed the production validator, but that generated corpus has not yet been promoted into `app/src/main/assets/quotes.json` and PR #8 has not yet been merged.

Before this gate can be marked complete:

1. Perform the final narrow cleanup of the remaining suspicious source labels / obsolete metaphysical fragments / context-dependent death-romance tail without over-filtering grounded mortality/adversity quotes.
2. Run the production generation/finalization workflow again and inspect real logs.
3. Keep at least 1,000 approved production quotes and at least 20 in every controlled category.
4. Spot-check every category, top authors, shortest/longest entries, and suspicious attribution patterns.
5. Promote the generated shipping `quotes.json` plus scalable source/provenance ledger into the repository.
6. Add/update curation-results documentation with funnel, source, author/category, and rights/provenance strategy.
7. Run Android CI and quote validation on the promoted corpus.
8. Merge PR #8 only after all checks are green.

## NEXT MILESTONE AFTER CORPUS GATE — M2

Build the real speech subsystem.

Required M2 scope:

1. Android `TextToSpeech` controller/wrapper.
2. asynchronous initialization handling.
3. one default/fixed trial voice behavior.
4. speak the current quote.
5. wire `Replay` to speak the current quote again.
6. opening quote auto-speaks approximately 2 seconds after app launch when TTS is available and access permits speech.
7. pressing `Next` while speech is active must stop/flush current speech before speaking/replacing it; exact post-Next speak timing should preserve the product feel and be verified during M2 testing.
8. expose speaking/ready/error state cleanly to Compose.
9. graceful fallback when TTS engine/voice data is unavailable.
10. proper lifecycle cleanup via `shutdown()`.
11. Android manifest TTS service query requirement for Android 11+.
12. CI must remain green.
13. generate/installable APK for physical-device test.

Do not implement RevenueCat, paywall, 20-theme selector, or notification system in M2 unless needed by the speech architecture. Keep milestones vertical and independently runnable.

## Later milestone order

```text
M2  TTS + Replay + launch-delay/interruption behavior
M3  DataStore + favorites + 20-theme system + selector + persistence
M4  access-state controller + trial/grace/locked UI + settings/paywall shell
M5  RevenueCat Test Store + deterministic opaque ID + offerings/purchase/restore/pro_access
M6  notifications + daily streak + trial/grace conversion flow + hardening/analytics
M7  Galaxy billing/store build + release hardening
M8  submission polish: license, README, demo video, BuildInPublic evidence
```

Share-card polish can be implemented alongside the home/persistence work when it does not block the vertical milestone sequence.

## Development discipline

Use a branch/PR per meaningful milestone or subsystem. Run GitHub Actions before merging. Do not merge known-broken builds.

Preferred loop:

```text
spec -> implement small subsystem -> CI -> physical-device test -> review -> merge -> next subsystem
```

Avoid giant generated code dumps, unnecessary frameworks, backend expansion, or architecture ceremony. The project is intentionally small.
