# Quotes of Wisdom

Quotes of Wisdom is a local-first Android app built for RevenueCat Shipaton 2026 with Kotlin, Jetpack Compose, Android Text-to-Speech, and RevenueCat.

The product is intentionally focused: one good quote, a quiet daily ritual, and no account or custom backend. The v1 app is a release candidate; judge distribution and Google Play production release are tracked separately.

## Highlights

- 1,063 curated, provenance-tracked quotes from 356 authors
- no-repeat browsing with lightweight favorite-based personalization
- favorites, attributed sharing, daily streaks, and local reminders
- Android Text-to-Speech with replay, engine, voice, and speed controls
- 100 hand-built three-color themes using a strict 60/30/10 system
- 30-day trial, text-only grace period, locked state, and Pro access
- RevenueCat weekly, monthly, lifetime, and restore-purchase paths
- immersive, display-cutout-aware Jetpack Compose UI
- no ads, login, analytics SDK, or custom backend

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

### Optimized QA / judge build

The normal debug APK is deliberately debuggable and can run noticeably slower than the release-like QA variant. Use the QA variant for performance testing and direct judge evaluation:

```bash
./gradlew :app:assembleQa
```

APK:

```text
app/build/outputs/apk/qa/app-qa.apk
```

QA is minified, resource-shrunk, debuggable, and connected to RevenueCat's Test Store. It is **not** a Google Play production artifact and its test purchases do not charge real money.

A local QA build uses that computer's Android debug keystore. GitHub Actions injects a stable, test-only CI signer so successive CI-produced judge APKs can update one another. The first move from an older/transiently signed APK requires one uninstall; production Play signing will use a separate identity.

## RevenueCat configuration

Debug and QA builds use the RevenueCat Test Store public SDK key included in the Android client configuration. This allows the full monetization flow to be evaluated without private credentials.

A release build reads the public RevenueCat Google Play Android SDK key from the Gradle property `REVENUECAT_API_KEY`:

```bash
./gradlew :app:bundleRelease -PREVENUECAT_API_KEY=goog_your_public_sdk_key
```

Release builds fail before compilation when the key is missing, is a Test Store key, or is not a Google Play key beginning with `goog_`. Production signing configuration and private keystore material are intentionally absent from the repository; the command above validates/builds the release path but does not by itself create a production-signed upload.

RevenueCat configuration expected by the app:

| Type | Identifier | Grants |
|---|---|---|
| Entitlement | `pro_access` | All Pro access |
| Weekly product | `qow_weekly` | `pro_access` |
| Monthly product | `qow_monthly` | `pro_access` |
| Lifetime product | `qow_lifetime` | `pro_access` |

All three products must be attached to RevenueCat's Current Offering as weekly, monthly, and lifetime packages. The UI displays only localized prices supplied by RevenueCat/the store; it does not infer location or invent fallback prices.

## Architecture

The app is intentionally local-first and has no login or custom backend.

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

Important source areas:

```text
app/src/main/java/com/shipaton/quotesofwisdom/MainActivity.kt
app/src/main/java/com/shipaton/quotesofwisdom/ui/home/
app/src/main/java/com/shipaton/quotesofwisdom/ui/settings/
app/src/main/java/com/shipaton/quotesofwisdom/ui/paywall/
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

GitHub Actions validates the production quote database, runs unit tests and Android lint, builds debug and optimized QA APKs, verifies their stable CI signature, and validates the minified release app-bundle path with a non-production CI key.

Current automated tests cover quote-deck behavior, local access policy, RevenueCat entitlement-state transitions, and theme palette invariants. Physical-device acceptance and paywall interaction regression coverage remain explicit release gates.

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

- [`docs/PROJECT_STATE.md`](docs/PROJECT_STATE.md) — canonical implementation and release state
- [`docs/product-spec.md`](docs/product-spec.md) — frozen v1 behavior and visual rules
- [`docs/SHIPATON_SUBMISSION.md`](docs/SHIPATON_SUBMISSION.md) — judge path, demo script, and submission copy
- [`docs/RELEASE_CHECKLIST.md`](docs/RELEASE_CHECKLIST.md) — judge and Google Play gates
- [`docs/PRIVACY_POLICY_DRAFT.md`](docs/PRIVACY_POLICY_DRAFT.md) — policy draft requiring owner/contact details
- [`docs/TERMS_OF_USE_DRAFT.md`](docs/TERMS_OF_USE_DRAFT.md) — subscription and product-terms draft
- [`docs/DATA_SAFETY_DRAFT.md`](docs/DATA_SAFETY_DRAFT.md) — current Google Play disclosure mapping
- [`docs/quote-curation-policy.md`](docs/quote-curation-policy.md) — corpus acceptance rules
- [`docs/quote-verification-ledger.md`](docs/quote-verification-ledger.md) — quote-level verification record

## License

The original application code, build tooling, original documentation, and original corpus selection/arrangement/metadata are available under the [Apache License 2.0](LICENSE).

Individual historical quotation texts remain attributed to their respective authors and are not claimed as original project authorship. The code license does not create new rights in those underlying words. See [`NOTICE`](NOTICE) and the quote provenance/rights records under [`docs/`](docs/).
