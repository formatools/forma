# Forma external dependency catalogs

Forma has **two** complementary ways to declare third-party libraries. This doc is the
user-facing guide for **F-012** catalog UX (settings-level version catalogs) and how it
relates to the typed demo catalogs under `build-dependencies/`.

## 1. Settings version catalog — `projectDependencies`

Declare libraries, bundles, and plugins once in `settings.gradle.kts`. Forma fills a
Gradle [version catalog](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog)
so modules can depend on type-safe accessors (`libs.*`).

```kotlin
import tools.forma.deps.catalog.bundle
import tools.forma.deps.catalog.library
import tools.forma.deps.catalog.plugin
import tools.forma.deps.catalog.projectDependencies
import tools.forma.deps.core.CustomConfiguration

val ksp = CustomConfiguration("ksp")

projectDependencies(
    "libs", // catalog name → accessors under `libs`
    // Bare GAV → auto name (see generators below)
    "com.jakewharton.timber:timber:5.0.1",
    // Explicit short name when auto name is awkward
    library("io.coil-kt:coil:2.1.0", name = "coil"),
    library("io.coil-kt:coil-base:2.1.0", name = "coilBase"),
    bundle(
        name = "room",
        "androidx.sqlite:sqlite:2.2.0",
        "androidx.room:room-runtime:2.5.1",
        // …
    ),
    plugin(
        id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
        version = "1.9.0-1.0.13",
        configuration = ksp,
        "androidx.room:room-compiler:2.5.1"
    ),
    plugin("androidx.navigation:navigation-safe-args-gradle-plugin", "2.7.4"),
)
```

### Entry kinds

| Entry | Factory | Catalog result | Notes |
|-------|---------|----------------|-------|
| GAV string | `"group:artifact:version"` | `libs.<generatedName>` | Must be exactly three `:` segments |
| Named library | `library(gav, name = "…")` | `libs.<name>` | Prefer when auto name is long/unclear |
| Bundle | `bundle(name, …gavs)` | `libs.bundles.<name>` | Members also registered as libraries |
| Plugin | `plugin(id, version, …)` | `libs.plugins.<generatedName>` | Optional companion libs + `CustomConfiguration` |

### Auto name generators

`defaultNameGenerator` (libraries) and `pluginNameGenerator` (plugins) camelCase the
coordinate after dropping common tokens (`com`, `org`, `androidx`, `google`, `plugin`, …)
and the version segment (libraries only).

| Coordinate | Generated accessor |
|------------|--------------------|
| `com.jakewharton.timber:timber:5.0.1` | `jakewhartonTimber` |
| `io.coil-kt:coil-base:2.1.0` | `coilKtBase` |
| `androidx.room:room-runtime:2.5.1` | `roomRuntime` |
| `androidx.navigation:navigation-safe-args-gradle-plugin` | `navigationSafeArgs` |

If every token is filtered, generators throw with a message asking for an explicit
`library(…, name = …)` instead of producing an empty name.

Generators are **pure** (no configuration-time `println`). Use Gradle debug logging if
you need to inspect catalog contents.

### Using catalog deps in targets

```kotlin
androidLibrary(
    packageName = "…",
    dependencies = deps(
        libs.jakewhartonTimber,
        libs.bundles.room,
    ) + deps(target(":some:api")),
)
```

`deps(Provider…)` / `Provider.dep` bridge catalog entries into Forma's
`NamedDependency` model. When a library was registered with a plugin's
`CustomConfiguration` (e.g. KSP), `applyDependencies` applies that plugin and uses the
custom configuration automatically.

### Plugin + processor wiring

```kotlin
plugin(
    id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
    version = "…",
    configuration = CustomConfiguration("ksp"),
    "androidx.room:room-compiler:2.5.1" // registered on the `ksp` configuration
)
```

At dependency application time, consuming the room-compiler coordinate applies the KSP
plugin once and adds the processor with `isTransitive = true`.

## 2. Typed catalogs — `build-dependencies/` (sample pattern)

The sample app also ships **hand-written** Kotlin objects
(`androidx`, `google`, `test`, …) under `build-dependencies/dependencies`. Those use
`String.dep` / `deps(...)` and nest transitive graphs explicitly (e.g. `androidx.appcompat`
pulls fragment, core, …).

| Approach | Best for | Trade-offs |
|----------|----------|------------|
| `projectDependencies` version catalog | App-level third-party pins, plugins, short `libs.*` accessors | Names generated or explicit; less control over nested transitive graphs |
| Typed `build-dependencies` objects | Large shared graphs, team-owned versions, non-transitive-by-default trees | More boilerplate; included via `includeBuild` + demo plugin |

Both can coexist: the sample uses catalogs for timber/coil/room/plugins and typed objects
for the androidx/google surface used across features.

## 3. API surface (plugins `:deps`)

| Symbol | Package | Role |
|--------|---------|------|
| `projectDependencies` | `tools.forma.deps.catalog` | Settings DSL entry |
| `library` / `LibraryDep` | same | Explicit library registration |
| `bundle` / `BundleDep` | same | Named library groups |
| `plugin` / `PluginDep` | same | Plugin + optional config/libs |
| `defaultNameGenerator` / `pluginNameGenerator` | same | Pure name helpers (overridable) |
| `parseGroupArtifactVersion` | same | GAV validation |
| `deps` / `String.dep` / `Provider.dep` | root (`dependencies.kt`) | Bridge into `FormaDependency` |
| `applyDependencies` | `tools.forma.deps.core` | Wire deps + plugin side effects |

## 4. Practical tips

1. **Pin versions in one place** — either catalog constants in `settings.gradle.kts` or
   `versions` object in a typed catalog module; avoid scattering GAVs across targets.
2. **Prefer `library(…, name = …)`** for public-facing short names you will type often.
3. **Use `bundle`** when several artifacts always travel together (Room, Coil).
4. **Keep transitive control intentional** — bare catalog `deps(libs.foo)` follows Forma's
   default non-transitive named-deps path unless a plugin registration forces transitive.
5. **Invalid GAV fails fast** — `group:artifact:version` only; two or four segments throw
   a clear `IllegalArgumentException` at configuration time.

## Related

- Live project-dep matrix: [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md)
- Architecture map: [`ARCHITECTURE.md`](ARCHITECTURE.md) §2.4
- Sample settings: `application/settings.gradle.kts`
- Sample typed catalogs: `build-dependencies/dependencies/src/main/kotlin/`
