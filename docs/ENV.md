# Worker host environment (macOS)

Forma workers need **JDK 17+** and an **Android SDK** with platform **34**
(sample `compileSdk`; Compose AAR metadata) and optionally **33** (`targetSdk`).

## Quick start (this Mac)

```bash
# Already installed on the Forma worker host (no sudo):
#   brew install openjdk@17
#   brew install --cask android-commandlinetools
#   sdk packages: platforms;android-34, platforms;android-33, platform-tools,
#                 build-tools 33.0.2 + 34.0.0

source scripts/env-mac.sh

# Point Gradle at the SDK (gitignored):
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties

# Verify
java -version
cd plugins && ./gradlew build
cd ../application && ./gradlew build
cd ../includer && ./gradlew build
cd ../depgen && ./gradlew build
```

## Paths used on this host

| Tool | Location |
|------|----------|
| JDK 17 | `/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home` |
| Android SDK root | `/usr/local/share/android-commandlinetools` |
| cmdline-tools | `$ANDROID_HOME/cmdline-tools/latest` |

`openjdk@17` is Homebrew **keg-only** (not on system `PATH` until you export `JAVA_HOME`). Prefer it over `brew install --cask temurin@17`, which requires **sudo** for the `.pkg` installer and fails in non-interactive cron.

## One-time install (fresh machine)

```bash
brew install openjdk@17
brew install --cask android-commandlinetools

export JAVA_HOME="/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export ANDROID_HOME="/usr/local/share/android-commandlinetools"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

yes | sdkmanager --licenses
sdkmanager --install \
  "platforms;android-34" \
  "platforms;android-33" \
  "platform-tools" \
  "build-tools;33.0.2" \
  "build-tools;34.0.0"

printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties
```

Optional system-wide JVM registration (needs sudo, not required for Gradle):

```bash
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

## Verified on 2026-07-10 (F-001) / re-verified 2026-07-11 (F-003, F-013)

- `java` / `javac` 17.0.19 (Homebrew OpenJDK)
- `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (AGP compile dep **8.1.2**)
- `includer/`: `./gradlew build` → **BUILD SUCCESSFUL**
- `depgen/`: `./gradlew build` → **BUILD SUCCESSFUL**
- `application/`: `./gradlew build` → **BUILD SUCCESSFUL** (compileSdk **34**,
  targetSdk 33, Compose compiler **1.5.10** / Kotlin **1.9.22**, F-018)
- Gradle wrappers: **8.7** (all roots; was 8.3/8.4 split)
- Toolchain note: plugins compile AGP matches sample forced AGP (no 7.4.2 skew)

## Shell profile

`~/.zshrc` on the worker exports `JAVA_HOME` / `ANDROID_HOME` so interactive shells pick them up. Cron / Hermes sessions should still `source scripts/env-mac.sh` (or set the same vars) because non-interactive jobs may not load `.zshrc`.
