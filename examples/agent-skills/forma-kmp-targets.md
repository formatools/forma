---
name: forma-kmp-targets
description: Kotlin Multiplatform Forma DSL targets, kmpProjectConfiguration, and KMP ladder.
---

# KMP targets (agent skill)

Plugin: `tools.forma.kmp`. Imports: `tools.forma.kmp.kmpLibrary`, `.kmpApi`, `.kmpUtil`, `.kmpTestUtil`.

Root config (default package, inside `buildscript { }`): `kmpProjectConfiguration(project, platforms, jvmTarget)`.

| DSL | Suffix | Role | Step |
|-----|--------|------|------|
| `kmpLibrary` | `kmp-library` | shared MPP library | 01 |
| `kmpApi` | `kmp-api` | shared contracts | (later ladder) |
| `kmpUtil` | `kmp-util` | shared helpers | (later) |
| `kmpTestUtil` | `kmp-test-util` | shared test helpers | (later) |

**No v1:** `kmpImpl`, `kmpBinary`, iOS/JS/Wasm, per-module `kotlin { targets }`.

## Platforms (one global way)

```kotlin
buildscript {
  kmpProjectConfiguration(
    project = rootProject,
    platforms = tools.forma.kmp.settings.KmpPlatforms(jvm = true, android = false),
  )
}
// Defaults if omitted: jvm=true, android=true (requires androidProjectConfiguration when android on)
```

Type owns `org.jetbrains.kotlin.multiplatform` (+ `com.android.kotlin.multiplatform.library` when android).

## Matrix rules of thumb

**KMP → KMP:** api→api; library→api/library/util; util→util; test-util→api/library/util/test-util.

**Consumers → KMP:** android/jvm `api`→`kmp.api`; impl/app/binary→api+library+util.  
**Never:** KMP → android/jvm `impl` / UI / `res`.  
**Never:** restore `androidLibrary` as shared bucket.

## Skeleton

```kotlin
// shared-kmp-library/build.gradle.kts
import tools.forma.kmp.kmpLibrary
kmpLibrary(packageName = "com.example.shared")

// binary/build.gradle.kts (JVM composition root)
import tools.forma.jvm.binary
binary(
  packageName = "com.example.binary",
  mainClass = "com.example.binary.MainKt",
  dependencies = deps(target(":shared-kmp-library")),
)
```

Sources: `src/commonMain/kotlin/…` on KMP modules (not `src/main`).

## Hard rejects

1. Call-site platform lists / raw `kotlin { androidTarget(); ios() }`
2. `.withPlugin` / free-form plugin ids on KMP modules
3. `impl` → `impl` via “shared” fat modules that pull UI
4. Suffix `*-library` without `kmp-` prefix (that is JVM `library`)

Examples: `examples/kmp/01-shared-library`. Design: `docs/KMP-TARGETS.md`. Tutorial: `docs/KMP-GETTING-STARTED.md`. Matrix: `docs/DEPENDENCY-MATRIX.md`.
