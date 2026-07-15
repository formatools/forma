# 09 — test-utils (Android)

**Goal:** Shared unit / instrumentation test helpers.

## Features introduced
- `testUtil` (shared unit-test code, no res/)
- `androidTestUtil` (shared Android test code)
- `testDependencies` / `androidTestDependencies` on `impl`

## androidNative (documented)
`androidNative` exists for NDK modules (suffix `native`). It has **no project-dep validation yet**.
Not required for this step; see agent skill `forma-android-targets`.

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
./gradlew :feature-hello-impl:testDebugUnitTest
```
