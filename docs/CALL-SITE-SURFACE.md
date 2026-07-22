# Call-site surface (F-081)

Forma target DSLs are **Bazel-flat**: one call, **`Unit` return**, attributes only.
Plugin identity and platform features live on the **type/rule** (or project-global
settings), not on a builder chain after the call.

**Status:** F-081 — chain API **removed** (`TargetBuilder`, `PluginWrapper`, sample
`Plugins` object). Type-owned external plugins: [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md).

## Contract

| Layer | Owns |
|-------|------|
| Target **type** / rule | Plugins, content rules, matrix row, always-on features for that role |
| Project configuration | Global defaults (`Forma.settings.compose`, `Forma.settings.buildFeatures`, SDK, etc.) |
| Call site (`build.gradle.kts`) | Instance attrs only: `packageName`, deps, version, optional rule flags |

**Rejected at call sites**

- Builder returns / `.withPlugin` / `.withPlugins`
- Free-form `plugins = plugins(plugin("id"))` lists
- Re-selecting plugin bindings per module

## Android target DSLs (`tools.forma.android`)

All return **`Unit`**.

| DSL | Suffix / role | Notable attrs | Notes |
|-----|---------------|---------------|--------|
| `api` | `api` | `packageName`, `dependencies` | JVM contracts; no Android UI |
| `impl` | `impl` | deps, **`viewBinding`**, **`compose`**, test runners, `buildConfiguration` | Feature impl; no → other `impl` |
| `androidApp` | `app` | deps, **`compose`**, `buildConfiguration`, … | Composition **library** (not APK) — **no** `versionCode`/`Name` |
| `androidBinary` | `binary` | **`versionCode`/`versionName` (required)**, deps, **`compose`**, … | APK composition root; version identity is **per-binary only** (F-092) |
| `library` | `library` | deps (pure JVM inside Android plugin) | Distinct from removed `androidLibrary` |
| `util` | `util` | deps | JVM helpers |
| `androidUtil` | `android-util` | deps, **`compose`** | Android helpers, no res content |
| `testUtil` / `androidTestUtil` | `test-util` / `android-test-util` | deps | Test helpers |
| `androidRes` | `res` | `packageName`, deps, placeholders | Resources only; no external plugins by default |
| `viewBinding` | `viewbinding` | deps | **Type owns** `viewBinding=true`; layouts only |
| `widget` | `widget` | deps, test runners | View UI components |
| `composeWidget` | `compose-widget` | deps, test runners | **Type owns** Compose on |
| `uiLibrary` | `ui-library` | deps, **`compose`**, `buildConfiguration` | Shared UI bases |
| `androidNative` | `native` | `buildSystem`, `abi`, … | NDK |
| Path B derived (e.g. `navigationRes`) | often shared suffix | same as base + type-owned plugins | Consumer-defined; see TARGET-PLUGINS |

Internal helper `resourcesTarget(type, …)` backs `androidRes` and derived res DSLs —
not a public “generic library” escape hatch.

## JVM target DSLs (`tools.forma.jvm`)

All return **`Unit`**.

| DSL | Role | Notable attrs |
|-----|------|---------------|
| `api` | contracts | `packageName`, `dependencies` |
| `impl` | feature impl | deps, test deps (no → impl) |
| `library` | shared JVM | deps |
| `util` | helpers | deps |
| `testUtil` | test helpers | deps |
| `binary` | runnable root | `mainClass`, deps |

## Optional flags inventory

Flags are **declared rule attributes** with defaults — not ad-hoc structure and not
a second way to apply plugins.

| Flag | Where | Default | Policy |
|------|--------|---------|--------|
| `compose` | `impl`, `androidApp`, `androidBinary`, `uiLibrary`, `androidUtil` | `Forma.settings.compose` (project-global) | Prefer global on; override only when a module must differ. Dedicated Compose UI → `composeWidget` type. |
| `viewBinding` | `impl` only (Boolean) | `Forma.settings.buildFeatures.viewBinding` (default **false**) | Layouts-only modules use the **`viewBinding` target type** (always on). Do not invent dual structure via flags. |
| *(type-owned)* | `composeWidget` | Compose always on | Prefer dedicated type over `widget(compose=true)`. |
| *(type-owned)* | `viewBinding` target | View binding always on | Prefer type over `impl(viewBinding=true)` when the module is layouts-only. |
| External plugins | never a call-site flag | n/a | `targetPlugin` + `deriveTargetType` / `registerTargetPlugin` |

Other common attrs (`testInstrumentationRunner`, `buildConfiguration`,
`consumerMinificationFiles`, `manifestPlaceholders`, `owner`, `visibility`) are
instance knobs with empty/public defaults — keep them; do not grow plugin-id
parameters beside them.

## APK version identity (F-092 / GH #82)

| Concern | Owner | Notes |
|---------|--------|--------|
| `versionCode` / `versionName` | **`androidBinary` call site only** (required attrs) | Wired to AGP `ApplicationExtension.defaultConfig` |
| Project-global version | **None** | Not on `androidProjectConfiguration` / `AndroidProjectSettings` |
| `androidApp` version attrs | **None** | `androidApp` is `com.android.library` (DI/feature shell); APK identity lives on binary |

**Why:** monorepos often ship multiple APKs with independent versions. Global-only
version forced every binary to share one code/name. There is no default and no
override ladder — each binary declares both attrs explicitly.

**Gradle limit:** `versionCode`/`versionName` apply to application modules. Putting
them on library-shaped `androidApp` would not produce a second APK version surface.

## AGP BuildFeatures (F-091 / GH #88)

AGP `BuildFeatures` flags are **project-global by default** (all **off**), with
type/call-site ownership only where already established. There is **no** per-flag
Boolean shopping list on every DSL.

| Concern | Owner | How to enable |
|---------|--------|----------------|
| `aidl`, `buildConfig`, `dataBinding`, `prefab`, `resValues`, `shaders` | Project only | `androidProjectConfiguration(buildFeatures = FormaBuildFeatures(...))` |
| `viewBinding` (project default for `impl` + binary) | Project + `impl` attr | `buildFeatures.viewBinding`; `impl` may override; binary has **no** call-site attr (fleet default only) |
| `viewBinding` always on | **`viewBinding` target type** | Use `viewBinding { … }` (type forces on) |
| `compose` project default | Top-level `compose =` on `androidProjectConfiguration` | **Not** nested under `buildFeatures` (one happy path) |
| `compose` per target | Call-site `compose` on listed DSLs | Defaults to `Forma.settings.compose` |
| `compose` always on | **`composeWidget` target type** | Use `composeWidget { … }` |

**Apply path:** library + binary feature definitions call a central helper that
starts from `Forma.settings.buildFeatures`, applies type/call-site overrides for
viewBinding/compose, and **explicitly sets every supported flag** so AGP cannot
silently enable features. Compose still runs `enableCompose` (compiler plugin),
not only the boolean.

**Do not** add call-site Booleans for `aidl` / `buildConfig` / `dataBinding` / etc.
Fleet-wide BuildConfig → `buildFeatures.buildConfig = true`. Rare per-module
BuildConfig (if ever needed) should be a dedicated derived type or a single
documented attr later — not free-form plugin/feature shopping.

**dataBinding** often pairs with view binding in app code; Forma does **not**
auto-enable one when the other is on — set both explicitly if both are required.
`renderScript` is not mapped (removed/deprecated in modern AGP).

## Removed (F-081)

| Removed | Replacement |
|---------|-------------|
| `TargetBuilder` + `.withPlugin` / `.withPlugins` | Type-owned plugins ([TARGET-PLUGINS.md](TARGET-PLUGINS.md)) |
| `PluginWrapper` + deps `pluginConfiguration { }` (chain-only) | `targetPlugin(id)` (+ future type-associated config on derived DSLs) |
| Sample `Plugins` object (`navigationSafeArgs`, GMS, Crashlytics factories) | Path B DSLs (e.g. `navigationRes`); Firebase → derived `firebaseBinary` style |

## Related

- Principles: [VISION.md](VISION.md) § Root principles  
- Matrix: [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md)  
- Plugins: [TARGET-PLUGINS.md](TARGET-PLUGINS.md)  
- Global config (one path + store story): [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) (F-082)
- External deps house style: [`DEPS-CATALOG.md`](DEPS-CATALOG.md) (**F-083** — `projectDependencies` happy path; typed catalogs advanced)
- Full-tree teaching audit: [`PRINCIPLE-AUDIT.md`](PRINCIPLE-AUDIT.md) (**F-085**)
