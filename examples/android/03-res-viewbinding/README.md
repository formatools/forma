# 03 — res-viewbinding (Android)

**Goal:** Feature resources + layout-only view binding modules.

## Features introduced
- Feature `androidRes` (only `res/` under src/main)
- `viewBinding` (only `layout*` resource types)
- `impl` depends on own `api` + `res` + `viewbinding`

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
04 adds shared `library` / `util` / `androidUtil` / `androidLibrary`.
