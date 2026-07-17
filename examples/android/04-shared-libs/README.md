# 04 — shared-libs (Android)

**Goal:** Shared pure-JVM and Android libraries outside features — **role-typed**, no generic `androidLibrary`.

## Features introduced
- `library` (JVM)
- `util` (JVM helpers)
- `androidUtil` (Android helpers, no res/)
- (intentionally **no** `androidLibrary` — deprecated; see `docs/ANDROID-LIBRARY-DEPRECATION.md`)

## Layout
- `common/library` — pure JVM
- `common/util` — JVM helpers
- `common/android-util` — Android helpers
- `core/platform/android-util` — platform Android util depending on JVM `library` + `android-util`

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
05 adds `widget` + `uiLibrary`.
