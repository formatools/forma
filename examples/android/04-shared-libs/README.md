# 04 — shared-libs (Android)

**Goal:** Shared pure-JVM and Android libraries outside features.

## Features introduced
- `library` (JVM)
- `util` (JVM helpers)
- `androidUtil` (Android helpers, no res/)
- `androidLibrary` (shared Android library — **cannot** depend on `impl`)

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
05 adds `widget` + `uiLibrary`.
