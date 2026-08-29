# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-08-29_

Read this file first when resuming work. GitHub `main` is the source of truth.

## Release status

| Area | State |
|---|---|
| v1 feature scope | Frozen |
| Production-hardening changes | Merged |
| RevenueCat Test Store integration | Implemented |
| Entitlement restart/offline hardening | Implemented |
| Stable CI QA signing | Implemented |
| Main-branch CI | Green |
| Open-source license | Apache-2.0 |
| Final stable-APK device smoke | Pending |
| Shipaton public-source packaging | In progress |
| Google Play production configuration | Pending |

Repository: `noob-express3000/quotes_of_wisdom`

Integrated code baseline:

- `main` commit `be6505c590ddbe43ac09be7e832ffcac09b1c9d5`
- PR #14, squash-merged on 2026-08-29
- post-merge [GitHub Actions run `33242052147`](https://github.com/noob-express3000/quotes_of_wisdom/actions/runs/33242052147) completed successfully
- quote validation, unit tests, lint, Debug APK, optimized QA APK, Release-path bundle, and stable-signature verification all passed

Documentation-only commits may follow this baseline without changing the application binary.

## Product

Android-only, local-first quote app built with Kotlin and Jetpack Compose.

- No login, ads, analytics SDK, or custom backend.
- Quotes, preferences, favorites, streaks, trial state, and notification settings are local.
- Android Text-to-Speech supplies narration.
- RevenueCat is authoritative for paid Pro entitlement.
- The effective feature set is determined by the app-controlled trial state plus the RevenueCat `pro_access` entitlement.

## Shipping quote corpus

Frozen unless a verified content defect is found:

- 1,063 quotes
- 356 authors
- 12 classifications
- global shuffled/no-repeat deck
- favorites lightly personalize future draws while preserving the global no-repeat deck
- provenance and verification records under `docs/`

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

- quote text remains available
- TTS disabled
- dismissible launch paywall

Locked:

- app blocked by a non-dismissible paywall until Pro is restored or purchased

Pro:

- all 100 themes
- TTS
- installed TTS engine and English voice selection
- additional voice-data installer flow
- speech rate from 0.7x to 1.4x
- selectable daily reminder time
- no launch paywall

## Entitlement startup behavior

RevenueCat remains authoritative, while a last-confirmed local entitlement snapshot prevents known Pro users from seeing a false paywall during refresh.

- A confirmed active `pro_access` entitlement is cached locally.
- The snapshot seeds the next cold launch while RevenueCat refreshes in the background.
- A network or SDK refresh failure does not replace known Pro with Free.
- Only a successful RevenueCat response confirming inactive access can clear a previously confirmed Pro state.
- Free/unknown startup waits for a fresh entitlement resolution before deciding whether to show the launch paywall.
- The entitlement snapshot is excluded from Android backup and device transfer.

This snapshot improves startup behavior; it is not a second source of truth and cannot grant a new purchase.

## Commercial model — frozen for v1

Test Store/catalog targets:

- Weekly: **USD 0.99**
- Monthly: **USD 2.99**
- Lifetime: **USD 29.99**

Product IDs:

- `qow_weekly`
- `qow_monthly`
- `qow_lifetime`

All products grant the single entitlement `pro_access` and must be attached to RevenueCat's Current Offering as weekly, monthly, and lifetime packages.

Runtime prices come from RevenueCat/store localized pricing. A plan remains disabled until its real product and formatted price load. The app does not request location, infer region, or display invented fallback prices.

## RevenueCat identity and privacy boundary

The app creates a deterministic, opaque, device-scoped RevenueCat App User ID:

```text
ANDROID_ID + package name + signing-certificate fingerprint
 -> SHA-256
 -> qow_<opaque digest>
```

The app does not pass the raw Android ID as its RevenueCat customer identifier. Changing the app signing certificate changes the identifier namespace, which is why judge and production signing identities are intentionally separate.

RevenueCat receives the opaque identifier and purchase/entitlement traffic. Local quote activity, favorites, streaks, themes, and reminder preferences are not sent to a custom backend. The selected Android TTS engine may process spoken quote text according to that provider's own behavior, especially for network voices.

## Build types

| Build | Optimization | RevenueCat | Signing | Intended use |
|---|---|---|---|---|
| Debug | Debuggable/unoptimized | Test Store | Local or stable CI debug signer | Development |
| QA | Minified and resource-shrunk; debuggable | Test Store | Local or stable CI debug signer | Device QA and judges |
| Release | Minified and resource-shrunk | Google Play key required | Not configured in repo | Production Play bundle |

The CI Release build uses `goog_ci_validation_key` only to validate compilation, lint, shrinking, and bundle generation. It is not a deployable production RevenueCat configuration.

## Stable judge signing

GitHub Actions restores one committed, test-only debug keystore for CI-produced Debug and QA APKs and verifies the signer before uploading artifacts.

- Stable CI certificate SHA-256: `72:94:42:18:4D:E0:36:0C:72:8F:56:CE:DA:A5:36:90:09:4A:0A:60:00:3D:C4:8E:CD:D3:7D:33:F3:7A:0B:03`
- Baseline QA APK SHA-256 from the post-merge run: `c53cf8cec3f5aab4e813b57189bd9639a1f1ad3746547e86675d419e1f9d93f9`
- APKs signed by older transient CI keys must be uninstalled once before installing the stable-signer APK.
- Future stable CI APKs can update one another.
- The keystore is deliberately test-only and must never become the Google Play upload or app-signing key.

Because the RevenueCat App User ID includes the signing fingerprint, the one-time signer migration also creates a new Test Store customer identity. Test purchases made under an older signer will not appear under the stable signer.

## Implemented interaction rules

- Exactly three base colors per theme with a perceptual 60/30/10 hierarchy.
- Accent color owns button labels, important labels, icons, and borders.
- 100 themes: 2 Trial and 98 Pro.
- Immersive fullscreen with display-cutout-aware controls.
- Settings gear top-left, access label top-right, streak centered around the cutout.
- Favorite and Share controls inside the quote card.
- Long quote text is directly scrollable.
- `PRO` performs one text-only 360-degree spin.
- Streak tap performs the palette-aware flame surge and synthesized horn without changing streak state.
- Replay restarts narration; Next stops current speech before advancing.
- Paywall Info and Close remain independently tappable above the scrolling content.
- Retry appears only after billing data fails and refreshes RevenueCat customer/offerings data; it does not start a purchase.

## Notifications

Daily reminders are implemented.

- Android 13+ notification permission handling
- local notification channel and AlarmManager scheduling
- free/trial reminder fixed at 09:00
- Pro-selectable reminder time
- rescheduling after delivery, reboot, manual clock change, timezone change, and app update
- debug-only high-priority demo receiver for capture/testing
- no exact-alarm permission required

## Automated validation

GitHub Actions currently performs:

1. production quote-database validation;
2. unit tests;
3. QA lint;
4. Debug and QA APK builds;
5. minified Release app-bundle path validation;
6. stable Debug/QA signing verification;
7. Debug and QA artifact upload.

Unit tests cover:

- RevenueCat entitlement-state transitions;
- global quote deck and personalization behavior;
- trial/grace/locked/Pro access policy;
- three-color theme invariants.

Compose interaction tests are not yet present. Paywall Info, Close, purchase, Retry, and Restore are therefore mandatory physical-smoke checks until automated UI coverage is added.

## Final judge checkpoint

Install the stable CI QA APK once after removing any older-signer build, then verify:

1. Quote appears and narration starts without an artificial delay.
2. Replay, Next, Favorite, Share, settings, and Back work.
3. Paywall Info and Close work repeatedly.
4. Test Store prices are `$0.99`, `$2.99`, and `$29.99`.
5. Lifetime test purchase activates `PRO`.
6. `PRO` survives force-stop and cold reopen.
7. Known Pro still opens as Pro while offline.
8. Restore Purchases succeeds for the same stable-signer Test Store identity.
9. Streak flames/horn and Pro spin complete without jank or crash.
10. Daily-notification permission and scheduling behave correctly.

Run on both Samsung and itel devices if time permits. Record the exact APK SHA-256 and CI commit in the GitHub Release.

## Remaining work

### Shipaton / direct judge release

- complete final stable-APK physical smoke;
- publish a permanent GitHub Release with APK, checksum, signer, and test instructions;
- make the repository public only after the source/credential review;
- record the demo video and attach screenshots plus BuildInPublic evidence.

### Google Play production release

- add the real Google Play products and RevenueCat Android SDK key;
- inject a private upload signing key outside the repository;
- add concise renewal/cancellation disclosure and Terms/Privacy links to the paywall;
- finalize and host Privacy/Terms, then complete Data safety from the final SDK inventory;
- run real Google Play sandbox purchase, cancellation, pending, restore, and offline tests;
- upload a signed AAB to Internal or Closed testing before production.

## Development discipline

```text
spec -> implement -> CI -> physical-device test -> review -> merge
```

Feature scope is locked. Prefer defects, submission assets, compliance, testing, and release hardening over new product scope.
