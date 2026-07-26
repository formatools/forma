# 01 — shared-library (KMP)

**Goal:** Minimal shared Kotlin Multiplatform library consumed by a JVM `binary`. Introduces `kmpLibrary`, project-global `kmpProjectConfiguration`, and the Android/JVM → KMP dependency edge (JVM consumer).

## Features introduced
- `kmpLibrary` (type-owned `org.jetbrains.kotlin.multiplatform`; suffix `*-kmp-library`)
- `kmpProjectConfiguration` with pure KMP+JVM platforms (`jvm = true`, `android = false`)
- `tools.forma.kmp` + `tools.forma.jvm` settings plugins
- JVM `binary` composition root depending on `target(":shared-kmp-library")`
- commonMain layout (`src/commonMain/kotlin/…`, not `src/main`)
- Includer with `arbitraryBuildScriptNames`
- Composite includeBuild to repo `plugins` + `includer` + `build-settings`

## Project tree
```
01-shared-library/
├── settings.gradle.kts
├── build.gradle.kts          # kmpProjectConfiguration once
├── gradle.properties
├── gradlew, gradle/wrapper/
├── shared-kmp-library/
│   ├── build.gradle.kts      # kmpLibrary(packageName = …)
│   └── src/commonMain/kotlin/tools/forma/examples/kmp/shared/Greeting.kt
└── binary/
    ├── build.gradle.kts      # binary(… deps target(":shared-kmp-library"))
    └── src/main/kotlin/tools/forma/examples/kmp/binary/Main.kt
```

## Build & run (from this dir)
```bash
source ../../../scripts/env-mac.sh
./gradlew build
./gradlew :binary:run
```

Expected:
```
Hello, World — from Forma KMP shared library
KMP 01 (shared-library) build + run successful.

BUILD SUCCESSFUL
```

## What this teaches
- **Type owns MPP:** modules never call `plugins { kotlin("multiplatform") }` or `kotlin { targets { … } }`.
- **One global way:** platforms are set once in root `kmpProjectConfiguration`.
- **Composition at JVM root:** only `binary` wires the shared library; no `impl` → `impl`.
- **Suffix rule:** the shared project **must** be named `*-kmp-library` so validators match `kmp.library`.

## Non-goals (this step)
- Android consumer / `android = true` platforms (optional later; needs `androidProjectConfiguration` + AGP)
- `kmpApi` / `kmpUtil` / expect-actual

## Next
- Tutorial: [`docs/KMP-GETTING-STARTED.md`](../../../docs/KMP-GETTING-STARTED.md)
- Agent skill: [`forma-kmp-targets`](../../agent-skills/forma-kmp-targets.md)
- Design/matrix: [`docs/KMP-TARGETS.md`](../../../docs/KMP-TARGETS.md)
- Broader KMP ladder may add api/util and Android consumer steps later.
