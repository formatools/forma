# 08 — deps-catalog (Android)

**Goal:** External dependency catalogs via `projectDependencies` — the **house
style** for third-party deps in Forma (F-083). Prefer this over typed
`build-dependencies/` objects for new apps.

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

## Docs
Full guide: [`docs/DEPS-CATALOG.md`](../../../docs/DEPS-CATALOG.md) (house style
§1; advanced typed catalogs §2).

## Next
09 adds `testUtil` + `androidTestUtil` (and documents `androidNative`).
