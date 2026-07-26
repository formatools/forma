# 14 — google-firebase (Android)

**Goal:** Google first-party Firebase (Crashlytics + Analytics) with a **real usage path**,
not classpath-only comments.

## Features introduced
- Type-owned Firebase Gradle plugins on the APK root (`com.google.gms.google-services`,
  `com.google.firebase.crashlytics`) via Path A + thin `firebaseBinary` DSL
- Firebase BOM + Crashlytics/Analytics SDKs
- Dummy `binary/google-services.json` (CI-safe placeholder project)
- App code: `FirebaseApp.initializeApp`, `FirebaseCrashlytics`, `FirebaseAnalytics`

## Layout
- `forma-defs` — `firebaseBinary` + plugin registration
- `binary` — `firebaseBinary` + `google-services.json`
- `root-app` — Application + Activity that call Firebase APIs
- `root-res` — strings

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

## Notes
- This is **not** a live Firebase backend — the JSON is a dummy for assemble.
- Production apps: real `google-services.json` from the Firebase console; never commit secrets.
- Gold sample Architecture Components / Room / Navigation coverage:
  [`docs/GOOGLE-LIBRARIES.md`](../../../docs/GOOGLE-LIBRARIES.md).
- Sample app still has Crashlytics on the **buildscript classpath** only (TODO Path B
  `firebaseBinary` in `application/binary`) — this example is the teaching path.

## Next
See the Google 1P matrix for Architecture Components, Room, Material, Play Core, etc.
