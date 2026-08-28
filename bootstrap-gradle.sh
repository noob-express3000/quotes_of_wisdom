#!/usr/bin/env bash
set -euo pipefail

VERSION="9.5.0"
ROOT="$(cd "$(dirname "$0")" && pwd)"
BOOTSTRAP="$ROOT/.bootstrap-gradle"
ZIP="$BOOTSTRAP/gradle-$VERSION-bin.zip"
HOME_DIR="$BOOTSTRAP/gradle-$VERSION"

mkdir -p "$BOOTSTRAP"

if [ ! -x "$HOME_DIR/bin/gradle" ]; then
  curl -fL "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ZIP"
  unzip -q -o "$ZIP" -d "$BOOTSTRAP"
  rm -f "$ZIP"
fi

"$HOME_DIR/bin/gradle" wrapper --gradle-version "$VERSION"

echo
echo "Gradle wrapper generated."
echo "Run: ./gradlew :app:assembleDebug"

