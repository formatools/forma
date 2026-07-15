# 06 — compose (Android)

**Goal:** Jetpack Compose via project default / per-target flags and `composeWidget`.

## Features introduced
- `androidProjectConfiguration(compose = true, composeCompilerVersion = …)`
- Per-target `compose = true` on `impl` / `androidApp` / `androidBinary`
- `composeWidget` (always enables Compose)
- Compose libraries still declared via `deps(...)` (Forma does not inject artifacts)

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
07 wires two features without impl→impl.
