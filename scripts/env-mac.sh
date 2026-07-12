#!/usr/bin/env bash
# Source this on the Forma worker Mac before Gradle:
#   source scripts/env-mac.sh
#
# Install (one-time, no sudo required for these paths):
#   brew install openjdk@17
#   brew install --cask android-commandlinetools
#   export JAVA_HOME="/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
#   export ANDROID_HOME="/usr/local/share/android-commandlinetools"
#   yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses
#   "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --install \
#     "platforms;android-34" "platforms;android-33" "platform-tools" \
#     "build-tools;33.0.2" "build-tools;34.0.0"
#   printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties
#
# Note: brew install --cask temurin@17 needs sudo for the system pkg installer;
# openjdk@17 (formula) is keg-only and does not need sudo.

export JAVA_HOME="${JAVA_HOME:-/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home}"
export ANDROID_HOME="${ANDROID_HOME:-/usr/local/share/android-commandlinetools}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

if [[ ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "env-mac.sh: java not found at $JAVA_HOME/bin/java" >&2
  return 1 2>/dev/null || exit 1
fi

if [[ ! -d "$ANDROID_HOME/platforms/android-34" ]]; then
  echo "env-mac.sh: warning: Android platform 34 missing under $ANDROID_HOME (sample compileSdk)" >&2
fi
if [[ ! -d "$ANDROID_HOME/platforms/android-33" ]]; then
  echo "env-mac.sh: warning: Android platform 33 missing under $ANDROID_HOME" >&2
fi
