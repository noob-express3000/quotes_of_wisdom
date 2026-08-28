# Quotes of Wisdom

Quotes of Wisdom is a local-first Android app built for RevenueCat Shipaton 2026 with Kotlin, Jetpack Compose, Android Text-to-Speech, and RevenueCat.

The product is intentionally small: one good quote, a quiet daily ritual, and no account or custom backend.

## Build from source

### Requirements

- JDK 17
- Android SDK Platform 36
- Android SDK Build Tools 36.0.0
- Git
- `curl` and `unzip` on macOS/Linux for the one-time Gradle bootstrap

The repository pins Gradle 9.5.0. You do not need to install Gradle globally.

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

### Optimized QA build

The normal debug APK is deliberately debuggable and can run noticeably slower on low-end Android hardware. For performance testing, build the release-like QA variant:

```bash
./gradlew :app:assembleQa
```

APK:

```text
app/build/outputs/apk/qa/app-qa.apk
```

The QA variant is optimized and signed with the normal Android debug signing configuration so it is installable for testing. It uses the RevenueCat Test Store configuration and is not a production-store artifact.

## RevenueCat configuration

Debug and QA builds use the RevenueCat Test Store SDK key included in the Android client configuration so the monetization flow can be evaluated without private credentials.

A production release reads the public RevenueCat Google Play SDK key from the Gradle property `REVENUECAT_API_KEY`:

```bash
./gradlew :app:assembleRelease -PREVENUECAT_API_KEY=goog_your_public_sdk_key
```

Release builds fail before compilation when the key is missing, is a Test Store key, or is not a Google Play key beginning with `goog_`.

Production signing is intentionally kept out of the repository.

## What is implemented

- 1,063 curated and provenance-tracked quotes
- 356 authors and 12 classifications
- no-repeat quote browsing with lightweight favorite-based personalization
- favorites and sharing
- daily streak tracking
- Android Text-to-Speech with replay and automatic playback
- Pro engine, voice, voice-download, and speech-speed controls
- 100 three-color themes using the app's 60/30/10 design system
- 30-day trial, text-only grace period, locked state, and Pro access state
- RevenueCat weekly, monthly, and lifetime purchase paths
- RevenueCat `pro_access` entitlement and restore purchases
- one local daily reminder
- fixed 09:00 reminder for free access
- Pro-selectable reminder time
- reboot, clock-change, timezone-change, and app-update reminder rescheduling
- Android 13+ notification permission handling
- immersive cutout-aware UI

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
app/src/main/java/com/shipaton/quotesofwisdom/billing/
app/src/main/java/com/shipaton/quotesofwisdom/speech/
app/src/main/java/com/shipaton/quotesofwisdom/notifications/
app/src/main/java/com/shipaton/quotesofwisdom/data/
app/src/main/assets/quotes.json
```

## Toolchain

- Android Gradle Plugin: 9.3.0
- Gradle: 9.5.0
- Kotlin / Compose compiler plugin: 2.3.21
- Compose BOM: 2026.04.01
- compileSdk / targetSdk: 36
- minSdk: 23
- JDK: 17

## CI

GitHub Actions validates the production quote database, runs unit tests and Android lint, bootstraps the same pinned Gradle wrapper a clean machine uses, builds both test APKs, and verifies the minified release app-bundle path.

This makes GitHub the source of truth rather than any one development computer.

## Project documentation

Detailed implementation and product notes live under [`docs/`](docs/).

The final signing, RevenueCat, store-listing, and device checks are tracked in [`docs/RELEASE_CHECKLIST.md`](docs/RELEASE_CHECKLIST.md).
