# Forma external dependency catalogs

**House style (one global way):** declare third-party libraries and plugins once with
settings-level [`projectDependencies`](#1-house-style--projectdependencies-version-catalog)
and consume them via `libs.*` + `deps(...)`.

This is the **only** supported happy path for new apps, progressive examples, and
agent skills. It matches root principle 2 — *one global way* per concern
([VISION.md](VISION.md)).

**Advanced (not a second happy path):** hand-written typed Kotlin catalog objects
(the sample’s `build-dependencies/` pattern). Keep them only when you need large
shared non-transitive graphs that version-catalog accessors do not express well.
Do not teach typed catalogs and `projectDependencies` as equal peer defaults.

| Path | Status | Use when |
|------|--------|----------|
| `projectDependencies` → `libs.*` | **House style** | New projects, Portal apps, plugins, bundles, normal third-party pins |
| Typed objects (`androidx.*`, `google.*`, …) | **Advanced / sample-scale** | Nested explicit transitive trees at monorepo scale; not the default teaching path |

---

## 1. House style — `projectDependencies` version catalog

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

Teaching ladder: [`examples/android/08-deps-catalog`](../examples/android/08-deps-catalog).
Agent skill: [`forma-deps-catalog`](../examples/agent-skills/forma-deps-catalog.md).

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
impl(
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

**Annotation processing happy path is KSP only** (`String.ksp` / `ksp()` / `Ksp`).
Legacy `.kapt` / `kotlin-kapt` was hard-removed in F-093 — there is no dual path.

```kotlin
plugin(
    id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
    version = "…",
    configuration = CustomConfiguration("ksp"),
    "androidx.room:room-compiler:2.5.1" // registered on the `ksp` configuration
)

// Or at the call site with the typed DSL:
dependencies = deps("com.google.dagger:dagger-compiler:…".ksp)
```

At dependency application time, consuming a `.ksp` coordinate applies
`com.google.devtools.ksp` once via `processorConfigurationFeatures()` and adds the
processor with `isTransitive = true`.

---

## 2. Advanced — typed catalogs (`build-dependencies/` pattern)

The gold-standard sample still ships **hand-written** Kotlin objects
(`androidx`, `google`, `test`, …) under `build-dependencies/dependencies`. Those use
`String.dep` / `deps(...)` and nest transitive graphs explicitly (e.g. `androidx.appcompat`
pulls fragment, core, …).

This is **not** the default product path. Prefer expanding `projectDependencies` first.
Use typed catalogs only when:

- You maintain a large shared AndroidX/Google surface with intentional non-transitive trees
- Version-catalog bundles become unwieldy for that surface
- You already own an `includeBuild` deps module (as the sample does via
  `tools.forma.demo:dependencies`)

| Concern | House style (`projectDependencies`) | Advanced typed objects |
|---------|-------------------------------------|-------------------------|
| New app / tutorial | **Yes** | No |
| Plugin ids on classpath | **Yes** (`plugin(...)`) | Via separate wiring |
| Short `libs.*` accessors | **Yes** | N/A (`androidx.foo` style) |
| Nested non-transitive graphs | Limited (bundles + `deps` defaults) | **Yes** — main reason to stay advanced |
| includeBuild required | No | Yes (sample pattern) |

The sample **mixes** both on purpose at monorepo scale: timber/coil/room/plugins go through
the house-style catalog; the broad androidx/google surface stays on typed objects. New
projects should start with **catalog only** and adopt typed objects later only if needed.

Do **not** invent a third path (scattering raw GAVs across every `build.gradle.kts`) for
production modules. Tiny progressive examples may still use inline `deps("g:a:v")` before
step 08 introduces catalogs.

---

## 3. Project / target deps (internal modules)

**House style (one global way):** internal module edges use **`target(...)`** only.
Do **not** put raw Gradle `project(":…")` in target `dependencies =` blocks.

Canonical issue: GH **#56** (F-101). Slash / Bazel-style path notation is **out of
scope** — see discussion **#57**.

### Happy path

| API | Shape | Role |
|-----|--------|------|
| `target(":a:b:c")` | `Project.target(String)` | Forma **logical** path (colons). Resolved to includer Gradle project path (`:a-b-c`) via pure `ProjectPathForms.gradleProjectPathFromFormaTarget` |
| `target(projects.someModule)` | `Project.target(ProjectDependency)` | Typesafe project accessors (Gradle 9: path only) |
| `project.target` / `this.target` | `Project.target` property | Self-ref when a module needs its own `FormaTarget` |
| `deps(target(...), …)` | `deps(vararg FormaTarget)` | Internal edges → `TargetDependency` |
| `deps(...) + deps(target(...))` | `+` on `FormaDependency` | Compose named/catalog deps with project targets |
| `deps(projects.foo, …)` | `Project.deps(vararg ProjectDependency)` | Bridge typesafe accessors → `TargetDependency` without spelling `target` each time |

```kotlin
impl(
    packageName = "com.example.feature.home.impl",
    dependencies = deps(
        libs.androidxCoreKtx,
    ) + deps(
        target(":feature:home:api"),
        target(":core:util"),
    ),
)

// Typesafe accessors (when enabled in settings):
dependencies = deps(target(projects.featureHomeApi))
// equivalent bridge:
dependencies = deps(projects.featureHomeApi)
```

### Path forms (colon happy path only)

| Form | Example | Where |
|------|---------|--------|
| **Forma logical path** | `:feature:home:impl` | `target("…")` call sites — **this is the happy path** |
| **Gradle / Includer project path** | `:feature-home-impl` | Task paths, `./gradlew :feature-home-impl:assemble`, typesafe accessor project path |
| Filesystem dir | `feature/home/impl` | On disk; Includer discovers `build.gradle.kts` here |

Normalization (parity with historic `target(String)`):

| Input (`target("…")`) | Gradle project path |
|----------------------|---------------------|
| `:feature:home:impl` | `:feature-home-impl` |
| `:root-app` | `:root-app` |
| `:feature-home-impl` (already dashed) | `:feature-home-impl` (accepted; prefer colons) |
| missing leading `:` / empty / `:` only | **rejected** (`IllegalArgumentException`) |

Leading **`:` is required**. Do not invent dual slash syntax (`\feature\home\impl` or
`//feature/home:impl`) in product code — deferred to #57.

### Rejected as happy path

| Pattern | Why |
|---------|-----|
| `dependencies = deps(project(":feature-home-api"))` or bare `project(":…")` in module deps | Gradle API leak; wrong terminology; bypasses Forma path form |
| Dual documented path syntax (colon **and** slash) at call sites | Two ways for one concern — violates root principle 2 until #57 decides |
| Scattering `project(...)` “because Gradle docs say so” | Implementation detail **inside** `Project.target(String)` only |

`project(...)` remains a Gradle primitive used **inside** Forma helpers (and for
non-deps concerns such as includeBuild). It is not the module-deps teaching path.

### API surface additions (`dependencies.kt` + core)

| Symbol | Role |
|--------|------|
| `Project.target` (property) | Self `FormaTarget` |
| `Project.target(String)` | Forma colon path → `FormaTarget` |
| `Project.target(ProjectDependency)` | Typesafe accessor → `FormaTarget` |
| `deps(vararg FormaTarget)` | Target edges |
| `Project.deps(vararg ProjectDependency)` | Typesafe → `TargetDependency` |
| `ProjectPathForms.gradleProjectPathFromFormaTarget` | Pure path normalize (unit-tested in `:core`) |

See also call-site summary: [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) § Project deps.
Live **who may depend on whom**: [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md).

---

## 4. API surface (plugins `:deps`)

| Symbol | Package | Role |
|--------|---------|------|
| `projectDependencies` | `tools.forma.deps.catalog` | Settings DSL entry (**house style**) |
| `library` / `LibraryDep` | same | Explicit library registration |
| `bundle` / `BundleDep` | same | Named library groups |
| `plugin` / `PluginDep` | same | Plugin + optional config/libs |
| `defaultNameGenerator` / `pluginNameGenerator` | same | Pure name helpers (overridable) |
| `parseGroupArtifactVersion` | same | GAV validation |
| `deps` / `String.dep` / `Provider.dep` | root (`dependencies.kt`) | Bridge into `FormaDependency` (named deps default **non-transitive**) |
| `transitiveDeps` / `String.transitiveDep` | root (`dependencies.kt`) | Same bridge with **transitive** named deps (`String.transitiveDep` = single-string parity with `String.dep`) |
| `depsIf` / `depsUnless` / `NamedDependency.whenFlag` | root (`dependencies.kt`) | **F-099** — gate named deps on project-global `FormaFeatureFlags`; resolved at `applyDependencies` time (not construction). Unknown flag = false |
| `resolveFeatureFlags` | `tools.forma.deps.core` | Pure filter of flag-gated `NameSpec`s (unit-tested) |
| `applyDependencies` | `tools.forma.deps.core` | Wire deps + plugin side effects (+ F-099 flag resolution) |
| `target` / `Project.target` / `deps(FormaTarget…)` / `Project.deps(ProjectDependency…)` | root (`dependencies.kt`) | **F-101** — internal project deps; see [§3](#3-project--target-deps-internal-modules) |
| `ProjectPathForms.gradleProjectPathFromFormaTarget` | `tools.forma.core.fleet` | Pure Forma path → Gradle path |

---

## 5. Practical tips

1. **Pin versions in one place** — catalog constants in `settings.gradle.kts` (house style)
   or a single `versions` object inside an advanced typed module; never scatter GAVs across
   feature targets.
2. **Prefer `library(…, name = …)`** for public-facing short names you will type often.
3. **Use `bundle`** when several artifacts always travel together (Room, Coil).
4. **Keep transitive control intentional** — bare catalog `deps(libs.foo)` / `"g:a:v".dep`
   follow Forma's default **non-transitive** named-deps path unless a plugin registration
   forces transitive. Use `"g:a:v".transitiveDep` (single) or `transitiveDeps(...)` (multi)
   when Maven transitively is required.
5. **Invalid GAV fails fast** — `group:artifact:version` only; two or four segments throw
   a clear `IllegalArgumentException` at configuration time.
6. **Plugins vs apply** — catalog `plugin(...)` + `extraPlugins` put jars on the
   **buildscript classpath only**. Type-owned apply is separate
   ([TARGET-PLUGINS.md](TARGET-PLUGINS.md)). Local convention plugins:
   includeBuild + catalog plugin GAV — not `project(":…")`
   ([BUILDSCRIPT-PROJECT-CLASSPATH.md](BUILDSCRIPT-PROJECT-CLASSPATH.md)).
7. **Conditional deps (F-099)** — declare flags once on
   `androidProjectConfiguration(featureFlags = …)`; use `depsIf` / `depsUnless` at
   call sites. Do **not** shop plugins with flags or add per-module Booleans for
   every product toggle. See [`TARGET-FEATURE-OPTIONS.md`](TARGET-FEATURE-OPTIONS.md).
8. **Internal modules use `target(...)`** — colon Forma paths
   (`target(":feature:home:api")`) or typesafe `target(projects…)` /
   `deps(projects…)`. Never teach raw `project(":…")` in `dependencies =`.
   Full table: [§3](#3-project--target-deps-internal-modules).

---

## Related

- Live project-dep matrix: [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md)
- Architecture map: [`ARCHITECTURE.md`](ARCHITECTURE.md) §2.4
- Sample settings (house style + advanced mix): `application/settings.gradle.kts`
- Sample typed catalogs (advanced): `build-dependencies/dependencies/src/main/kotlin/`
- Progressive example (house style): `examples/android/08-deps-catalog`
- **Target external plugins** — type owns plugin, call sites auto-apply:
  [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md)
- Call-site surface: [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md)
- Product feature flags + conditional deps: [`TARGET-FEATURE-OPTIONS.md`](TARGET-FEATURE-OPTIONS.md)
- Project / target deps (F-101): [§3](#3-project--target-deps-internal-modules)
