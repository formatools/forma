# Sample Android app — gold-standard multi-feature structure (F-014)

The `application/` composite build is Forma’s **product reference**: a multi-feature
Android app wired with the full target set (`api` / `impl` / `res` / `viewbinding` /
`widget` / `composeWidget` / composition roots).

Use this layout when teaching Forma or copying a starter structure.
**Getting started tutorial:** [GETTING-STARTED.md](GETTING-STARTED.md) (F-015 / GH #53).

## Layout

```
application/
├── binary/                      androidBinary   (APK / composition root)
├── root-app/                    androidApp      (Application + root DI)
├── root-res/                    androidRes      (launcher, app nav host layout)
├── toggle-widget/               widget          (theme toggle action view)
├── core/
│   ├── di/android-util          androidUtil     (Dagger scopes / base component)
│   ├── mvvm/ui-library          uiLibrary       (ViewModel helpers, adapters)
│   ├── navigation/res           navigationRes   (Path B derived type — nav graphs + type-owned safe-args)
│   ├── network/library          library (JVM)   (Retrofit / Config / NetworkState)
│   └── theme/{android-util,res}
├── common/
│   ├── util                     util
│   ├── extensions/{util,android-util}
│   ├── greeting/compose-widget  composeWidget   (Compose sample — F-013)
│   ├── placeholder/res
│   ├── progressbar/{res,viewbinding}
│   └── recyclerview/widget
└── feature/
    ├── home/{api,impl,res,viewbinding}
    └── characters/
        ├── core/{api,impl}                    (shared domain + Marvel API)
        ├── list/{api,impl,res,viewbinding}
        ├── detail/{api,impl,res,viewbinding}
        └── favorite/{api,impl,res,viewbinding}
```

Includer discovers every `build.gradle.kts` under `application/`
(`arbitraryBuildScriptNames = true`). Project path → name uses `-` instead of `/`
(e.g. `feature/characters/list/impl` → `:feature-characters-list-impl` for Gradle
type-safe accessors; Forma `target(":feature:characters:list:impl")` uses colon
path form).

## Feature slice convention

| Slice | Target | Responsibility |
|-------|--------|----------------|
| `api` | `api` | JVM contracts, DI feature interfaces, domain models visible to other features |
| `impl` | `impl` | Android feature implementation (UI, ViewModels, Dagger feature components) |
| `res` | `androidRes` | Feature strings / dimens / drawables only |
| `viewbinding` | `viewBinding` | Layouts + light binding-facing types (interfaces for list/favorite UI contracts) |

Rules enforced by validators ([DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md)):

- **`impl` cannot depend on another `impl`** (Dagger-friendly). Cross-feature code
  goes through `api` (or shared `core` libraries).
- **Composition roots** (`androidApp` / `androidBinary`) pull feature `api` + `impl`
  explicitly — do not rely on transitive feature wiring alone.
- **`api` stays free of `res/`** under `src/main`.

## Package naming

Namespace / `packageName` follows the module path under `tools.forma.sample…`:

| Module path | `packageName` |
|-------------|-----------------|
| `feature/home/api` | `tools.forma.sample.feature.home.api` |
| `feature/characters/list/impl` | `tools.forma.sample.feature.characters.list.impl` |
| `feature/characters/list/res` | `tools.forma.sample.feature.characters.list.res` |
| `feature/characters/favorite/viewbinding` | `tools.forma.sample.feature.characters.favorite.viewbinding` |
| `root-res` | `tools.forma.sample.root.res` |
| `toggle-widget` | `tools.forma.sample.widget.toggle` |
| `binary` | `tools.forma.sample.app` |

Kotlin sources live under `src/main/java/<package-as-dirs>/…` matching the
declared package (no leftover third-party roots).

Shared UI contracts that belong with layouts may live in the `viewbinding`
module package (e.g. list view-state interfaces under
`…list.viewbinding.domain.model`) so `impl` depends on them without putting
layouts into `impl`.

## Wiring sketch

```
binary (androidBinary)
  └── root-app (androidApp) ── root-res
  └── feature/*/api + feature/*/impl   (explicit)
  └── common/greeting/compose-widget   (Compose demo)
  └── core/* shared libraries

root-app hosts SampleApp DI:
  BaseComponent + ThemeComponent + CharactersCore + CharacterFavorite

home/impl hosts bottom navigation:
  navigation_characters_list_graph + navigation_character_favorite_graph
  (from core/navigation/res)
```

## Build

```bash
source scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties
cd application && ./gradlew build
```

Toolchain: AGP **9.3.0**, compileSdk **37**, targetSdk **37**, Gradle **9.6.1**,
build JDK **21** (app language level unchanged). See [ENV.md](ENV.md),
[COMPOSE.md](COMPOSE.md), [ARCHITECTURE.md](ARCHITECTURE.md).

## What “gold standard” means here

1. **One feature = one folder** with consistent `api` / `impl` / `res` / `viewbinding` slices.
2. **Aligned packageNames** with directory + Kotlin packages.
3. **Explicit composition-root deps** on every feature entrypoint.
4. **Shared platform code** under `core/` and `common/`, not copied per feature.
5. **Modern targets** represented: View widgets + Compose widget side by side.
6. **Documented** enough to copy without reading every `build.gradle.kts`.

Not in scope for F-014: full navigation redesign (GH #46), publish path (F-016).
Configuration-time performance: [CONFIGURATION-PERFORMANCE.md](CONFIGURATION-PERFORMANCE.md) (F-017).

**Navigation abstraction (F-102 / GH #46):** design is in
[NAVIGATION-ABSTRACTION.md](NAVIGATION-ABSTRACTION.md) — presentation-layer ports
so feature `impl`/ViewModels do not take Jetpack Navigation / Safe Args codegen;
graphs stay on `navigationRes`. Sample refactor = **F-111**; progressive example
= **F-112**. This gold-standard tree still shows today’s bleed until F-111 lands.

## Type-owned plugins (navigation)

`core/navigation/res` uses Path B **`navigationRes(...)`** (defined in
`build-dependencies/.../NavigationRes.kt`), not plain `androidRes` + `.withPlugin`.
Safe-args is owned by the derived type and auto-applies. See
[TARGET-PLUGINS.md](TARGET-PLUGINS.md), [NAVIGATION-ABSTRACTION.md](NAVIGATION-ABSTRACTION.md)
(Layer B), and progressive example `examples/android/10-target-plugins/`.

## Google first-party libraries (AndroidX / Firebase / …)

Coverage of Architecture Components, Room, Navigation, Compose, Material, Dagger,
Play Core, Gson, and Firebase is tracked in **[GOOGLE-LIBRARIES.md](GOOGLE-LIBRARIES.md)**.
The sample exercises most Jetpack stacks in real feature code; Firebase Crashlytics
teaching path is progressive example **`examples/android/14-google-firebase`**
(sample binary still has Crashlytics on the buildscript classpath only).
