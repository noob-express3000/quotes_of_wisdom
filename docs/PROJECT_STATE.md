# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-08-27_

Read this file first when resuming work. GitHub is the source of truth.

## Repository / active work

- Repo: `noob-express3000/quotes_of_wisdom`
- Current integration branch: `m5-revenuecat-test`
- Current PR: #10 `M5: RevenueCat Test Store integration`
- PR #10 is stacked on `m2-product-test`; do not merge blindly until physical-device billing validation passes.
- GitHub Actions is the canonical clean Android build.

## Product

Android-only, local-first quote app built with Kotlin + Jetpack Compose.

- No login or custom backend.
- Local quote corpus + Android Text-to-Speech.
- RevenueCat is authoritative for paid Pro entitlement.
- Local DataStore holds app preferences, favorites, streak, trial state and debug previews.

## Shipping quote corpus

Complete and frozen unless a real defect is found:

- 1,063 quotes
- 356 authors
- 12 categories
- global shuffled/no-repeat deck
- favorites lightly personalize future draws while preserving the global no-repeat deck

## Access lifecycle

```text
DAY 0-30   TRIAL_ACTIVE
DAY 31-33  GRACE_TEXT_ONLY
DAY 34+    LOCKED

ANY STATE + active RevenueCat `pro_access` -> PRO
```

Trial:
- full quote browsing
- TTS with one fixed local-preferred English voice at 1.0x
- 2 themes
- dismissible launch paywall

Grace:
- text only
- TTS disabled
- dismissible launch paywall

Locked:
- app blocked by non-dismissible paywall

Pro:
- all 100 themes
- TTS
- installed TTS engine selection
- installed English voice selection
- additional voice-data installer flow
- speech rate 0.7x–1.4x
- no launch paywall

## Commercial model — FROZEN

RevenueCat/Test Store catalog pricing:

- Weekly: **USD 0.99**
- Monthly: **USD 2.99**
- Lifetime: **USD 29.99**

Product IDs:

- `qow_weekly`
- `qow_monthly`
- `qow_lifetime`

All three products grant the single entitlement:

- `pro_access`

The three products must be attached to the RevenueCat **Current Offering** as Weekly / Monthly / Lifetime packages.

Runtime paywall prices come from RevenueCat/store localized pricing whenever available. Hardcoded `$0.99 / $2.99 / $29.99` values are fallback labels only.

## RevenueCat M5 implementation

Implemented on `m5-revenuecat-test`:

- RevenueCat Android SDK 10.18.1
- debug build uses the RevenueCat Test Store public SDK key
- release build intentionally has no Test Store key
- application-start RevenueCat configuration
- deterministic opaque device-scoped App User ID
- `pro_access` observation and launch refresh
- Current Offering retrieval
- localized package pricing
- Weekly / Monthly / Lifetime package mapping
- purchase flow with success / cancellation / error handling
- Restore Purchases flow
- RevenueCat Pro state feeds the existing Trial / Grace / Locked / Pro controller
- paywall cards invoke real RevenueCat purchase calls

Device-scoped RevenueCat identity:

```text
ANDROID_ID + package name + signing-certificate fingerprint
 -> SHA-256
 -> opaque RevenueCat App User ID
```

Never send/display the raw Android ID as the RevenueCat identifier.

## Physical-test signing

GitHub Actions previously relied on runner-generated debug signing, which could change between ephemeral runners and make successive physical-test APKs incompatible for update installs.

The M5 branch now restores one stable **test-only debug keystore** before `assembleDebug`.

Important:
- uninstall pre-stable-signer APKs once before installing the first stable-signer checkpoint;
- after that, subsequent M5 debug APKs should update normally;
- this test signer is not the future production Play signing key.

Because the RevenueCat test App User ID includes the signing fingerprint, switching from the old transient signer to the stable test signer changes the test customer identity once. Production signing will intentionally create its own identity namespace later.

## Current UI / interaction rules

- Exactly 3 base colors per theme using perceptual 60/30/10 hierarchy.
- 100 themes: 2 Trial + 98 Pro.
- Immersive fullscreen with display-cutout-aware controls.
- Settings gear top-left.
- Access label top-right.
- Streak centered below likely camera/cutout area.
- Favorite bottom-left inside quote card.
- Share bottom-right inside quote card.
- Long quote text directly scrollable.
- No decorative quote marks.

Home microinteractions:

- Tapping `PRO` performs exactly one 360° text-only spin with no ripple, scale pulse, background change, navigation or state mutation.
- Tapping the streak triggers the approximately 1.2-second palette-aware full-screen flame surge.
- The same streak tap now starts a roughly **1.8-second synthesized deep horn blast** through Android media audio.
- The horn is generated in-app with `AudioTrack`; no bundled/licensed audio asset is required.
- Repeated streak taps restart the horn rather than stacking multiple horn instances.
- Flame/horn effects do not mutate streak or access state.

## Speech behavior

Opening quote:
- appears immediately;
- begins narration as soon as TTS is ready and access allows speech;
- no artificial 2-second app-side delay.

Replay:
- restarts current quote with queue flush.

Next:
- stops active speech before advancing.

Trial:
- system-default engine
- one fixed local-preferred English voice
- 1.0x speed
- TTS failures degrade to readable text

Pro:
- enumerate installed TTS engines
- selected engine persists
- engine switch reinitializes TTS and refreshes voices
- installed English voices selectable
- local/online voice labels
- not-installed voices excluded
- `Get more voices` opens engine/system voice-data UI
- rate 0.7x–1.4x persists

## Retention

- opening app once in a local calendar day completes that streak day
- same-day reopen counts once
- missed day breaks streak
- broken streak uses a strong motivational opening quote rather than guilt copy
- daily notifications remain to be implemented in M6

## Debug behavior

Debug builds expose persisted Trial / Grace / Locked / Pro previews for product testing. Release builds ignore debug access overrides.

## Current validation target

The next physical-device checkpoint must verify:

1. Fresh install succeeds after uninstalling any APK signed by the old transient CI signer.
2. Paywall loads RevenueCat prices `$0.99 / $2.99 / $29.99` (or localized equivalents).
3. Weekly purchase opens the RevenueCat Test Store purchase UI and activates `pro_access`.
4. Monthly purchase works.
5. Lifetime non-consumable purchase works.
6. Pro persists after app restart.
7. Restore Purchases restores Pro when an active test purchase exists.
8. Correct RevenueCat Current Offering/package wiring is present if the app reports that a package is unavailable.
9. Streak tap launches flame surge and the deep horn together without jank/crash.
10. Repeated streak taps restart the horn cleanly.
11. Existing TTS, themes, favorites, sharing, Pro spin and access-state behavior remain intact.

## Remaining milestones

```text
M5  finish RevenueCat Test Store physical validation and harden trial identity/clocks
M6  notifications + streak/conversion flow + analytics/hardening
M7  Google Play/store release signing, real products, release build and store hardening
M8  license/README/demo/BuildInPublic + Shipaton submission polish
```

## Development discipline

```text
spec -> implement -> CI -> physical-device test -> review -> merge
```

Feature set is effectively locked. Prefer bug fixes, monetization, required submission work and release hardening over additional scope, unless the product owner explicitly requests a small contained change.
