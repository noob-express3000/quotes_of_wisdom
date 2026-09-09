# myQuote

myQuote is an Android quote app built for RevenueCat Shipaton 2026. It uses Kotlin, Jetpack Compose, Android Text-to-Speech, and RevenueCat.

The app is a working release candidate. App-owned content and state remain local. RevenueCat handles purchase and entitlement traffic, and a user-selected network-capable Text-to-Speech engine may use its own network service. There are no app accounts, ads, analytics SDK, or custom servers. Judge/Test Store builds and the eventual Google Play production release are handled separately.

## Current status

- Current source version: **1.0.2** (`versionCode 3`)
- Billing validation: RevenueCat Test Store
- Google Play production billing: pending Play Console access/configuration and Play testing
- Package/application ID: `com.shipaton.quotesofwisdom` (retained as the stable technical namespace)
- Public repository: `noob-express3000/quotes_of_wisdom` (retained for link/history stability)

The product brand is **myQuote**. Legacy package, repository, storage, and historical release identifiers are not user-facing branding and are intentionally preserved to avoid unnecessary migration or provenance breakage.

## Highlights

- 1,063 curated quotes from 356 authors, with provenance records
- no-repeat browsing and favorite-based personalization
- favorites, attributed sharing, daily streaks, and local reminders
- Android Text-to-Speech with replay, engine, voice, and speed controls
- 100 three-color themes using a strict 60/30/10 system
- 30-day app-controlled trial, text-only grace period, locked state, and Pro access
- RevenueCat weekly, monthly, lifetime, and restore-purchase paths
- full-screen Jetpack Compose UI with display-cutout handling
- no ads, app login, analytics SDK, or custom backend

## Build from source

### Requirements

- JDK 17
- Android SDK Platform 36
- Android SDK Build Tools 36.0.0
- Git
- `curl` and `unzip` on macOS/Linux for the one-time Gradle bootstrap

The repository pins Gradle 9.5.0. Gradle does not need to be installed globally.

### macOS / Linux

```bash
git clone https://github.com/noob-express3000/quotes_of_wisdom.git
cd quotes_of_wisdom
chmod +x bootstrap-gradle.sh
./bootstrap-gradle.sh
./gradlew :app:assembleDebug
```

APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Windows

```powershell
git clone https://github.com/noob-express3000/quotes_of_wisdom.git
cd quotes_of_wisdom
Set-ExecutionPolicy -Scope Process Bypass
.\bootstrap-gradle.ps1
.\gradlew.bat :app:assembleDebug
```

APK:

```text
app\build\outputs\apk\debug\app-debug.apk
```

### QA / judge build

Use the release-derived QA variant for judge evaluation and physical-device testing:

```bash
./gradlew :app:assembleQa
```

APK:

```text
app/build/outputs/apk/qa/app-qa.apk
```

QA remains debuggable, uses the release configuration where compatible, and connects to RevenueCat's Test Store. It is **not** a Google Play production artifact and Test Store purchases do not charge real money.

A local QA build uses that computer's Android debug keystore. GitHub Actions uses a stable test-only CI signer so later CI judge builds can update earlier ones. Google Play production signing uses a separate identity.

## RevenueCat configuration

Debug and QA builds use the RevenueCat Test Store public SDK key included in the Android client configuration.

A release build reads the public RevenueCat Google Play Android SDK key from the Gradle property `REVENUECAT_API_KEY`:

```bash
./gradlew :app:bundleRelease -PREVENUECAT_API_KEY=goog_your_public_sdk_key
```

Release builds fail before compilation if the key is missing, is a Test Store key, or does not begin with `goog_`. Production signing material is not included in the repository.

RevenueCat configuration expected by the app:

| Type | Identifier | Grants |
|---|---|---|
| Entitlement | `pro_access` | All Pro access |
| Weekly product | `qow_weekly` | `pro_access` |
| Monthly product | `qow_monthly` | `pro_access` |
| Lifetime product | `qow_lifetime` | `pro_access` |

The `qow_*` product IDs are stable technical catalog identifiers and do not represent the public product name.

All three products must be attached to RevenueCat's Current Offering as weekly, monthly, and lifetime packages. The UI displays localized prices supplied by RevenueCat/the store and does not infer location or invent fallback prices.

The app has no login system, so RevenueCat is configured without a custom App User ID and uses RevenueCat-generated anonymous IDs. The RevenueCat project should use **Transfer to new App User ID** restore behavior so purchases can be restored after reinstalling or moving to another device.

RevenueCat `CustomerInfo` is the paid-entitlement source of truth. myQuote does not maintain a second persistent Boolean Pro cache that can outlive RevenueCat's subscription-expiration logic.

## Architecture

```text
Jetpack Compose UI
        |
        v
HomeViewModel / UI state
        |
        +---- AssetQuoteRepository -> bundled quotes.json
        |
        +---- AppPreferencesRepository -> Android DataStore
        |
        +---- TtsController -> Android TextToSpeech
        |
        +---- RevenueCatController -> RevenueCat SDK
        |
        +---- DailyWisdomNotifications -> AlarmManager / notifications
```

Important source areas retain the original technical namespace:

```text
app/src/main/java/com/shipaton/quotesofwisdom/MainActivity.kt
app/src/main/java/com/shipaton/quotesofwisdom/ui/
app/src/main/java/com/shipaton/quotesofwisdom/billing/
app/src/main/java/com/shipaton/quotesofwisdom/speech/
app/src/main/java/com/shipaton/quotesofwisdom/notifications/
app/src/main/java/com/shipaton/quotesofwisdom/data/
app/src/main/assets/quotes.json
```

## Verification

Run the same core checks used by CI:

```bash
python3 tools/validate_production_quotes.py app/src/main/assets/quotes.json
./gradlew :app:testDebugUnitTest :app:lintQa :app:assembleDebug :app:assembleQa
```

GitHub Actions validates the quote database, runs unit tests and Android lint, builds Debug and QA APKs, checks the CI signature, and validates the Release app-bundle path with a non-production CI key.

## Toolchain

- Android Gradle Plugin: 9.3.0
- Gradle: 9.5.0
- Kotlin / Compose compiler plugin: 2.3.21
- Compose BOM: 2026.04.01
- RevenueCat Android SDK: 10.18.1
- compileSdk / targetSdk: 36
- minSdk: 23 (Android 6.0)
- JDK: 17

## Documentation

- [`docs/PROJECT_STATE.md`](docs/PROJECT_STATE.md) — implementation and release state
- [`docs/product-spec.md`](docs/product-spec.md) — v1 behavior and visual rules
- [`docs/SHIPATON_SUBMISSION.md`](docs/SHIPATON_SUBMISSION.md) — judge path, demo script, and submission copy
- [`docs/RELEASE_CHECKLIST.md`](docs/RELEASE_CHECKLIST.md) — judge and Google Play gates
- [`docs/PRIVACY_POLICY_DRAFT.md`](docs/PRIVACY_POLICY_DRAFT.md) — working privacy policy
- [`docs/TERMS_OF_USE_DRAFT.md`](docs/TERMS_OF_USE_DRAFT.md) — working product terms
- [`docs/DATA_SAFETY_DRAFT.md`](docs/DATA_SAFETY_DRAFT.md) — current Google Play disclosure mapping
- [`docs/quote-curation-policy.md`](docs/quote-curation-policy.md) — corpus acceptance rules
- [`docs/quote-verification-ledger.md`](docs/quote-verification-ledger.md) — quote-level verification record

## License

The original application code, build tooling, documentation, and corpus selection/arrangement/metadata are available under the [Apache License 2.0](LICENSE).

Individual historical quotation texts remain attributed to their respective authors and are not claimed as original project authorship. See [`NOTICE`](NOTICE) and the quote provenance/rights records under [`docs/`](docs/).
