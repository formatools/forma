# Project configuration (F-082)

**One global configuration path only.**

Forma has a single supported entry point for Android project-wide settings and classpath:

```kotlin
// root build.gradle.kts
buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 23,
        targetSdk = 35,
        compileSdk = 35,
        agpVersion = "9.3.0",
        // compose = false,
        // composeCompilerVersion = "2.3.21",
        // F-091 / GH #88: AGP BuildFeatures — all default off; opt in intentionally
        // buildFeatures = FormaBuildFeatures(
        //     buildConfig = true,
        //     // viewBinding = true, // default for impl(viewBinding=…) only
        // ),
        // F-098 / GH #103: core library desugaring (Java 8+ APIs on lower minSdk)
        // coreLibraryDesugaring = true,
        // coreLibraryDesugaringDependency = "com.android.tools:desugar_jdk_libs:2.1.5", // optional
        // F-090: skip project-dep suffix checks only for listed forked-in modules
        // dependencyValidationExclusions = setOf(
        //     ":third-party:exoplayer:library-core",
        //     "forked-media-engine",
        // ),
        extraPlugins = listOf(
            // jars / Provider<PluginDependency> for the *buildscript classpath only*
            libs.plugins.navigationSafeArgs,
            // ...
        )
    )
}
```

This is the **only** supported way to configure an Android Forma project.

## What `androidProjectConfiguration` does

- **Classpath**: Adds the requested AGP version, the Kotlin Compose compiler Gradle plugin (when relevant), and any `extraPlugins` entries to the root **buildscript classpath**.
- **Settings store**: Creates an `AndroidProjectSettings` and writes it via `Forma.store(...)`, which delegates to the singleton `FormaSettingsStore`.
- **Target registry**: Calls `registerAndroidDefaults()` so that all Android target types + restriction matrix + content rules are available to DSL call sites.
- **Convenience**: Registers a conventional root `clean` task.

Downstream targets and features read global values (SDK versions, `compose` default, `kotlinVersion`, `agpVersion`, etc.) from `Forma.settings` (or the delegated store surfaces). The store is initialized exactly once from the root `buildscript` block.

### `dependencyValidationExclusions` (F-090 / GH #97)

| | |
|--|--|
| **Type** | `Set<String>` (default `emptySet()`) |
| **Set on** | Root `androidProjectConfiguration(...)` only |
| **Stored as** | `AndroidProjectSettings.dependencyValidationExclusions` |
| **Read by** | `applyDependencies` via `FormaSettingsStore.isExcludedFromDependencyValidation` |

Lists Gradle **project paths** and/or **project names** that skip **project-dependency
type/suffix validation** when they appear as a dependency of a Forma target.

**Intended use:** monorepo-vendored forks (ExoPlayer-class) that do not use Forma
name suffixes.

**Not for:** bypassing the matrix between first-party Forma modules (e.g. `impl`→`impl`).
There is no per-module / per-call-site `skipValidation` flag — one global allow-list only.

**Matching:** exact path (e.g. `:third-party:exoplayer:library-core`) or exact name.
Does **not** disable self-type validation on Forma DSL entry points.

See [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) § Dependency validation exclusions.

### `buildFeatures` (F-091 / GH #88)

| | |
|--|--|
| **Type** | `FormaBuildFeatures` (default `FormaBuildFeatures()` — **all flags false**) |
| **Set on** | Root `androidProjectConfiguration(...)` only |
| **Stored as** | `AndroidProjectSettings.buildFeatures` |
| **Read by** | Android library + binary feature definitions (central apply helper) |

Maps AGP public `BuildFeatures` flags into Forma with **defaults off** where safe:

| Flag | In `FormaBuildFeatures` | Notes |
|------|-------------------------|--------|
| `aidl` | yes | Project-global only |
| `buildConfig` | yes | Project-global; enables `BuildConfig` generation fleet-wide when true |
| `dataBinding` | yes | Project-global; AGP 9 exposes this on library/app feature subtypes; does not auto-enable `viewBinding` |
| `prefab` | yes | Project-global |
| `resValues` | yes | Project-global |
| `shaders` | yes | Project-global |
| `viewBinding` | yes | Default for `impl(viewBinding=…)`; **`viewBinding` target type always on** |
| `compose` | **no** — use top-level `compose` | One happy path for Compose project default |
| `renderScript` | not mapped | Deprecated/removed in modern AGP |

Every Android library/app target **explicitly sets** each supported flag from the
resolved values so AGP platform defaults cannot silently turn features on.

**Compose relationship:** keep using top-level `compose = true/false` and
`composeCompilerVersion`. Do not put a second Compose default inside
`FormaBuildFeatures`. See [`COMPOSE.md`](COMPOSE.md) and
[`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) § BuildFeatures.

### `coreLibraryDesugaring` (F-098 / GH #103)

| | |
|--|--|
| **Type** | `Boolean` (default **false**) + optional `String` dependency coordinate |
| **Set on** | Root `androidProjectConfiguration(...)` only |
| **Stored as** | `AndroidProjectSettings.coreLibraryDesugaring` / `.coreLibraryDesugaringDependency` |
| **Read by** | Android library + binary + native feature definitions (shared apply helper) |

Enables [AGP core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring)
fleet-wide so modules with a lower `minSdk` can use Java 8+ **library** APIs
(`java.time`, streams extras, etc.) rewritten by the desugar toolchain.

When **true**, every Android AGP target (library, binary, native) gets:

1. `compileOptions.isCoreLibraryDesugaringEnabled = true` (via `CompileOptions.applyFrom`)
2. A dependency on the `coreLibraryDesugaring` configuration using
   `coreLibraryDesugaringDependency` (default
   `com.android.tools:desugar_jdk_libs:2.1.5` —
   [`DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY`](../plugins/config/src/main/java/tools/forma/config/AndroidProjectSettings.kt))

When **false** (default): flag stays off and **no** desugar dependency is auto-added.
The dependency string may still be stored for overrides but is not applied.

**Not a call-site flag.** Do not invent `desugar=` / `coreLibraryDesugaring=` on
`impl` / `androidBinary` / etc. Fleet concern — same tier as `javaVersionCompatibility`
and `buildFeatures`. See [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md).

```kotlin
androidProjectConfiguration(
    // ...
    coreLibraryDesugaring = true,
    // optional pin override:
    // coreLibraryDesugaringDependency = "com.android.tools:desugar_jdk_libs:2.1.5",
)
```

## What it does **not** do

- `extraPlugins` (and catalog `plugin(...)` entries) are **classpath only**. They put Gradle plugin jars on the buildscript classpath. They do **not** apply any plugin to your modules.
- Applying plugins to targets is handled exclusively by the **type-owned plugin registry**:
  - Register once with `targetPlugin(...)` + `deriveTargetType(...)` (Path B, preferred) or `registerTargetPlugin(...)` (Path A).
  - The registry auto-applies on matching DSL calls.
  - See [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md).
- **APK `versionCode` / `versionName`** — not project-global (F-092 / GH #82). Set them as
  **required** attrs on each `androidBinary { … }` call site. `androidApp` is a library
  composition shell and does not take version attrs. See
  [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) § APK version identity.
- **APK signing configs** — not project-global (F-097 / GH #51). Named
  `FormaSigningConfig` entries and `buildTypeSigning` live on each `androidBinary`
  only. See [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) § APK signing.

There is no per-module `withPlugin`, no free-form `plugins =` lists, and no second configuration path.

## Removed (F-082)

The deprecated `Project.androidProjectConfiguration(...)` receiver overload has been **hard-removed**.

- It was the "call configuration from any module" dual happy path.
- All live call sites already used the `ScriptHandlerScope` form inside root `buildscript { }`.
- Do not attempt to configure from arbitrary `Project` scopes. Configuration belongs at the root buildscript level.

## The single settings / store story

| Piece | Location | Role |
|-------|----------|------|
| `androidProjectConfiguration(...)` (ScriptHandlerScope) | `plugins/android/.../androidProjectConfiguration.kt` | The one public API; called from root `buildscript` |
| `AndroidProjectSettings` | `plugins/config/.../AndroidProjectSettings.kt` | Immutable data class holding SDKs, versions, compose default, `buildFeatures`, core library desugaring, repos, etc. |
| `FormaBuildFeatures` | `plugins/config/.../FormaBuildFeatures.kt` | Nested AGP BuildFeatures defaults (all off); pure data + resolve helper |
| `DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY` | `plugins/config/.../AndroidProjectSettings.kt` | Default `desugar_jdk_libs` GAV pin (F-098) |
| `FormaSettingsStore` | `plugins/config/.../AndroidProjectSettings.kt` (as `object`) | The backing singleton store (`SettingsStore<T>` + `PluginInfoStore`) |
| `Forma` (singleton accessor) | `plugins/android/.../androidProjectConfiguration.kt` | `object Forma : SettingsStore<...> by FormaSettingsStore, ...` — primary reader surface (`Forma.settings`) |
| `registerAndroidDefaults()` | `plugins/android/.../AndroidTargetRegistry.kt` | Populates `AndroidTargetRegistry` (target types + matrix) |

Readers (feature wiring, dependency helpers, target DSLs) obtain values through `Forma.settings` after the root configuration has run. The store is the single source of truth for the project-global Android configuration.

## Cross references

- [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md) — classpath (`extraPlugins`) vs. type-owned apply
- [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) — F-081 / F-082 surface rules (no chains, minimal attrs, global config)
- [`GETTING-STARTED.md`](GETTING-STARTED.md) — tutorial usage + checklist
- [`COMPOSE.md`](COMPOSE.md) — `compose` + `composeCompilerVersion` details
- [`ARCHITECTURE.md`](ARCHITECTURE.md) — plugin module layout and store
- [`VISION.md`](VISION.md) — root principles (one global way)
- Sample: `application/build.gradle.kts`
- Progressive example 10: `examples/android/10-target-plugins`

## Checklist — correct global configuration

- [ ] Exactly one `androidProjectConfiguration(...)` call in the **root** `buildscript { }`
- [ ] `project = rootProject` (or equivalent root)
- [ ] `extraPlugins` only for classpath jars (see TARGET-PLUGINS for how to actually apply)
- [ ] No calls to any `Project.androidProjectConfiguration` form (removed)
- [ ] Child targets rely on `Forma.settings` / per-target overrides only
- [ ] Type-owned plugins used for external plugin application (no ad-hoc apply)
- [ ] Forked third-party modules (if any) listed once in `dependencyValidationExclusions` — never per-call-site skips

---

F-082 completes the "one global way" cleanup for project configuration (paired with F-081 call-site surface work).
