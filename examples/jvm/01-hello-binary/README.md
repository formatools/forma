# 01 — hello-binary (JVM)

**Goal:** Minimal runnable JVM binary using Forma. Introduces `binary` target, includer, composite `includeBuild`.

## Features introduced
- `binary` (composition root + `application` plugin → `:binary:run`)
- `tools.forma.jvm` plugin via settings
- Includer with `arbitraryBuildScriptNames`
- Composite includeBuild to repo `plugins` + `includer` + `build-settings`

## Project tree
```
01-hello-binary/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew, gradle/wrapper/
└── binary/
    ├── build.gradle.kts
    └── src/main/kotlin/tools/forma/examples/jvm/hello/binary/Main.kt
```

## Build & run (from this dir)
```bash
source ../../../scripts/env-mac.sh
./gradlew build
./gradlew :binary:run
```

Expected:
```
Hello from Forma JVM progressive example 01 (binary only)

BUILD SUCCESSFUL
```

## What changed vs nothing
First use of `binary(...)` DSL. No feature slices yet. No deps. Package name matches directory structure exactly.

## Next
See `02-api-impl` for first feature split (`api` + `impl`).
