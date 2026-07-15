# 01 — hello-apk (Android)

**Goal:** Smallest installable APK with Forma: composition roots + app resources.

## Features introduced
- `androidProjectConfiguration` (SDK + AGP once at root)
- `androidBinary` — APK composition root
- `androidApp` — application / activity shell
- `androidRes` — resources-only target
- Includer + composite `includeBuild` of plugins

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
02 adds feature `api` + `impl`.
