# 08 — deps-catalog (Android)

**Goal:** External dependency catalogs via `projectDependencies`.

## Features introduced
- Bare GAV → auto-named accessor (`libs.jakewhartonTimber`)
- `library(gav, name = …)` stable names
- `bundle(name, …)` → `libs.bundles.*`
- `plugin(id, version)` catalog entry (declared; optional apply)
- Consuming catalogs with `deps(libs…)`

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
09 adds `testUtil` + `androidTestUtil` (and documents `androidNative`).
