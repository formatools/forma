# JVM targets (F-030)

Pure **JVM** platform for Forma, built on **forma-core** (`tools.forma:core`)
without AGP. Parallel to the Android product (`tools.forma.android`), not a
fork of the restriction engine.

Plugin id: **`tools.forma.jvm`** (module `plugins/:jvm`).

## Types

| DSL (package `tools.forma.jvm`) | Type id | Name suffix | Role |
|---------------------------------|---------|-------------|------|
| `api` | `jvm.api` | `api` | Public contracts (Dagger-friendly) |
| `impl` | `jvm.impl` | `impl` | Feature implementation |
| `library` | `jvm.library` | `library` | Shared pure-JVM library |
| `util` | `jvm.util` | `util` | Utilities / extensions |
| `testUtil` | `jvm.test-util` | `test-util` | Shared test helpers |

Name matching is the same as Android / core: project name equals the suffix or
ends with `-$suffix` (see `SuffixNameMatcher`).

Composition roots (`binary` / app) are **out of scope for F-030** (sample app
is F-031). Until then, feature graphs should avoid needing multi-`impl`
composition at a root.

## Dependency matrix (project edges only)

External / catalog GAV deps are not filtered by type (same as Android).

| Consumer ↓ \ Dep → | api | impl | library | util | test-util |
|--------------------|-----|------|---------|------|-----------|
| **api** | Y | — | Y | — | — |
| **impl** | Y | — | Y | Y | Y |
| **library** | — | — | — | Y | Y |
| **util** | — | — | Y | Y | — |
| **test-util** | — | — | Y | Y | Y |

### Rules of thumb

- **`impl` ↛ `impl`** — implementations stay independent; compose later at a
  binary/app root (F-031).
- **`api` only sees contracts** — `api` + `library` (no util/impl leakage into
  the public surface).
- **`library` stays leaf-ish** — only `util` / `test-util` (parity with the
  historical JVM `library()` row inside the Android matrix).
- **`util` cannot depend on `api` / `impl`**.
- **Content rules** — pure JVM has no Android `res/`; no content rules are
  attached for F-030.

Source of truth in code: `plugins/jvm/.../JvmTargetRegistry.kt`
(`registerJvmDefaults`).

## Relation to Android

- Android already exposes pure-Kotlin entrypoints (`api`, `library`, `util`,
  `testUtil`) **inside** `tools.forma.android`, registered on
  `AndroidTargetRegistry` with some shared **id strings** (`jvm.library`,
  `jvm.util`).
- F-030 introduces an **authoritative JVM platform registry**
  (`JvmTargetRegistry`) and plugin so pure-JVM products do not need AGP.
- Registries are **separate singletons**; shared id strings are intentional.
- Full Android matrix remains [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md).

## Consumer sketch

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    id("tools.forma.jvm") version "0.1.3"
}

// some-module/build.gradle.kts
import tools.forma.jvm.api
import tools.forma.jvm.library
// …

api(
    packageName = "com.example.feature.api",
    dependencies = deps(
        project(":shared-library")
    )
)
```

`registerJvmDefaults()` runs on first DSL entry so the matrix is available
without a separate configuration call (a dedicated `jvmProjectConfiguration`
can land later if settings need grow).

Kotlin JVM is applied by the DSL (`org.jetbrains.kotlin.jvm`); default
compatibility is Java **11** (`JvmDefaults`).

## Tests

- `plugins/jvm` unit tests: `JvmTargetRegistryTest` (matrix allow/deny,
  self-suffix, validator identity cache).
- Engine coverage remains in `plugins/core` tests.

## Next

- **F-031** — multi-module JVM sample application (gold standard like
  `application/`).
- **F-032** — JVM getting-started tutorial.

## See also

- [`forma-core-api.md`](forma-core-api.md) — core types / registry SPI
- [`ARCHITECTURE.md`](ARCHITECTURE.md) — module graph
- [`PLUGIN-PUBLISH.md`](PLUGIN-PUBLISH.md) — coordinates
- [`VISION.md`](VISION.md) — product sequencing
