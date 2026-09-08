# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-09-08_

Read this file first when resuming work. GitHub `main` is the source of truth.

## Release status

| Area | State |
|---|---|
| v1 feature scope | Frozen |
| RevenueCat Test Store integration | Implemented |
| RevenueCat anonymous-user hardening | Implemented |
| RevenueCat cached entitlement handling | Implemented |
| Stable CI QA signing | Implemented |
| Open-source license | Apache-2.0 |
| Current-tree public-source review | Complete |
| GitHub repository | Public |
| Public judge release | `v1.0.0` published |
| Hardened judge candidate | `v1.0.1` source ready; CI/device verification pending |
| Google Play production configuration | Pending |

Repository: `noob-express3000/quotes_of_wisdom`

Latest application-code baseline:

- application-code commit `0b5a169f4815f1dc3d58062a7c6b183eecb1cd6b`
- later commits may update documentation without changing the application binary
- the hardened RevenueCat path must pass the standard Android CI workflow before replacing the public judge APK

## Product

Android-only, local-first quote app built with Kotlin and Jetpack Compose.

- No login, ads, analytics SDK, or custom backend.
- Quotes, preferences, favorites, streaks, trial state, and notification settings are local.
- Android Text-to-Speech supplies narration; a selected network-capable engine may use its own network service.
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
- selectable quote font
- selectable daily reminder time
- no launch paywall

## RevenueCat identity and restore model

Quotes of Wisdom has no login system. The RevenueCat SDK is therefore configured **without a custom App User ID**. RevenueCat generates and caches a random anonymous App User ID for the installation.

This avoids tying purchase identity to Android ID, hardware metadata, or the app signing certificate.

For production and Test Store restore testing, the RevenueCat project should use:

```text
Restore behavior: Transfer to new App User ID
```

That is the documented RevenueCat model for apps that rely on anonymous users and need purchases to be recoverable after reinstalling or moving to another device.

The app exposes a Restore Purchases action. Google Play remains the payment/store identity in production; Quotes of Wisdom does not add its own account system.

## Entitlement startup and offline behavior

RevenueCat `CustomerInfo` is the entitlement source of truth.

- The app asks RevenueCat for `CustomerInfo` at startup and listens for later updates.
- RevenueCat's SDK cache supplies restart/offline entitlement behavior.
- Quotes of Wisdom does **not** maintain a second persistent Boolean Pro cache.
- If a transient entitlement refresh fails after Pro is already known in the current process, the in-memory Pro state is preserved rather than being downgraded because of the error alone.
- A successful RevenueCat response confirming inactive `pro_access` can remove Pro.
- Purchase success is not reported to the UI as Pro success unless the returned `CustomerInfo` actually contains active `pro_access`.
- Restore success without active `pro_access` is surfaced as a clear no-active-purchase result.

This keeps subscription expiration/offline grace semantics inside RevenueCat rather than allowing an app-owned Boolean to outlive the store entitlement.

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

## Privacy boundary

The app does not create its RevenueCat identity from Android ID and does not call RevenueCat `collectDeviceIdentifiers()`.

RevenueCat receives its own anonymous App User ID plus purchase/entitlement traffic required for billing. Local quote activity, favorites, streaks, themes, trial state, and reminder preferences are not sent to a custom backend. The selected Android TTS engine may process spoken quote text according to that provider's behavior, especially for network voices.

See `docs/PRIVACY_POLICY_DRAFT.md` and `docs/DATA_SAFETY_DRAFT.md` for the current working disclosure mapping.

## Build types

| Build | Configuration | RevenueCat | Signing | Intended use |
|---|---|---|---|---|
| Debug | Debuggable development build | Test Store | Local or stable CI debug signer | Development |
| QA | Release-derived but debuggable | Test Store | Local or stable CI debug signer | Device QA and judges |
| Release | Release optimization/shrinking enabled | Google Play key required | Not configured in repo | Production Play bundle |

Because QA remains debuggable, Android Gradle Plugin may disable optimization/obfuscation steps that are incompatible with a debuggable build. The production Release path is separately validated in CI.

The CI Release build uses `goog_ci_validation_key` only to validate compilation, lint, shrinking, and bundle generation. It is not a deployable production RevenueCat configuration.

## Stable judge signing

GitHub Actions restores one committed, test-only debug keystore for CI-produced Debug and QA APKs and verifies the signer before uploading artifacts.

- Stable CI certificate SHA-256: `72:94:42:18:4D:E0:36:0C:72:8F:56:CE:DA:A5:36:90:09:4A:0A:60:00:3D:C4:8E:CD:D3:7D:33:F3:7A:0B:03`
- APKs signed by older transient CI keys must be uninstalled once before installing the stable-signer APK.
- Future stable CI APKs can update one another.
- The keystore is deliberately test-only and must never become the Google Play upload or app-signing key.

RevenueCat identity is no longer derived from this signing certificate.

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
- Quote font selection is Pro-only; non-Pro renders the default Lora font while preserving a saved Pro choice.

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

GitHub Actions performs:

1. production quote-database validation;
2. unit tests;
3. QA lint;
4. Debug and QA APK builds;
5. Release app-bundle path validation;
6. stable Debug/QA signing verification;
7. Debug and QA artifact upload.

Unit tests cover:

- RevenueCat entitlement-state transitions;
- global quote deck and personalization behavior;
- trial/grace/locked/Pro access policy;
- three-color theme invariants.

Compose interaction tests are not yet present. Paywall Info, Close, purchase, Retry, and Restore therefore remain mandatory physical-smoke checks.

## Hardened judge checkpoint

After the hardened CI build is green, verify on a physical Android device:

1. Quote appears and narration starts without an artificial delay.
2. Replay, Next, Favorite, Share, settings, and Back work.
3. Paywall Info and Close work repeatedly.
4. Test Store prices are `$0.99`, `$2.99`, and `$29.99`.
5. Lifetime test purchase activates `PRO`.
6. `PRO` survives force-stop and cold reopen.
7. Previously loaded Pro remains available during the RevenueCat-supported offline cache/grace path.
8. Restore Purchases works after reinstall/new anonymous identity when RevenueCat restore behavior is **Transfer to new App User ID**.
9. A completed purchase without returned `pro_access` produces a visible error instead of false success.
10. Pro-only font, theme, speech, and reminder controls remain gated correctly.
11. Streak flames/horn and Pro spin complete without jank or crash.
12. Daily-notification permission and scheduling behave correctly.

Repeat the critical smoke path on more than one Android device if time permits. Record the exact APK SHA-256 and CI commit in the GitHub Release.

## Remaining work

### Shipaton / direct judge release

- complete hardened CI verification;
- run final physical smoke;
- publish the hardened judge APK as `v1.0.1` if it passes;
- record the demo video and attach screenshots plus BuildInPublic evidence.

### Google Play production release

- set RevenueCat production and sandbox restore behavior appropriately;
- add the real Google Play products and RevenueCat Android SDK key;
- inject a private upload signing key outside the repository;
- add concise renewal/cancellation disclosure and Terms/Privacy links to the paywall;
- finalize and host Privacy/Terms, then complete Data Safety from the final SDK inventory;
- run real Google Play sandbox purchase, cancellation, pending, restore, reinstall, multi-device, and offline tests;
- upload a signed AAB to Internal or Closed testing before production.

## Development discipline

```text
spec -> implement -> CI -> physical-device test -> review -> release
```

Feature scope is locked. Prefer defects, compliance, testing, release hardening, and submission assets over new product scope.
