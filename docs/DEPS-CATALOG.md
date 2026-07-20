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

## 3. API surface (plugins `:deps`)

| Symbol | Package | Role |
|--------|---------|------|
| `projectDependencies` | `tools.forma.deps.catalog` | Settings DSL entry (**house style**) |
| `library` / `LibraryDep` | same | Explicit library registration |
| `bundle` / `BundleDep` | same | Named library groups |
| `plugin` / `PluginDep` | same | Plugin + optional config/libs |
| `defaultNameGenerator` / `pluginNameGenerator` | same | Pure name helpers (overridable) |
| `parseGroupArtifactVersion` | same | GAV validation |
| `deps` / `String.dep` / `Provider.dep` | root (`dependencies.kt`) | Bridge into `FormaDependency` |
| `applyDependencies` | `tools.forma.deps.core` | Wire deps + plugin side effects |

---

## 4. Practical tips

1. **Pin versions in one place** — catalog constants in `settings.gradle.kts` (house style)
   or a single `versions` object inside an advanced typed module; never scatter GAVs across
   feature targets.
2. **Prefer `library(…, name = …)`** for public-facing short names you will type often.
3. **Use `bundle`** when several artifacts always travel together (Room, Coil).
4. **Keep transitive control intentional** — bare catalog `deps(libs.foo)` follows Forma's
   default non-transitive named-deps path unless a plugin registration forces transitive.
   Use `transitiveDeps(...)` when Maven transitively is required.
5. **Invalid GAV fails fast** — `group:artifact:version` only; two or four segments throw
   a clear `IllegalArgumentException` at configuration time.
6. **Plugins vs apply** — catalog `plugin(...)` + `extraPlugins` put jars on the
   **buildscript classpath only**. Type-owned apply is separate
   ([TARGET-PLUGINS.md](TARGET-PLUGINS.md)).

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
