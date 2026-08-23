#!/usr/bin/env bash
set -euo pipefail

ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
CMDLINE_VERSION="12266719"
GRADLE_VERSION="9.5.0"

mkdir -p "$ANDROID_HOME/cmdline-tools" "$HOME/.local"

if [ ! -x "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" ]; then
  tmp="$(mktemp -d)"
  curl -fsSL "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_VERSION}_latest.zip" -o "$tmp/cmdline.zip"
  unzip -q "$tmp/cmdline.zip" -d "$tmp"
  rm -rf "$ANDROID_HOME/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest"
  rm -rf "$tmp"
fi

export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
yes | sdkmanager --licenses >/dev/null || true
sdkmanager "platform-tools" "platforms;android-36" "build-tools;36.0.0"

GRADLE_HOME="$HOME/.local/gradle-$GRADLE_VERSION"
if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  tmp="$(mktemp -d)"
  curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$tmp/gradle.zip"
  unzip -q "$tmp/gradle.zip" -d "$HOME/.local"
  rm -rf "$tmp"
fi

cat >> "$HOME/.bashrc" <<EOF
export ANDROID_HOME="$ANDROID_HOME"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$GRADLE_HOME/bin:\$PATH"
EOF

export PATH="$GRADLE_HOME/bin:$PATH"

echo "Android SDK + Gradle ready."
echo "Build with: gradle :app:assembleDebug"
