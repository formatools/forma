# Worker host environment (macOS)

Forma workers need **JDK 21** (build/daemon JVM; F-018) and an **Android SDK**
with platform **34** (sample `compileSdk`; Compose AAR metadata) and optionally
**33** (`targetSdk`). Android app language level / `jvmTarget` stays at the
project’s configured `JavaVersion` (sample default still 1.8) — raising bytecode
is a separate decision from the daemon JDK.

## Quick start (this Mac)

```bash
# Already installed on the Forma worker host (no sudo):
#   brew install openjdk@21
#   brew install --cask android-commandlinetools
#   sdk packages: platforms;android-34, platforms;android-33, platform-tools,
#                 build-tools 33.0.2 + 34.0.0

source scripts/env-mac.sh

# Point Gradle at the SDK (gitignored):
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties

# Verify
java -version   # expect 21.x
cd plugins && ./gradlew --version   # Gradle 8.14.5 + JVM 21
cd ../plugins && ./gradlew build
cd ../application && ./gradlew build
cd ../includer && ./gradlew build
cd ../depgen && ./gradlew build
```

## Paths used on this host

| Tool | Location |
|------|----------|
| JDK 21 (Intel Homebrew) | `/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home` |
| JDK 21 (Apple Silicon) | `/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home` |
| Android SDK root | `/usr/local/share/android-commandlinetools` |
| cmdline-tools | `$ANDROID_HOME/cmdline-tools/latest` |

`scripts/env-mac.sh` probes both Homebrew prefixes. `openjdk@21` is Homebrew
**keg-only** (not on system `PATH` until you export `JAVA_HOME`). Prefer it over
`brew install --cask temurin@21`, which requires **sudo** for the `.pkg`
installer and fails in non-interactive cron.

## One-time install (fresh machine)

```bash
brew install openjdk@21
brew install --cask android-commandlinetools

# Intel:
export JAVA_HOME="/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
# Apple Silicon:
# export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"

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
sudo ln -sfn /usr/local/opt/openjdk@21/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

## Verified on 2026-07-19 (F-018 complete — 8.x terminal)

- `java` / `javac` **21.x** (Homebrew OpenJDK 21) via `scripts/env-mac.sh`
- Gradle wrappers: **8.14.5** (all roots; unified via #180 then #182)
- AGP **8.13.2** lockstep (`plugins/android` compile + sample `agpVersion`)
- Kotlin **2.0.21** (Gradle embedded); KSP **2.0.21-1.0.28**
- Compose compiler default **2.0.21** (+ Kotlin Compose Compiler plugin when `compose=true`)
- Sample SDK: min **23** / target **35** / compile **35** (install platform 35 for full sample builds; 34/33 still useful)
- CI: Temurin **21** all jobs (`.github/workflows/main.yml`)
- AGP 9 / Gradle 9 / absolute-latest AndroidX = **F-019** only with explicit OK

See `docs/PROGRESS.md` for the latest host build tails.

## Shell profile

`~/.zshrc` on the worker should export `JAVA_HOME` / `ANDROID_HOME` so interactive
shells pick them up (prefer openjdk@21). Cron / Hermes sessions should still
`source scripts/env-mac.sh` (or set the same vars) because non-interactive jobs
may not load `.zshrc`.
