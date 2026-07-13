# Forma architecture audit

Living map of the monorepo build graph, plugin modules, sample app, CI, and
what should become **forma-core**. Source of truth for F-002; update when the
graph changes.

Last audited: **2026-07-11** (worker host green after F-001).

---

## 1. Repository layout (composite builds)

The root `settings.gradle.kts` only names the project (`forma`); it is **not** a
unified multi-project build. Real work lives in **independent Gradle builds**
that compose via `includeBuild`:

| Path | Role | Gradle wrapper | Publishes / consumed as |
|------|------|----------------|-------------------------|
| `plugins/` | Forma Gradle plugins (main product) | 8.3 | Composite + Plugin Portal (`tools.forma.*`) |
| `application/` | Sample Android product (gold standard) | 8.4 | Consumer only |
| `includer/` | Settings plugin: auto-`include` subprojects | (own wrapper) | `tools.forma.includer` |
| `depgen/` | Transitive-deps generation plugin | (own wrapper) | `tools.forma.depgen` (standalone) |
| `build-settings/` | Shared settings conventions (repos) | — | `includeBuild` / convention plugins |
| `build-dependencies/` | Typed external dep catalogs for sample | — | `includeBuild` as `tools.forma.demo:dependencies` |
| `docs/`, `scripts/` | Worker/env docs (not Gradle) | — | — |
| `.github/workflows/` | CI | — | — |

```
                    ┌──────────────────────┐
                    │   build-settings     │  convention-dependencies,
                    │   (pluginManagement) │  convention-plugins (repos)
                    └──────────┬───────────┘
           includeBuild        │
    ┌──────────────────────────┼──────────────────────────┐
    │                          │                          │
    v                          v                          v
┌─────────┐   includeBuild  ┌────────────┐   includeBuild  ┌──────────────┐
│includer │◄────────────────│ application│────────────────►│   plugins    │
└─────────┘                 │  (sample)  │                 │  (product)   │
                            └─────┬──────┘                 └──────┬───────┘
                                  │ includeBuild                  │ includeBuild
                                  v                               │ ../build-settings
                         ┌─────────────────┐                      │
                         │build-dependencies│                     │
                         └─────────────────┘                      │
                                                                  │
┌─────────┐                                                       │
│ depgen  │  (separate; not on application critical path)         │
└─────────┘                                                       │
```

**Application `pluginManagement` / plugins** (from `application/settings.gradle.kts`):

- `includeBuild("../build-settings")`, `../plugins`, `../includer`
- Settings plugins: `convention-dependencies`, `tools.forma.includer`, `tools.forma.android`, Gradle Enterprise
- `includeBuild("../build-dependencies")` for demo catalog
- Heavy `buildscript` resolution forces (AGP 8.1.2, bundletool, asm, guava, …)

**Plugins build** (`plugins/settings.gradle.kts`):

- `pluginManagement { includeBuild("../build-settings") }`
- `tools.forma.includer` **0.2.0** from Portal (not local includer composite)
- Includer discovers `:android`, `:config`, `:core`, `:deps`, `:owners`, `:target`, `:validation` (F-021 added `:core`)

---

## 2. `plugins/` module graph

Group/version (root `plugins/build.gradle.kts`): **`tools.forma` / `0.1.3`**.

```
                    ┌────────────┐
                    │    core    │  TargetType, NameMatcher, RestrictionGraph (F-021)
                    └─────▲──────┘
                          │
                    ┌─────┴──────┐
                    │   target   │  TargetTemplate, FormaTarget (parallel for compat)
                    └─────▲──────┘
                          │
              ┌───────────┼────────────┐
              │           │            │
        ┌─────┴─────┐ ┌───┴────┐       │
        │validation │ │ owners │       │
        └─────▲─────┘ └───▲────┘       │
              │           │            │
        ┌─────┴─────┐     │      ┌─────┴─────┐
        │   deps    │◄────┼──────┤  config   │
        └─────▲─────┘     │      └─────▲─────┘
              │           │            │
              └─────┬─────┴────────────┘
                    │
              ┌─────┴─────┐
              │  android  │  user-facing DSL + AGP features (+ AndroidTargetTypes + matrix)
              └───────────┘
```

| Module | Plugin id | Depends on | Responsibility |
|--------|-----------|------------|----------------|
| `:core` | (library, not plugin) | (none / gradleApi compileOnly if needed) | `TargetType` / `TargetRef`, `NameMatcher`/`SuffixNameMatcher`, `RestrictionGraph`/`MutableRestrictionGraph`, `EdgeKind`, `RestrictionRule`. Pure engine. |
| `:target` | `tools.forma.target` | `gradleApi` | `TargetTemplate(suffix)`, `FormaTarget(project)` (compat facade; F-021 adds parallel core types) |
| `:validation` | `tools.forma.validation` | `:target`, `gradleApi` | Name validators, content validators, `ProjectValidationError` (F-022 will migrate to core) |
| `:owners` | `tools.forma.owners` | `gradleApi` | `Owner` / `Person` / `Team` / `NoOwner` |
| `:config` | `tools.forma.config` | `gradleApi` | `AndroidProjectSettings`, `FormaSettingsStore`, plugin/dep registration maps |
| `:deps` | `tools.forma.deps` | `:validation`, `:target`, `:config`, kotlin-dsl | `FormaDependency` model, `applyDependencies`, version-catalog generators |
| `:android` | `tools.forma.android` | `:core` + all of the above + **AGP** + Kotlin GP | Target DSL (`api`, `impl`, `androidLibrary`, …), feature appliers; owns `AndroidTargetTypes` + `AndroidRestrictionKit` (F-021) |

`:android` compiles against **AGP 8.1.2** (aligned with sample runtime force in
`application/settings.gradle.kts` as of F-003). Keep `plugins/android` AGP
compile dep and consumer `agpVersion` in lockstep.

`publishPlugins` on `:android` depends on publishing all sibling plugins.

**Publish configuration (F-016 / GH #132):** shared metadata lives in
`plugins/build.gradle.kts` via `formaPluginConfiguration { … }`
(`plugins/buildSrc`). Each plugin module calls
`formaPublishedPlugin(name = …)` instead of copying `rootProject.ext` +
`gradlePlugin { }` blocks. Operator guide: [`docs/PLUGIN-PUBLISH.md`](PLUGIN-PUBLISH.md)
(Portal org/credentials for GH #133 are human-owned; not stored in-repo).

### 2.1 Target templates (suffixes)

Defined in `plugins/android/.../AndroidTargets.kt`:

| Object | Suffix | User DSL entrypoint |
|--------|--------|---------------------|
| `BinaryTargetTemplate` | `binary` | `androidBinary` |
| `ApplicationTargetTemplate` | `app` | `androidApp` |
| `LibraryTargetTemplate` | `library` | `androidLibrary` **and** `library` (JVM) |
| `UiLibraryTargetTemplate` | `ui-library` | `uiLibrary` |
| `NativeTarget` | `native` | `androidNative` |
| `UtilTargetTemplate` | `util` | `util` |
| `TestUtilTargetTemplate` | `test-util` | `testUtil` |
| `AndroidTestUtilTargetTemplate` | `android-test-util` | `androidTestUtil` |
| `AndroidUtilTargetTemplate` | `android-util` | `androidUtil` |
| `ViewBindingTargetTemplate` | `viewbinding` | `viewBinding` |
| `ResourcesTargetTemplate` | `res` | `androidRes` |
| `ApiTargetTemplate` | `api` | `api` |
| `ImplTargetTemplate` | `impl` | `impl` |
| `WidgetTargetTemplate` | `widget` | `widget` |
| `ComposeWidgetTargetTemplate` | `compose-widget` | `composeWidget` |

Name rule (`validation`): project name equals `suffix` or ends with `-$suffix`.

### 2.2 Allowed project dependencies (from live validators)

**Canonical tables + full matrix:** [`docs/DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md)
(F-010). That file is the user-facing code truth; keep this section in sync
when validators change.

| Consumer DSL | Allowed *project* dependency suffixes | Content rules |
|--------------|----------------------------------------|---------------|
| `api` | `api`, `library` | no `res/` under `src/main` |
| `impl` | `api`, `android-util`, `test-util`, `util`, `library`, `ui-library`, `res`, `viewbinding`, `widget`, `compose-widget` | — |
| `library` (JVM) | `util`, `test-util` | — |
| `androidLibrary` | `library`, `util`, `android-util`, `test-util`, `res`, `api` | — |
| `uiLibrary` | `widget`, `compose-widget`, `util`, `android-util`, `res` | — |
| `util` | `util`, `library` | no `res/` |
| `androidUtil` | `android-util`, `test-util`, `res` | no `res/` |
| `testUtil` | `test-util`, `util` | no `res/` |
| `androidTestUtil` | `android-test-util`, `test-util` | — |
| `androidRes` | `res`, `widget`, `compose-widget` | **only** `res/` under `src/main` |
| `widget` | `ui-library`, `widget`, `compose-widget`, `util`, `android-util`, `res` | — |
| `composeWidget` | `ui-library`, `compose-widget`, `widget`, `util`, `android-util`, `res` | always Compose |
| `viewBinding` | `api`, `widget`, `compose-widget`, `res`, `library`, `android-util` | only `layout*` under `src/main/res` |
| `androidApp` | `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library` | no `res/` |
| `androidBinary` | `app`, `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library` | no `res/` |
| `androidNative` | (no `applyDependencies` in current code) | no `res/` |

Notes for later tickets:

- `impl` cannot depend on other `impl` (Dagger-friendly) — enforced; F-011.
- `androidLibrary` / `androidApp` / `androidBinary` use restricted project-dep lists — F-011 done.
- JVM `library` and Android `androidLibrary` share `LibraryTargetTemplate` suffix `library` — naming collision risk for forma-core registry design (F-020).

### 2.3 Feature stack (Android module)

Under `tools.forma.android.feature`:

- `FeatureDefinition` + `applyFeatures`
- `androidLibraryFeatureDefinition` / `androidBinaryFeatureDefinition` / `androidNativeDefinition`
- Kotlin JVM vs Kotlin Android feature definitions
- `kaptConfigurationFeature` (auto when kapt-ish deps present)

Configuration singleton: `Forma` object delegates to `FormaSettingsStore`
(`AndroidProjectSettings`: min/target/compile SDK, AGP/Kotlin versions, repos,
compose default + compose compiler version, owners mandatory flag, Java
compatibility). See [`COMPOSE.md`](COMPOSE.md) for F-013 usage.

### 2.4 Deps subsystem

- Model: `FormaDependency` sealed hierarchy (`NamedDependency`, `TargetDependency`,
  `FileDependency`, `PlatformDependency`, `MixedDependency`, `EmptyDependency`)
- Application: `applyDependencies` — validates each project dep, applies plugin
  side-effects from catalog registrations, sets transitive flags
- Catalog: `projectDependencies` / `library` / `bundle` / `plugin` in settings
  (`tools.forma.deps.catalog`) + pure name generators + GAV validation
  (user guide: [`DEPS-CATALOG.md`](DEPS-CATALOG.md))
- Sample also uses hand-written catalogs in `build-dependencies/dependencies`
  (`Androidx`, `Google`, `Test`, …)

### 2.5 Includer

Settings plugin walks the tree for `build.gradle(.kts)` (optional arbitrary
script names), skips nested settings roots, maps path → project name with
`:` + path using `-` instead of `/` (avoids intermediate empty projects).
Sample enables `arbitraryBuildScriptNames = true`.

---

## 3. Sample `application/` structure

~35 Forma targets, multi-feature Marvel demo. **User-facing gold-standard
guide:** [`SAMPLE-APP.md`](SAMPLE-APP.md) (F-014).

```
application/
├── binary/                 androidBinary  (APK entry)
├── root-app/               androidApp
├── root-res/               androidRes
├── toggle-widget/          widget
├── core/
│   ├── di/library          androidLibrary
│   ├── mvvm/library        androidLibrary
│   ├── navigation/library  androidLibrary
│   ├── network/library     library (JVM)
│   └── theme/{android-util,res}
├── common/
│   ├── util                util
│   ├── extensions/{util,android-util}
│   ├── greeting/compose-widget  composeWidget
│   ├── placeholder/res
│   ├── progressbar/{res,viewbinding}
│   └── recyclerview/widget
└── feature/
    ├── home/{api,impl,res,viewbinding}
    └── characters/
        ├── core/{api,impl}
        ├── list/{api,impl,res,viewbinding}
        ├── detail/{api,impl,res,viewbinding}
        └── favorite/{api,impl,res,viewbinding}
```

Wiring pattern:

- Feature **api** = JVM Kotlin contracts (+ network/library etc.)
- Feature **impl** = Android library + Dagger + navigation + viewbinding/res/widget
- **binary** depends on root-app + all feature api/impl + shared core (explicit
  graph; not only transitive)
- `packageName` aligned to path under `tools.forma.sample…` (F-014)

Root configuration (`application/build.gradle.kts`):

```kotlin
androidProjectConfiguration(
  minSdk = 21, targetSdk = 33, compileSdk = 34,
  agpVersion = "8.1.2",
  extraPlugins = [ demo deps, KSP, nav safe-args, crashlytics ]
)
```

---

## 4. CI (`.github/workflows/main.yml`)

Display name **CI** (`on: push`, `pull_request`, `workflow_dispatch`).

| Job | Directory | Java | Notes |
|-----|-----------|------|-------|
| `build_application` | `application/` | Temurin **17** | `android-actions/setup-android@v3` (SDK 33 + build-tools 33/34); `./gradlew build --stacktrace --console=plain` |
| `build_plugins` | `plugins/` | Temurin **17** | `./gradlew build --stacktrace --console=plain` |
| `build_includer` | `includer/` | Temurin **17** | same |
| `build_depgen` | `depgen/` | Temurin **17** | same |

Concurrency group `ci-${{ github.workflow }}-${{ github.ref }}` cancels in-progress runs on the same ref.

F-004 fixes applied (2026-07-11):

1. **All jobs** pin Temurin 17 via `actions/setup-java@v4`.
2. **Application** installs Android SDK packages matching sample `compileSdk` 33.
3. README badge → `formatools/forma` + `actions/workflows/main.yml/badge.svg`.
4. Gradle setup via `gradle/actions/setup-gradle@v4` (successor of `gradle-build-action`).
5. Dropped unconditional `--scan` (no build-scan account coupling in CI).

Still optional / later: CI job for `publishPlugins --validate-only` (needs no
keys) or full Portal publish via secrets; deeper Gradle remote cache.

---

## 5. Toolchain snapshot (host + declared)

| Component | Declared / observed |
|-----------|---------------------|
| JDK | 17 (all CI jobs Temurin; host OpenJDK 17 via Homebrew) |
| Gradle | plugins 8.3, application 8.4 |
| AGP | **8.1.2** sample runtime + plugins compile (aligned F-003) |
| Kotlin | embeddedKotlin from Gradle distribution |
| Android SDK | sample compileSdk **34** / target 33; host platforms 34+33 + build-tools 33/34 |
| Forma version | 0.1.3 |

Host bootstrap details: `docs/ENV.md`, `scripts/env-mac.sh` (F-001).

---

## 6. forma-core extraction map (candidates)

Aligned with `docs/VISION.md`: core must not assume Android/AGP/Dagger.

| Concern | Current home | forma-core? | Notes |
|---------|--------------|-------------|-------|
| Target type identity (`TargetTemplate` / suffix) | `:target` + `:core` (F-021) | **Yes** | Parallel `TargetType` in core; templates kept for compat in F-021. Registry in F-022/F-023. |
| Name + dep-type `Validator` | `:validation` | **Yes** | Keep framework; Android content rules as plugins |
| Content validators (`onlyAllowResources`, …) | `:android` + `:validation` helpers | **Split** | Generic dir checks → core; Android paths → android plugin |
| Dependency model + apply | `:deps` | **Mostly yes** | Strip AGP-ish config features; catalog generators may stay tooling |
| Settings store | `:config` | **Split** | Generic `SettingsStore` / plugin registry → core; `AndroidProjectSettings` → android |
| Owners | `:owners` | **Optional / yes** | Platform-agnostic metadata |
| Feature definitions (AGP library/binary/native) | `:android` | **No** | Stay platform |
| DSL entrypoints (`api`/`impl`/…) | `:android` | **No** (register *onto* core) | First consumer of core (F-023) |
| Includer / depgen | separate builds | **No** | Adjacent tooling |
| build-dependencies catalogs | sample support | **No** | Demo-only; pattern informs F-012 |

Suggested extraction order (tickets F-020…F-024):

1. Document public API — **done (F-020):** [`forma-core-api.md`](forma-core-api.md)
   (types, restriction graph, validator SPI, target registry, coords preview,
   `library` suffix decision).
2. Create `plugins/core` + TargetType + RestrictionGraph + wire Android matrix from DEPENDENCY-MATRIX (F-021). Parallel types + facades; full move later.
3. Re-home `applyDependencies` project-validation path on core validators (F-022).
4. Leave `:android` as the first platform package implementing templates + AGP features (F-023).
5. Coordinates: e.g. `tools.forma:core` vs `tools.forma.android` (F-024).

---

## 7. Known inconsistencies / follow-ups

| Item | Ticket |
|------|--------|
| ~~README dependency matrix ≠ live validators~~ → [`docs/DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) | F-010 done |
| ~~`EmptyValidator` on app/binary/androidLibrary~~ (composition-root + library allowlists) | F-011 done |
| ~~AGP 7.4.2 compile vs 8.1.2 runtime~~ (aligned 8.1.2) | F-003 done |
| ~~CI missing SDK + Java on some jobs~~ (GHA green on PR #153) | F-004 done |
| ~~Compose flag in settings, limited target support~~ → per-target flags + `composeWidget` | F-013 done |
| ~~Missing Android getting-started tutorial~~ → [`docs/GETTING-STARTED.md`](GETTING-STARTED.md) | F-015 done |
| Shared `library` suffix for JVM vs Android library | F-020 design: unique `TargetType.id`, shared suffix OK ([`forma-core-api.md`](forma-core-api.md) §7); optional rename later |
| Plugin publish / Portal path | F-016 (`docs/PLUGIN-PUBLISH.md`; Portal org GH #133 is human) |
| ~~Configuration-time cost (validators/deps/repos)~~ → [`docs/CONFIGURATION-PERFORMANCE.md`](CONFIGURATION-PERFORMANCE.md) | F-017 done |

---

## 8. Quick reference — where to change what

| Goal | Start here |
|------|------------|
| New user / first project | [`docs/GETTING-STARTED.md`](GETTING-STARTED.md) |
| Configuration-time performance | [`docs/CONFIGURATION-PERFORMANCE.md`](CONFIGURATION-PERFORMANCE.md) |
| New target type | `AndroidTargets.kt` + new DSL file under `plugins/android/src/main/java/` + validator list |
| Tighten dep rules | `validator(...)` in that DSL file; update this doc §2.2 |
| Global SDK/AGP defaults | `androidProjectConfiguration` + sample `application/build.gradle.kts` |
| External deps UX | `plugins/deps` catalog + `build-dependencies` |
| Auto module discovery | `includer/` |
| CI | `.github/workflows/main.yml` |
| Sample structure | `application/feature/**`, `binary/` · [SAMPLE-APP.md](SAMPLE-APP.md) |

---

## 9. Audit method

- Walked all `settings.gradle.kts` / `build.gradle.kts` for composite edges.
- Read every `plugins/android` DSL entrypoint for `validator(...)` and content checks.
- Listed `application/**/build.gradle.kts` targets and feature folders.
- Inspected `.github/workflows/main.yml` and wrapper properties.
- Host builds (F-001): `plugins/`, `includer/`, `depgen/`, `application/` all
  **BUILD SUCCESSFUL** on OpenJDK 17 + Android SDK 33 — not re-run in this audit slice.
