# Quotes of Wisdom

Android-only Shipaton project built with Kotlin + Jetpack Compose.

## Current checkpoint

M0 is compile-verified and installs on a physical Android device.

Current implementation:

- Android-only Kotlin project
- Jetpack Compose + Material 3
- single `app` module
- working home-screen shell based on the notebook sketches
- three-color 60/30/10 theme foundation
- no login
- no custom backend
- GitHub Actions remote APK build
- universal browser development through GitHub Codespaces

Product behavior and commercial decisions are frozen in [`docs/product-spec.md`](docs/product-spec.md).

## Universal workflow

GitHub is the source of truth. The project does not depend on one development computer.

### Browser / any computer

1. Open this repository on GitHub.
2. Choose **Code -> Codespaces -> Create codespace on main**.
3. Wait for the dev container setup to finish.
4. Edit the Kotlin/project files in the browser.
5. Build from the Codespaces terminal with:

```bash
gradle :app:assembleDebug
```

6. Commit and push from Codespaces.

Every push also runs the canonical GitHub Actions Android build. The resulting debug APK is available under the successful workflow run's **Artifacts** section as `quotes-of-wisdom-debug`.

This makes the normal development loop:

```text
browser / laptop / other computer
          |
          v
       GitHub
          |
          v
   GitHub Actions
          |
          v
       APK artifact
          |
          v
   physical Android phone
```

### Windows local build

The repository does not commit a binary Gradle wrapper JAR yet. Generate the wrapper once:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\bootstrap-gradle.ps1
.\gradlew.bat :app:assembleDebug
```

### macOS / Linux local build

```bash
chmod +x bootstrap-gradle.sh
./bootstrap-gradle.sh
./gradlew :app:assembleDebug
```

Local Android builds also require an Android SDK containing API 36. If you do not want to configure that locally, use Codespaces or GitHub Actions instead.

## Toolchain

- Android Gradle Plugin: 9.3.0
- Gradle: 9.5.0
- Kotlin / Compose compiler plugin: 2.3.21
- Compose BOM: 2026.04.01 (Compose 1.11 line)
- compileSdk / targetSdk: 36
- minSdk: 23
- JDK: 17

## Product rules currently frozen

- exactly three colors per theme
- perceptual 60/30/10 visual hierarchy
- 20 themes for v1
- 30-day automatic install trial
- days 31-33: text-only grace period, TTS disabled
- day 34 onward: app locked until Pro
- upgrade notifications for at least seven post-trial days
- one trial TTS voice and fixed TTS speed
- Pro unlocks all themes, voice selection and adjustable speech speed
- RevenueCat `pro_access` entitlement for all paid products
- approximate pricing targets: $0.50 weekly, $1 monthly, $70 lifetime
- quote text scroll control

## M1

Next checkpoint:

1. `Quote` model
2. bundled `quotes.json`
3. classification/genre metadata
4. repository
5. shuffle-bag quote deck
6. `HomeViewModel`
7. wire `Next`
8. preserve the current UI shell
