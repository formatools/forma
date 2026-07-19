#!/usr/bin/env bash
# Source this on the Forma worker Mac before Gradle:
#   source scripts/env-mac.sh
#
# Install (one-time, no sudo required for these paths):
#   brew install openjdk@21
#   brew install --cask android-commandlinetools
#   export JAVA_HOME="/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
#   # Apple Silicon Homebrew prefix is /opt/homebrew instead of /usr/local
#   export ANDROID_HOME="/usr/local/share/android-commandlinetools"
#   yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses
#   "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --install \
#     "platforms;android-34" "platforms;android-33" "platform-tools" \
#     "build-tools;33.0.2" "build-tools;34.0.0"
#   printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties
#
# Note: brew install --cask temurin@21 needs sudo for the system pkg installer;
# openjdk@21 (formula) is keg-only and does not need sudo.
#
# Build JVM is JDK 21 (F-018). Android/app bytecode stays at the project's
# configured JavaVersion (sample still 1.8 / jvmTarget from settings) — do not
# confuse the daemon JDK with language level.

_forma_java_home_candidates=(
  /usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
  /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
)

if [[ -z "${JAVA_HOME:-}" ]]; then
  for _candidate in "${_forma_java_home_candidates[@]}"; do
    if [[ -x "$_candidate/bin/java" ]]; then
      export JAVA_HOME="$_candidate"
      break
    fi
  done
fi
unset _candidate _forma_java_home_candidates

export JAVA_HOME="${JAVA_HOME:-/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home}"
export ANDROID_HOME="${ANDROID_HOME:-/usr/local/share/android-commandlinetools}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

if [[ ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "env-mac.sh: java not found at $JAVA_HOME/bin/java" >&2
  echo "env-mac.sh: install with: brew install openjdk@21" >&2
  return 1 2>/dev/null || exit 1
fi

if [[ ! -d "$ANDROID_HOME/platforms/android-35" ]]; then
  echo "env-mac.sh: warning: Android platform 35 missing under $ANDROID_HOME (sample compileSdk)" >&2
fi
if [[ ! -d "$ANDROID_HOME/platforms/android-34" ]]; then
  echo "env-mac.sh: warning: Android platform 34 missing under $ANDROID_HOME" >&2
fi
if [[ ! -d "$ANDROID_HOME/platforms/android-33" ]]; then
  echo "env-mac.sh: warning: Android platform 33 missing under $ANDROID_HOME" >&2
fi
