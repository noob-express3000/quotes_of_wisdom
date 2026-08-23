# Quotes of Wisdom — M0

This is the first implementation checkpoint for the Shipaton app.

## What exists

- Android-only Kotlin project
- Jetpack Compose + Material 3
- Single `app` module
- Working home-screen shell based on the notebook sketches
- 60/30/10-style palette foundation
- FREE state shown in the UI
- Replay and Next controls present but intentionally unwired
- No login
- No backend
- No RevenueCat yet
- No TTS yet
- GitHub Actions build workflow for remote APK builds

## Toolchain

- Android Gradle Plugin: 9.3.0
- Gradle: 9.5.0
- Kotlin / Compose compiler plugin: 2.3.21
- Compose BOM: 2026.04.01 (Compose 1.11 line)
- compileSdk / targetSdk: 36
- minSdk: 23
- JDK: 17

## First local build on Windows

The archive does not contain a pre-generated Gradle wrapper JAR.

From PowerShell in the project directory:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\bootstrap-gradle.ps1
.\gradlew.bat :app:assembleDebug
```

The debug APK will be at:

```text
app\build\outputs\apk\debug\app-debug.apk
```

You can also push the project to GitHub and run the included `Android` workflow. That builds the APK remotely, keeping the old laptop out of the heavy build path.

## M1

Next checkpoint:

1. `Quote` model
2. bundled `quotes.json`
3. repository
4. shuffle-bag quote deck
5. `HomeViewModel`
6. wire `Next`
7. preserve the current UI shell

The product idea remains unchanged.
