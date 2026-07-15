# 07 — multi-feature (Android)

**Goal:** Two independent features composed at the root. No `impl` → `impl`.

## Features introduced
- Multi-feature wiring on composition roots
- Cross-feature collaboration only via `api` (here: composed in `androidApp`)

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
08 introduces `projectDependencies` catalogs.
