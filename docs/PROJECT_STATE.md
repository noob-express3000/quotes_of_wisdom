# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-08-25_

This file is the handoff checkpoint for continuing development from any ChatGPT window, computer, Codespace, or local checkout. Read this file first, then `docs/product-spec.md`, before making product or architecture changes.

## Working mode

This project is intentionally vibe-coded.

The user/product owner defines product behavior, tests builds on real hardware, reads/reviews the source, and may propose improvements. ChatGPT is expected to perform the coding, architecture implementation, testing scaffolding, CI maintenance, and debugging.

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

Current bundled quotes are development-safe placeholder/original content; release quote corpus should be curated later for attribution/rights accuracy.

## NEXT MILESTONE — M2

Build the real speech subsystem.

Required M2 scope:

1. Android `TextToSpeech` controller/wrapper.
2. asynchronous initialization handling.
3. one default/fixed trial voice behavior.
4. speak the current quote.
5. wire `Replay` to speak the current quote again.
6. decide whether a newly displayed quote auto-speaks according to the fixed product behavior; preserve the original app concept.
7. pressing `Next` while speech is active must stop/flush current speech before speaking/replacing it.
8. expose speaking/ready/error state cleanly to Compose.
9. graceful fallback when TTS engine/voice data is unavailable.
10. proper lifecycle cleanup via `shutdown()`.
11. Android manifest TTS service query requirement for Android 11+.
12. CI must remain green.
13. generate/installable APK for physical-device test.

Do not implement RevenueCat, paywall, 20-theme selector, or notification system in M2 unless needed by the speech architecture. Keep milestones vertical and independently runnable.

## Later milestone order

```text
M2  TTS + Replay + interruption behavior
M3  DataStore + 20-theme system + selector + persistence
M4  access-state controller + trial/grace/locked UI + settings/paywall shell
M5  RevenueCat Test Store + deterministic opaque ID + offerings/purchase/restore/pro_access
M6  notifications + trial/grace conversion flow + hardening/analytics
M7  Galaxy billing/store build + release hardening
M8  submission polish: license, README, demo video, BuildInPublic evidence
```

## Development discipline

Use a branch/PR per meaningful milestone or subsystem. Run GitHub Actions before merging. Do not merge known-broken builds.

Preferred loop:

```text
spec -> implement small subsystem -> CI -> physical-device test -> review -> merge -> next subsystem
```

Avoid giant generated code dumps, unnecessary frameworks, backend expansion, or architecture ceremony. The project is intentionally small.
