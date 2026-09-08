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
| Original judge release | `v1.0.0` retained for provenance |
| Hardened judge release | `v1.0.1` published |
| Google Play production configuration | Pending |

Repository: `noob-express3000/quotes_of_wisdom`

Hardened application baseline:

- source commit: `2c1d9ddb83384f4dc0764059b686dbcdf770762b`
- version: `1.0.1` (`versionCode 2`)
- Android CI run: `34281375036`
- QA artifact ID: `10077906158`
- judge APK SHA-256: `3fd037fa01b396c5aef129febf5b4a554fe071f9590cb9e4734ee85fb37f8092`
- release: <https://github.com/noob-express3000/quotes_of_wisdom/releases/tag/v1.0.1>

The CI run passed quote validation, unit tests, QA lint, Debug/QA APK builds, Release bundle-path validation, stable signer verification, and artifact uploads.

## Product

Android-only, local-first quote app built with Kotlin and Jetpack Compose.

- No login, ads, analytics SDK, or custom backend.
- 1,063 curated quotes from 356 authors across 12 classifications.
- Quotes, favorites, themes, streaks, trial state, and reminders are local.
- Android Text-to-Speech supplies narration; network-capable voices may use their provider's network service.
- RevenueCat is authoritative for paid `pro_access` entitlement.
- 100 themes: 2 Trial and 98 Pro.
- Pro includes TTS engine/voice/speed controls, quote fonts, custom reminder time, and all themes.

## Access lifecycle

```text
DAY 0-30   TRIAL_ACTIVE
DAY 31-33  GRACE_TEXT_ONLY
DAY 34+    LOCKED

ANY STATE + active RevenueCat `pro_access` -> PRO
```

The app-controlled trial remains intentionally local. Clearing app data can reset local trial state; this is a known tradeoff of the no-account/no-backend model rather than a paid-entitlement mechanism.

## RevenueCat identity and restore model

Quotes of Wisdom has no authentication system, so RevenueCat is configured **without a custom App User ID**. The RevenueCat SDK generates and caches an anonymous App User ID for the installation.

The app no longer derives billing identity from Android ID, package signing material, or hardware metadata.

For Test Store and Google Play restore behavior, the RevenueCat project should use:

```text
Restore behavior: Transfer to new App User ID
```

The app exposes Restore Purchases so a purchase can be recovered after reinstalling or moving to another device without adding a Quotes of Wisdom account system.

## Entitlement behavior

RevenueCat `CustomerInfo` is the paid-entitlement source of truth.

- Startup requests `CustomerInfo` and installs an update listener.
- RevenueCat's SDK cache provides restart/offline entitlement behavior.
- Quotes of Wisdom does not maintain a second persistent Boolean Pro cache.
- A transient refresh failure does not downgrade already-known in-process Pro state by itself.
- A successful RevenueCat response confirming inactive `pro_access` can remove Pro.
- Purchase success is reported only when returned `CustomerInfo` contains active `pro_access`.
- Restore without active `pro_access` produces an explicit no-active-purchase error.
- Loss of Pro resets the daily reminder to the free 09:00 default.

## Commercial model

- Weekly: **USD 0.99** — `qow_weekly`
- Monthly: **USD 2.99** — `qow_monthly`
- Lifetime: **USD 29.99** — `qow_lifetime`
- Entitlement: `pro_access`

All three products must be attached to RevenueCat's Current Offering. Runtime prices come from RevenueCat/store localized pricing; the app does not request location or invent fallback prices.

## Build types

| Build | RevenueCat | Signing | Intended use |
|---|---|---|---|
| Debug | Test Store | Local/stable CI debug signer | Development |
| QA | Test Store | Stable CI test signer in CI | Device QA / judges |
| Release | Google Play key required | Production signing not in repo | Play Store AAB |

Stable CI certificate SHA-256:

`72:94:42:18:4D:E0:36:0C:72:8F:56:CE:DA:A5:36:90:09:4A:0A:60:00:3D:C4:8E:CD:D3:7D:33:F3:7A:0B:03`

The committed CI keystore is test-only and must never become the Google Play upload or app-signing key.

## Physical smoke still required

Before replacing submission links with `v1.0.1`, verify the released APK on a physical Android device:

1. Clean install and cold launch.
2. Trial paywall is dismissible; Info/Close work repeatedly.
3. Weekly/Monthly/Lifetime Test Store prices show `$0.99`, `$2.99`, `$29.99`.
4. Lifetime Test Store purchase activates Pro.
5. Pro survives force-stop/reopen.
6. RevenueCat-cached Pro behaves correctly offline.
7. After setting RevenueCat sandbox restore behavior to **Transfer to new App User ID**, reinstall and Restore Purchases successfully recover Pro.
8. Trial/non-Pro uses the default Lora font and gates font selection behind `See Pro`.
9. Pro exposes all quote fonts, themes, speech controls, and reminder-time control.
10. Replay, Next, Favorite, Share, settings, Back, streak effect, Pro spin, and notifications work without regression.

## Remaining Google Play work

- Create/configure the Play Console app and products.
- Configure `qow_lifetime` as a non-consumable one-time product.
- Set RevenueCat production restore behavior to **Transfer to new App User ID** and verify the sandbox override separately.
- Supply the real RevenueCat Google Play public SDK key outside source.
- Inject a private upload signing key outside the repository.
- Finalize/host Privacy Policy and Terms, add production paywall links/disclosures, and complete Play Data Safety from the final SDK inventory.
- Test purchase, cancellation, pending purchase, expiry, refund/revocation, reinstall, second-device restore, and offline behavior through a Play testing track.

## Development discipline

```text
spec -> implement -> CI -> physical-device test -> review -> release
```

Feature scope remains locked. Prefer defects, compliance, verification, release hygiene, and submission assets over new product scope.
