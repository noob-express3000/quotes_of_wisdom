# myQuote — Canonical Project State

_Last updated: 2026-09-09_

Read this file first when resuming work. GitHub `main` is the source of truth.

## Product identity

- **Public product name:** `myQuote`
- **Android package/application ID:** `com.shipaton.quotesofwisdom`
- **Repository:** `noob-express3000/quotes_of_wisdom`
- **Launcher/icon mark:** Gothic `Q`

The package, repository, existing storage keys, RevenueCat product IDs, and historical release identifiers intentionally retain their original technical names. They are compatibility/provenance identifiers, not current public branding.

## Release status

| Area | State |
|---|---|
| v1 feature scope | Frozen |
| RevenueCat Test Store integration | Implemented |
| RevenueCat anonymous-user hardening | Implemented |
| RevenueCat cached entitlement handling | Implemented |
| Stable CI QA signing | Implemented |
| Open-source license | Apache-2.0 |
| Current product brand | `myQuote` |
| Original/pre-rename judge releases | `v1.0.0` and `v1.0.1` retained for provenance |
| Renamed judge candidate | `v1.0.2` / `versionCode 3` |
| Google Play production configuration | Pending |

The `v1.0.2` candidate changes product branding and documentation without changing the package/application ID or monetization model. It must pass the standard CI and physical-device smoke path before becoming the preferred judge artifact.

## Product

myQuote is an Android-only, local-first quote app built with Kotlin and Jetpack Compose.

- No app login, ads, analytics SDK, or custom backend.
- 1,063 curated quotes from 356 authors across 12 classifications.
- Quotes, favorites, themes, streaks, trial state, and reminders are local.
- Android Text-to-Speech supplies narration; network-capable voices may use their provider's network service.
- RevenueCat is authoritative for paid `pro_access` entitlement.
- 100 themes: 2 Trial and 98 Pro.
- Pro includes TTS engine/voice/speed controls, quote fonts, custom reminder time, and all themes.

## Current deployment status

myQuote is a **working Android release candidate**, not a production Google Play release.

The app has been built and exercised against RevenueCat's Test Store. Production Google Play publication still requires Play Console access/configuration, the real RevenueCat Google Play public SDK key, production signing, Play product setup, policy URLs, and real Play testing. The remaining gap is store configuration and production validation rather than an unfinished application prototype.

## Access lifecycle

```text
DAY 0-30   TRIAL_ACTIVE
DAY 31-33  GRACE_TEXT_ONLY
DAY 34+    LOCKED

ANY STATE + active RevenueCat `pro_access` -> PRO
```

The app-controlled trial remains intentionally local. Clearing app data can reset local trial state; this is a known tradeoff of the no-account/no-backend model rather than a paid-entitlement mechanism.

## RevenueCat identity and restore model

myQuote has no authentication system, so RevenueCat is configured **without a custom App User ID**. The RevenueCat SDK generates and caches an anonymous App User ID for the installation.

The app does not derive billing identity from Android ID, package signing material, or hardware metadata.

For Test Store and Google Play restore behavior, the RevenueCat project should use:

```text
Restore behavior: Transfer to new App User ID
```

The app exposes Restore Purchases so a purchase can be recovered after reinstalling or moving to another device without adding a myQuote account system.

## Entitlement behavior

RevenueCat `CustomerInfo` is the paid-entitlement source of truth.

- Startup requests `CustomerInfo` and installs an update listener.
- RevenueCat's SDK cache provides restart/offline entitlement behavior.
- myQuote does not maintain a second persistent Boolean Pro cache.
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

The `qow_*` identifiers are stable catalog IDs. Runtime prices come from RevenueCat/store localized pricing; the app does not request location or invent fallback prices.

## Build types

| Build | RevenueCat | Signing | Intended use |
|---|---|---|---|
| Debug | Test Store | Local/stable CI debug signer | Development |
| QA | Test Store | Stable CI test signer in CI | Device QA / judges |
| Release | Google Play key required | Production signing not in repo | Play Store AAB |

Stable CI certificate SHA-256:

`72:94:42:18:4D:E0:36:0C:72:8F:56:CE:DA:A5:36:90:09:4A:0A:60:00:3D:C4:8E:CD:D3:7D:33:F3:7A:0B:03`

The committed CI keystore is test-only and must never become the Google Play upload or app-signing key.

## v1.0.2 verification gate

1. CI: quote validation, unit tests, lint, Debug/QA builds, Release bundle-path validation, stable signer verification, artifact upload.
2. Install the stable CI QA APK and confirm the launcher/app identity displays **myQuote**.
3. Confirm Trial paywall, Info and Close behavior.
4. Confirm Test Store prices `$0.99`, `$2.99`, `$29.99`.
5. Complete Lifetime Test Store purchase and confirm Pro.
6. Force-stop/reopen and test RevenueCat cached entitlement behavior offline.
7. After setting sandbox restore behavior to **Transfer to new App User ID**, reinstall and Restore Purchases.
8. Confirm Trial/non-Pro default font and all Pro feature gates.
9. Confirm Replay, Next, Favorite, Share, settings, Back, streak effect, Pro spin, and notifications.
10. Confirm demo notification displays **myQuote**.

## Remaining Google Play work

- Create/configure the Play Console app using package `com.shipaton.quotesofwisdom` and public product name **myQuote**.
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
