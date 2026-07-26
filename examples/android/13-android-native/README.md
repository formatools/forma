# 13 — android-native (Android)

**Goal:** First-party NDK module via `androidNative`, consumed through JNI bindings.

## Features introduced
- `androidNative` (suffix `native`) — CMake/NDK leaf, **no** first-party project deps
- Matrix consumers: `androidUtil` / `androidApp` / `androidBinary` → `native`
- Pattern: **native** builds `.so`; **androidUtil** owns `System.loadLibrary` + `external` API

## Layout
- `common/hello/native` — CMake + `libforma_hello.so`
- `common/hello/android-util` — `HelloNative.greeting()` JNI façade
- `root-app` / `binary` — display native string

## Prerequisites
NDK + CMake in `ANDROID_HOME` (example uses CMake 3.22+ path API):

```bash
sdkmanager "ndk;28.2.13676358" "cmake;3.22.1"
```

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
./gradlew :common-hello-native:assembleDebug
```

## Notes
- `androidNative` does not take `dependencies =` today (leaf).
- Prefer JNI bindings on `androidUtil` rather than `impl` → `native` (impl has no native edge).
- **AGP namespace pitfall:** `packageName` cannot end with the Java keyword `native`
  (e.g. use `….hello.ndk`). The **project dir/suffix** must still be `native`
  for Forma validators (`common/hello/native` → `:common-hello-native`).
