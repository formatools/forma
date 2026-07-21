# Worker host environment (macOS)

Forma workers need **JDK 21** (build/daemon JVM; F-018/F-019) and an **Android SDK**
with platform **37** (sample `compileSdk` / `targetSdk` after F-087; AndroidX core
1.19 AAR metadata). Platforms **36**/**35** remain useful for intermediate ceilings.
Android app language level / `jvmTarget` stays at the project’s configured
`JavaVersion` (sample default still 1.8) — raising bytecode is a separate decision
from the daemon JDK.

## Quick start (this Mac)

```bash
# Already installed on the Forma worker host (no sudo):
#   brew install openjdk@21
#   brew install --cask android-commandlinetools
#   sdk packages: platforms android-37.0 (+ symlink android-37), 36, 35, …
#                 build-tools 37/36/35, platform-tools

source scripts/env-mac.sh

# Point Gradle at the SDK (gitignored):
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties

# Verify
java -version   # expect 21.x
cd plugins && ./gradlew --version   # Gradle 9.6.1 + JVM 21
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
  "platforms;android-37.0" \
  "platforms;android-36" \
  "platforms;android-35" \
  "platform-tools" \
  "build-tools;37.0.0" \
  "build-tools;36.0.0" \
  "build-tools;35.0.0"
# AGP resolves compileSdk=37 as platforms/android-37
ln -sfn android-37.0 "$ANDROID_HOME/platforms/android-37"

printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties
```

Optional system-wide JVM registration (needs sudo, not required for Gradle):

```bash
sudo ln -sfn /usr/local/opt/openjdk@21/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

## Verified on 2026-07-21 (F-087 — AndroidX / SDK ceiling)

- `java` / `javac` **21.x** (Homebrew OpenJDK 21) via `scripts/env-mac.sh`
- Gradle wrappers: **9.6.1** (all roots; F-019)
- AGP **9.3.0** lockstep (`plugins/android` compile + sample `agpVersion`)
- Kotlin **2.3.21** (Gradle embedded); KSP **2.3.10**
- Compose UI **1.11.4** / compiler **2.3.21** (+ Kotlin Compose Compiler plugin when `compose=true`)
- Sample SDK: min **23** / target **37** / compile **37** (platform package `android-37.0`, AGP dir `android-37`)
- AndroidX ceiling (stable, AAR-probed): core **1.19.0**, activity **1.13.0**, lifecycle **2.11.0**, room **2.8.4**, material **1.14.0**; paging **2.1.2** kept (API rewrite separate)
- CI: Temurin **21** + platforms 37/36/35 (`.github/workflows/main.yml`)
- F-019 Phase 1 + F-086 ksp/built-in Kotlin + F-087 ceiling

See `docs/PROGRESS.md` for the latest host build tails.

## Shell profile

`~/.zshrc` on the worker should export `JAVA_HOME` / `ANDROID_HOME` so interactive
shells pick them up (prefer openjdk@21). Cron / Hermes sessions should still
`source scripts/env-mac.sh` (or set the same vars) because non-interactive jobs
may not load `.zshrc`.
