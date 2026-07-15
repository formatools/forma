# 02 — feature-api-impl (Android)

**Goal:** Feature slice with `api` + `impl`. Composition roots list both.

## Features introduced
- `api` (JVM contracts, no `res/`)
- `impl` (Android implementation)
- Explicit `api` + `impl` on `androidBinary` / `androidApp`
- **Rule:** `impl` ↛ `impl`

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
03 adds feature `androidRes` + `viewBinding`.
