# 09 — test-utils (Android)

**Goal:** Shared unit / instrumentation test helpers — real **usage**, not empty modules.

## Features introduced
- `testUtil` (shared unit-test code, no res/) — used from `src/test` via main/`testDependencies`
- `androidTestUtil` (shared Android instrumented-test helpers) — used from `src/androidTest` via `androidTestDependencies`
- `testDependencies` / `androidTestDependencies` on `impl` accept **named GAVs and first-party project targets** (FormaDependency)

## Layout
- `common/test-util` — `assertPositive` used by `AdderTest`
- `common/android-test-util` — `AndroidChecks` used by `AdderAndroidTest`
- `feature/hello/impl` — unit + instrumented tests

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
./gradlew :feature-hello-impl:testDebugUnitTest
# Compile instrumented tests (no device required):
./gradlew :feature-hello-impl:assembleDebugAndroidTest
```

## Next
`androidNative` has its own ladder step: [13-android-native](../13-android-native).
Navigation ports: [12-navigation-ports](../12-navigation-ports) (F-112).
