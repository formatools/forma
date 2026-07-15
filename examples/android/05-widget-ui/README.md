# 05 — widget-ui (Android)

**Goal:** Custom View widgets and shared UI libraries.

## Features introduced
- `widget` (Custom View / UI component)
- `uiLibrary` (shared UI building blocks for widgets)

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Next
06 adds Compose (`compose` flag + `composeWidget`).
