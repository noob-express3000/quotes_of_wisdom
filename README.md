# Quotes of Wisdom

Android-only Shipaton project built with Kotlin + Jetpack Compose.

## Continue this project in another chat

Read [`docs/PROJECT_STATE.md`](docs/PROJECT_STATE.md) first. It is the canonical handoff checkpoint containing the frozen product decisions, completed milestones, architecture decisions, CI state, and exact next milestone.

Then read [`docs/product-spec.md`](docs/product-spec.md) for the detailed product/access specification.

## Current checkpoint

M0 and M1 are complete. M1 is compile-verified through GitHub Actions and merged into `main`.

Current implementation:

- Android-only Kotlin project
- Jetpack Compose + Material 3
- single `app` module
- three-color 60/30/10 theme foundation
- local bundled quote corpus with classification metadata
- asset-backed quote repository
- no-repeat shuffle deck
- `HomeViewModel` + `StateFlow`
- functional `Next` button
- no login
- no custom backend
- GitHub Actions remote APK build
- universal browser development through GitHub Codespaces

Replay remains intentionally unwired until M2 adds Android TTS.

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
- deterministic opaque device-scoped RevenueCat identity planned for reinstall-resistant trial history
- pricing: $0.99 weekly, $2.99 monthly, $29.99 lifetime; production UI uses RevenueCat/store-localized pricing
- quote text scroll control

## Next checkpoint: M2

M2 adds the real Android Text-to-Speech subsystem:

1. TTS controller/lifecycle
2. current-quote speech
3. functional Replay
4. stop/flush behavior when Next interrupts speech
5. ready/speaking/error state
6. unavailable-TTS fallback
7. lifecycle shutdown
8. CI + physical-device APK validation

See `docs/PROJECT_STATE.md` for the exact handoff state before coding.
