# 02 — api-impl (JVM)

**Goal:** Split a feature into `api` (contract) + `impl`. Binary is the composition root that lists both.

## Features introduced
- `api` target (public contracts, no resources)
- `impl` target (depends on its `api`)
- `binary` lists **api + impl** explicitly
- **Key rule:** impls never depend on other impls (enforced by validator)

## Project tree
```
02-api-impl/
├── binary/
│   ├── build.gradle.kts
│   └── src/.../Main.kt
├── feature/greeter/
│   ├── api/
│   │   ├── build.gradle.kts
│   │   └── src/.../Greeter.kt
│   └── impl/
│       ├── build.gradle.kts
│       └── src/.../RealGreeter.kt
└── ...
```

## Build & run
```bash
source ../../../scripts/env-mac.sh
./gradlew build
./gradlew :binary:run
```

## Changes vs 01
- Introduced feature slice under `feature/`
- `target(":feature:greeter:api")` colon paths (includer + Forma)
- Composition root explicitly wires api+impl
- Package names match source roots

## Next
03 adds `library` + `util`.
