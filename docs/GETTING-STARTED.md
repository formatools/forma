# Getting started — Android project with Forma (F-015 / GH #53)

Forma is a **meta build system** for Android (Gradle plugin). You declare
**targets** with typed, single-method builders (`api`, `impl`, `androidUtil`, `uiLibrary`,
…) instead of hand-wiring AGP plugins, source sets, and ad-hoc dependency
rules. Forma applies plugins, shared Android settings, and **dependency
visibility validation** for you.

**Root principles** (see [`VISION.md`](VISION.md)): (1) Bazel-like rules — configure
once on the type, minimal call-site attrs; (2) one global way project-wide; (3)
explicit structure, with tooling for large-scale change
([`FLEET-TOOLING.md`](FLEET-TOOLING.md) — `formaLayoutCheck` / `formaLayoutGenerate` + core APIs).

This tutorial gets you from zero to a working multi-target Android app. For the
full multi-feature reference layout, see [SAMPLE-APP.md](SAMPLE-APP.md).

**Looking for pure JVM?** See the parallel [JVM getting-started tutorial](JVM-GETTING-STARTED.md) (no Android SDK required).

---

## 1. Mental model (5 minutes)

| Concept | Meaning |
|---------|---------|
| **Target** | One Gradle project / module with a Forma type (`impl`, `api`, …). Prefer “target” over “module” in Forma docs. |
| **Suffix** | Folder / project name ending that encodes type (`…-impl`, `…-api`, `…-res`, `binary`, …). Validators use suffixes. |
| **Composition root** | `androidApp` and/or `androidBinary` — where feature graphs are wired (Dagger-friendly). |
| **Feature slice** | Typical feature folder: `api` + `impl` + `res` + `viewbinding`. |
| **Catalogs** | External deps declared once via house-style `projectDependencies` (`libs.*`), consumed with `deps(...)`. Typed objects = advanced only — [DEPS-CATALOG.md](DEPS-CATALOG.md). |

**What Forma does for each target**

1. Validates the project name matches the expected suffix.
2. Applies the right Android / Kotlin plugins and shared `androidProjectConfiguration`.
3. Enforces content rules (e.g. `api` cannot ship `res/`; `androidRes` is resources-only).
4. Checks **project** dependencies against the [dependency matrix](DEPENDENCY-MATRIX.md).

You still write Kotlin, layouts, and choose library coordinates — Forma owns
structure and boundaries, not business logic.

---

## 2. Prerequisites

| Tool | Version / notes |
|------|-----------------|
| JDK | **21** build/daemon (F-018); app language level unchanged |
| Android SDK | Platform **37** (`compileSdk` / modern Compose + AndroidX AARs); **36**/**35** still useful |
| Build tools | 33.0.2 / 34.0.0 as used by the sample |
| Gradle | Use the wrapper in the project (`./gradlew`); **9.6.1** all roots |
| AGP | **9.3.0** (keep consumer `agpVersion` aligned with plugin compile AGP) |

Worker / macOS install steps: [ENV.md](ENV.md) (`source scripts/env-mac.sh`).

```bash
# After SDK + JDK are installed:
source scripts/env-mac.sh   # if using this repo’s helper
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties
```

---

## 3. Path A — run the sample app first (recommended)

The `application/` directory is the **gold-standard product**. Build it before
starting a greenfield project so you see real targets and wiring.

```bash
git clone https://github.com/formatools/forma.git
cd forma
source scripts/env-mac.sh   # or export JAVA_HOME / ANDROID_HOME yourself
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > application/local.properties

cd application
./gradlew :binary:assembleDebug
# or full verification:
./gradlew build
```

Open `application/` in Android Studio (import the `application` Gradle project,
not necessarily the monorepo root). Explore:

| Path | Target | Role |
|------|--------|------|
| `binary/` | `androidBinary` | APK entry / composition root |
| `root-app/` | `androidApp` | Application class + root DI |
| `feature/home/{api,impl,res,viewbinding}` | feature slices | One vertical feature |
| `common/greeting/compose-widget/` | `composeWidget` | Compose UI sample |
| `settings.gradle.kts` | — | Plugin management, catalogs, includer |
| `build.gradle.kts` | — | `androidProjectConfiguration` |

Deep dive: [SAMPLE-APP.md](SAMPLE-APP.md).

---

## 4. Path B — greenfield project skeleton

You can consume Forma from the **Gradle Plugin Portal** or via **composite
`includeBuild`** (as this monorepo does for development).

Published plugin id: `tools.forma.android` (badge version on the [README](../README.md);
sample monorepo develops **0.1.3** line). Companion settings plugin:
`tools.forma.includer` (auto-`include` subprojects that contain `build.gradle.kts`).

### 4.1 `settings.gradle.kts`

Minimal shape (Portal publish path — adjust version to the badge):

```kotlin
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("tools.forma.includer") version "0.2.0"
    id("tools.forma.android") version "0.1.3"
}

// Discover every build.gradle.kts under this root (skip nested settings.gradle*).
includer {
    // true when folder names are not forced to end in the suffix only
    // (sample uses nested paths like feature/home/impl).
    arbitraryBuildScriptNames = true
}

rootProject.name = "my-app"

// Optional: settings-level version catalog — see docs/DEPS-CATALOG.md
// projectDependencies("libs", "com.jakewharton.timber:timber:5.0.1", …)
```

**Monorepo / composite** (how `application/` is wired today):

```kotlin
pluginManagement {
    repositories { google() }
    includeBuild("../plugins")
    includeBuild("../includer")
    // + build-settings conventions as in application/settings.gradle.kts
}
plugins {
    id("tools.forma.includer")
    id("tools.forma.android")
}
includer { arbitraryBuildScriptNames = true }
```

Do **not** list every module with `include(...)` when using Includer — drop a
`build.gradle.kts` in a directory and it becomes a project. Nested projects that
have their own `settings.gradle.kts` are skipped.

Project path mapping (Includer): `feature/home/impl` → Gradle name
`:feature-home-impl` (type-safe accessors). Forma `target(":feature:home:impl")`
uses colon path form.

### 4.2 Root `build.gradle.kts`

```kotlin
buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 21,
        targetSdk = 33,
        compileSdk = 34,          // 34 if you use modern Compose transitive AARs
        agpVersion = "9.3.0",     // keep aligned with plugin compile AGP
        // compose = false,       // project default for per-target compose flags
        // composeCompilerVersion = "2.3.21", // match your Kotlin (2.3.21 → 2.3.21)
        // coreLibraryDesugaring = true, // F-098: Java 8+ library APIs on lower minSdk (project-global)
        // featureFlags = FormaFeatureFlags("daggerReflect" to true), // F-099: product flags + depsIf/depsUnless
        // Classpath only — does NOT apply plugins to modules. See TARGET-PLUGINS.md.
        extraPlugins = listOf(
            // e.g. libs.plugins.navigationSafeArgs (jar on buildscript classpath)
        ),
    )
}
```

This stores shared Android settings (`AndroidProjectSettings` via `Forma` / `FormaSettingsStore`),
puts AGP + optional plugin jars on the **buildscript classpath only**, and registers a root
`clean` task. Child targets read values from `Forma.settings`.

See [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) for the single-path + store story
(including optional **core library desugaring** for Java 8+ APIs on lower `minSdk` — F-098,
and **feature flags** + conditional deps — F-099 / [`TARGET-FEATURE-OPTIONS.md`](TARGET-FEATURE-OPTIONS.md)).

### External Gradle plugins (safe-args, Firebase, …)

**Do not** call `.withPlugin` on targets (**removed** in F-081). Plugin identity belongs on
the **target type**:

| Path | Use when |
|------|----------|
| **B** `deriveTargetType` + thin DSL (e.g. `navigationRes`) | Only some modules need the plugin (**preferred**) |
| **A** `registerTargetPlugin(predefinedType, …)` | Every module of that kind should get the plugin |

Call sites stay attributes-only (`Unit` return). Full contract: [TARGET-PLUGINS.md](TARGET-PLUGINS.md).
DSL / flag inventory: [CALL-SITE-SURFACE.md](CALL-SITE-SURFACE.md).
Hands-on: [examples/android/10-target-plugins](../examples/android/10-target-plugins).
Sample: `application/core/navigation/res` + `build-dependencies/.../NavigationRes.kt`.

`extraPlugins` / catalog `plugin(...)` = **classpath**. Type-owned registry =
**apply** on the right targets.

Useful `gradle.properties` (sample-aligned):

```properties
android.useAndroidX=true
android.nonTransitiveRClass=true
org.gradle.parallel=true
org.gradle.caching=true
```

### 4.3 First targets — “hello APK”

Create folders + one-line builders. Suggested minimal tree:

```
my-app/
├── settings.gradle.kts
├── build.gradle.kts
├── binary/build.gradle.kts          # androidBinary
├── root-app/build.gradle.kts        # androidApp (Application + DI shell)
├── root-res/build.gradle.kts        # androidRes (launcher / app strings)
└── feature/hello/
    ├── api/build.gradle.kts
    ├── impl/build.gradle.kts
    ├── res/build.gradle.kts
    └── viewbinding/build.gradle.kts # optional until you have layouts
```

**`binary/build.gradle.kts`** — composition root + APK:

```kotlin
androidBinary(
    packageName = "com.example.myapp",
    // F-092: version identity is per-binary (not androidProjectConfiguration)
    versionCode = 1,
    versionName = "0.1.0",
    // F-097: optional APK signing — binary only; see CALL-SITE-SURFACE § APK signing
    // signingConfigs = mapOf("demoRelease" to FormaSigningConfig(...)),
    // buildTypeSigning = mapOf("release" to "demoRelease"),
    dependencies = deps(
        target(":root-app"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
    ),
)
```

Add another `androidBinary` module if you ship a second APK — give it its own
`versionCode` / `versionName`. Do not put version attrs on `androidApp` (library shell).
Signing configs are likewise **binary-only** (F-097); debug builds need no custom
signing. Full example: sample `application/binary/`.

**`root-app/build.gradle.kts`**:

```kotlin
androidApp(
    packageName = "com.example.myapp.root",
    dependencies = deps(
        libs.androidxCoreKtx,   // house style: projectDependencies → libs.*
        libs.androidxAppcompat,
        libs.material,
    ) + deps(
        target(":root-res"),
    ),
)
```

> **External deps house style (F-083):** use settings
> `projectDependencies` → `libs.*` for all new modules
> ([DEPS-CATALOG.md](DEPS-CATALOG.md)). The gold-standard sample still uses
> **advanced** typed catalogs under `build-dependencies/` (`androidx.*`,
> `google.*`) for its large shared graph — that is optional scale, not a second
> happy path. Portal / greenfield apps should stay on `projectDependencies` only.

**`feature/hello/api/build.gradle.kts`** — JVM contracts only (no `res/`):

```kotlin
api(
    packageName = "com.example.myapp.feature.hello.api",
)
```

**`feature/hello/res/build.gradle.kts`** — resources only under `src/main/res`:

```kotlin
androidRes(
    packageName = "com.example.myapp.feature.hello.res",
)
```

**`feature/hello/impl/build.gradle.kts`** — Android implementation:

```kotlin
impl(
    packageName = "com.example.myapp.feature.hello.impl",
    dependencies = deps(
        // external libs…
    ) + deps(
        target(":feature:hello:api"),
        target(":feature:hello:res"),
        // target(":feature:hello:viewbinding"),
    ),
)
```

Put Kotlin under `src/main/java/<package-as-dirs>/…` matching `packageName`.
Scaffold missing package trees with `./gradlew :module:formaLayoutGenerate` or root
`formaLayoutGenerateAll` (see [FLEET-TOOLING.md](FLEET-TOOLING.md)).

### 4.4 Build

```bash
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
```

If validation fails, the error names the **consumer suffix**, the **illegal
dependency**, and the allowed set — fix the edge, don’t weaken the graph.

---

## 5. Target cheat sheet (when to use what)

| DSL | Suffix | Use for |
|-----|--------|---------|
| `androidBinary` | `binary` | Installable APK; top composition root |
| `androidApp` | `app` | Application / root Android library (DI shell) |
| `api` | `api` | Feature public contracts (JVM); no Android resources |
| `impl` | `impl` | Feature implementation (UI, ViewModels, feature DI) |
| `androidRes` | `res` | Strings, drawables, themes only |
| `viewBinding` | `viewbinding` | Layout XML (+ light binding-facing types) |
| `widget` | `widget` | Custom Views |
| `composeWidget` | `compose-widget` | Compose UI components (always Compose) |
| ~~`androidLibrary`~~ | — | **Removed (F-063)** — use role-specific targets ([guide](ANDROID-LIBRARY-DEPRECATION.md)) |
| `library` | `library` | Shared pure JVM library |
| `uiLibrary` | `ui-library` | Shared UI building blocks for widgets |
| `util` / `androidUtil` / `testUtil` | `util` / `android-util` / `test-util` | Small helpers |

Full matrix and content rules: [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md).
Compose flags: [COMPOSE.md](COMPOSE.md).

---

## 6. Declaring dependencies

### External

```kotlin
dependencies = deps(
    libs.jakewhartonTimber,   // house style: projectDependencies catalog
    libs.bundles.room,
    libs.androidxAppcompat,
)
```

Combine groups with `+`:

```kotlin
dependencies = deps(libs.androidxCoreKtx, libs.material) +
    deps(libs.jakewhartonTimber) +
    deps(target(":feature:hello:api"))
```

> Advanced only: the sample’s typed objects (`androidx.core_ktx`, `google.dagger`)
> remain valid where that pattern is already in use — see DEPS-CATALOG §2.

### Project / internal

```kotlin
target(":feature:hello:api")           // Forma path (colons)
// Gradle project path is often :feature-hello-api (Includer dashes)
```

**Composition roots must list feature `api` + `impl` explicitly** — do not rely
on transitive feature wiring alone. That keeps Dagger graphs honest and matches
validator intent (F-011).

---

## 7. Rules that bite on day one

1. **`impl` → `impl` is forbidden.** Cross-feature collaboration goes through
   `api` (or shared `core` libraries).
2. **`androidLibrary` is gone (F-063).** Prefer `androidUtil` / `uiLibrary` /
   `androidRes` / `viewBinding` so the graph stays flat and role-typed
   ([ANDROID-LIBRARY-DEPRECATION.md](ANDROID-LIBRARY-DEPRECATION.md)).
3. **`api` cannot contain `res/`.** Put resources in `androidRes` / `viewBinding`.
4. **`androidRes` only allows `res/` content** under `src/main`.
5. **`viewBinding` only allows layout resource types.**
6. **Name your folder / project so the suffix matches the DSL** (`…/impl` →
   `impl { }`, not a generic library target).
7. **Align `packageName` with source roots** (`tools.forma.sample…` in the sample).

If you fight the matrix, usually the **type** is wrong — not the tool.

---

## 8. Adding a second feature

Copy the `feature/hello` slice to `feature/settings` (or follow
`feature/characters/*` in the sample). Then:

1. Implement `api` contracts the other features may need.
2. Implement UI / DI in `impl` depending only on `api` + shared libs + own
   `res` / `viewbinding`.
3. Register **both** `api` and `impl` on `androidBinary` / `androidApp`.

```kotlin
// binary/build.gradle.kts (excerpt)
dependencies = deps(
    target(":root-app"),
    target(":feature:hello:api"),
    target(":feature:hello:impl"),
    target(":feature:settings:api"),
    target(":feature:settings:impl"),
)
```

---

## 9. Compose (optional)

- Project default: `androidProjectConfiguration(compose = …, composeCompilerVersion = …)`.
- Per target: `impl(…, compose = true)`, `androidBinary(…, compose = true)`, …
- Dedicated UI: `composeWidget { }` (always enables Compose).
- You still add Compose **libraries** yourself via catalogs.

Details: [COMPOSE.md](COMPOSE.md). Sample:
`application/common/greeting/compose-widget`.

---

## 10. Day-to-day workflow

```bash
# From the app root (application/ in this repo)
./gradlew :binary:assembleDebug
./gradlew :feature-home-impl:compileDebugKotlin
./gradlew build
```

- Prefer small targets; keep feature folders consistent.
- Put shared non-feature code under `core/` or `common/` (see sample).
- When a validator fails, fix the dependency edge or re-slice the target —
  don’t disable validation. For **forked-in** third-party modules only, use
  project-global `dependencyValidationExclusions` on `androidProjectConfiguration`
  (F-090) — see [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md) and
  [PROJECT-CONFIGURATION.md](PROJECT-CONFIGURATION.md). Never use that list to
  bypass first-party matrix rules (e.g. `impl`↛`impl`).

---

## 11. Common pitfalls

| Symptom | Likely cause |
|---------|----------------|
| “Java not found” / old JDK | Use JDK 21 (or 17+ min); export `JAVA_HOME` ([ENV.md](ENV.md)) |
| SDK / `compileSdk` errors | Install platform **37** (`platforms;android-37.0` + `android-37` symlink); set `local.properties` `sdk.dir` |
| Project not included | Missing `build.gradle.kts`, or nested `settings.gradle.kts` blocked Includer |
| Illegal project dependency | Matrix violation — see [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md). Forked vendor modules: global `dependencyValidationExclusions` only (F-090) |
| Empty / wrong package | `packageName` ≠ directory under `src/main/java` — run `formaLayoutCheck` / `formaLayoutGenerate` |
| Compose compiler mismatch | Align `composeCompilerVersion` with Kotlin (sample: 2.3.21 ↔ 2.3.21) |
| AGP resolution conflicts | Align consumer `agpVersion` with plugin AGP line (**9.3.0** today) |

---

## 12. Next reading

| Doc | Topic |
|-----|--------|
| [SAMPLE-APP.md](SAMPLE-APP.md) | Multi-feature gold standard layout |
| [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md) | Allowed project-dep edges (code truth) |
| [DEPS-CATALOG.md](DEPS-CATALOG.md) | External catalogs (`library`, `bundle`, `plugin`) |
| [COMPOSE.md](COMPOSE.md) | Compose flags + `composeWidget` |
| [PROJECT-CONFIGURATION.md](PROJECT-CONFIGURATION.md) | One global config path + settings store (F-082) |
| [ENV.md](ENV.md) | JDK / SDK bootstrap |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Monorepo + plugin module graph |
| [VISION.md](VISION.md) | forma-core → JVM → Bazel roadmap |
| [JVM-GETTING-STARTED.md](JVM-GETTING-STARTED.md) | Pure JVM parallel tutorial (F-032) |
| [PROGRESSIVE-EXAMPLES.md](PROGRESSIVE-EXAMPLES.md) | Feature-by-feature ladders + agent skills (F-050) |

---

## 13. Checklist — “I’m using Forma correctly”

- [ ] Root `androidProjectConfiguration` sets SDK + AGP once
- [ ] Includer (or explicit includes) discovers every target
- [ ] Features use `api` / `impl` (+ `res` / `viewbinding` as needed)
- [ ] No `impl` → `impl` edges
- [ ] Composition roots list feature entrypoints explicitly
- [ ] External versions live in a catalog, not copy-pasted GAVs in every target
- [ ] `./gradlew :binary:assembleDebug` succeeds on a clean machine with JDK 21 + SDK 35
