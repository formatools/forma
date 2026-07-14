# JVM targets (F-030 / F-031)

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
| `binary` | `jvm.binary` | `binary` | Composition root + runnable app |

Name matching is the same as Android / core: project name equals the suffix or
ends with `-$suffix` (see `SuffixNameMatcher`).

`binary(...)` applies `org.jetbrains.kotlin.jvm` + Gradle `application`, sets
`mainClass`, and may depend on multiple `impl` modules (composition root).

## Dependency matrix (project edges only)

External / catalog GAV deps are not filtered by type (same as Android).

| Consumer ↓ \ Dep → | api | impl | library | util | test-util | binary |
|--------------------|-----|------|---------|------|-----------|--------|
| **api** | Y | — | Y | — | — | — |
| **impl** | Y | — | Y | Y | Y | — |
| **library** | — | — | — | Y | Y | — |
| **util** | — | — | Y | Y | — | — |
| **test-util** | — | — | Y | Y | Y | — |
| **binary** | Y | Y | Y | Y | Y | — |

### Rules of thumb

- **`impl` ↛ `impl`** — implementations stay independent; compose at `binary`.
- **`api` only sees contracts** — `api` + `library` (no util/impl leakage into
  the public surface).
- **`library` stays leaf-ish** — only `util` / `test-util` (parity with the
  historical JVM `library()` row inside the Android matrix).
- **`util` cannot depend on `api` / `impl`**.
- **`binary` is the composition root** — may pull api + multiple impls + shared.
- **Content rules** — pure JVM has no Android `res/`; no content rules are
  attached.

Source of truth in code: `plugins/jvm/.../JvmTargetRegistry.kt`
(`registerJvmDefaults`).

## Relation to Android

- Android already exposes pure-Kotlin entrypoints (`api`, `library`, `util`,
  `testUtil`) **inside** `tools.forma.android`, registered on
  `AndroidTargetRegistry` with some shared **id strings** (`jvm.library`,
  `jvm.util`).
- F-030 introduced an **authoritative JVM platform registry**
  (`JvmTargetRegistry`) and plugin so pure-JVM products do not need AGP.
- F-031 added **`binary`** + the multi-module sample `jvm-application/`.
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
    // or includeBuild("../plugins") in a composite
}

// module build.gradle.kts
import tools.forma.jvm.api
import tools.forma.jvm.library

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
  self-suffix, validator identity cache, binary composition-root row).
- Engine coverage remains in `plugins/core` tests.
- Integration: [`jvm-application/`](../jvm-application/) (`./gradlew build` /
  `:binary:run`).

## Next

- [JVM getting-started tutorial](JVM-GETTING-STARTED.md) (F-032) — start here for a new pure-JVM project.

Future: Bazel adapter (F-040 design accepted) will map these same six types and the restriction matrix to `kt_jvm_*` rules + visibility derived from the graph. See [`BAZEL-ADAPTER.md`](BAZEL-ADAPTER.md). The JVM kit is the initial target set for the adapter.

## See also

- [`JVM-SAMPLE.md`](JVM-SAMPLE.md) — multi-module sample layout + run
- [`forma-core-api.md`](forma-core-api.md) — core types / registry SPI
- [`ARCHITECTURE.md`](ARCHITECTURE.md) — module graph
- [`PLUGIN-PUBLISH.md`](PLUGIN-PUBLISH.md) — coordinates
- [`VISION.md`](VISION.md) — product sequencing
