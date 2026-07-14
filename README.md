<p align="center">
    <a href="https://forma.tools" target="_blank" rel="noopener noreferrer"><img width="100" src="./img/press.svg" alt="Logo"></a>
</p>

<p align="center">
    <img src="https://github.com/formatools/forma/actions/workflows/main.yml/badge.svg" alt="CI"/>
    <a href="https://plugins.gradle.org/plugin/tools.forma.android"><img src="https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/tools/forma/android/tools.forma.android.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin"/></a>
    <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/formatools/forma">
    <img alt="License" src="https://img.shields.io/github/license/formatools/forma"/>
    <img alt="Contributors" src="https://img.shields.io/github/contributors/formatools/forma"/>
    <img alt="GitHub top language" src="https://img.shields.io/github/languages/top/formatools/forma"/>
    <img alt="GitHub closed pull requests" src="https://img.shields.io/github/issues-pr-closed/formatools/forma"/>
    <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/formatools/forma?style=social"/>
</p>

**Forma** - Kotlin first, Meta Build System with Android and Gradle support. Opinionated, scalable, thoughtfully
structured, type-safe and guided way to declare your project structure. Distributed as a Gradle plugin, Forma helps
developers to shift focus from `Build Configuration` to `Project Structure Declaration`, abstracting away build
configuration complexity.

- You don't need to be a gradle expert anymore
- Get rid of project configuration bad practices
- Type-safe, single method configuration for your targets, no room for error
- Built-in dependency visibility rules
- Target types - enforce scalable project structure
- High-performance builds: Gradle best practices are applied automatically
- Dependencies framework - helps developers to understand and deal with transitive dependencies hell
- Extensible - be the expert when you need to!
- And much more...

⚠️ We are using `target` term to express application components(e.g. modules or projects, depending in the context)
across documentation and code, there is a couple of reasons for that. `Module` term often confused with Dagger modules
which makes communication harder, `project` from the other hand used only in Gradle context but not in other build
systems like Buck and Bazel.

⚠️ This is early *alpha* release - please do try this at home🏠

[Presentation Link](https://www.beautiful.ai/player/-MLn7RnBBWeh7vePDoDq)

## Getting started

**New to Forma?** Start here:

1. [**Android project tutorial**](docs/GETTING-STARTED.md) — mental model, run the sample, greenfield skeleton, target cheat sheet (F-015 / GH #53)
2. [**JVM getting started**](docs/JVM-GETTING-STARTED.md) — pure JVM tutorial, run the sample, greenfield skeleton with `binary`, `api`/`impl`, target cheat sheet (F-032)
3. [Sample app gold standard](docs/SAMPLE-APP.md) — multi-feature layout to copy
4. [Dependency matrix](docs/DEPENDENCY-MATRIX.md) — what may depend on what
5. [External deps catalogs](docs/DEPS-CATALOG.md) · [Compose](docs/COMPOSE.md) · [Environment](docs/ENV.md)
6. [Plugin publish path](docs/PLUGIN-PUBLISH.md) — Portal metadata DSL + release notes (F-016)
7. [Configuration performance](docs/CONFIGURATION-PERFORMANCE.md) — measure + hot-path guidance (F-017 / GH #106)
8. [forma-core public API design](docs/forma-core-api.md) — extraction contract for types / restrictions / registry (F-020)
9. [JVM targets](docs/JVM-TARGETS.md) — pure JVM platform plugin `tools.forma.jvm` (F-030)
10. [JVM sample application](docs/JVM-SAMPLE.md) — multi-module pure-JVM gold standard (`jvm-application/`, F-031)
11. [Bazel adapter design](docs/BAZEL-ADAPTER.md) — target types to rules, restriction graph to visibility (F-040 design)
12. [Bazel adapter spike](bazel-adapter/README.md) — generate/check BUILD from Forma model (F-041; JVM-first)
13. [Bazel sample (experimental)](bazel-sample/) — minimal `kt_jvm_*` workspace exercising forma-core matrix + `impl` ↛ `impl` (F-042)

Configuration made easy:

``` gradle
// root build.gradle.kts
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("tools.forma.android") version "0.1.3"
}

// Configure shared aspects of your android Project
buildscript {
    androidProjectConfiguration(
        project = project,
        minSdk = 21,
        targetSdk = 33,
        compileSdk = 34,
        agpVersion = "8.1.2",
        extraPlugins = listOf(
            "androidx.navigation:navigation-safe-args-gradle-plugin:2.7.4",
            "com.google.firebase:firebase-crashlytics-gradle:2.9.9",
        )
    )
}
```

Your kotlin android library

``` gradle
// Single method, type-safe creation of your target
// Plugins applied automatically
// Project configuration shared between targets
androidLibrary(
    // Mandatory, visible from build configuration
    packageName = "tools.forma.sample.example",
    // External dependencies declaration, one universal syntax
    dependencies = deps(
        google.material,
        androidx.appcompat,
    ) + deps(
        // Internal project dependencies, declared separately from externals
        project(":demo-library")
    ),
    // Test dependencies declaration
    testDependencies = deps(
        test.junit
    ),
    // Android test dependencies declaration
    androidTestDependencies = deps(
        test.espresso
    )
)
```

## Development environment

Worker / contributor host setup (JDK 17+, Android SDK platform **34** for
`compileSdk`, 33 still useful for target): see [`docs/ENV.md`](docs/ENV.md) and
`source scripts/env-mac.sh`.

## External dependency catalogs

Declare third-party libraries and plugins once in `settings.gradle.kts` with
`projectDependencies` (version catalog), or use typed Kotlin catalogs
(`build-dependencies/` pattern). Full guide: [`docs/DEPS-CATALOG.md`](docs/DEPS-CATALOG.md).

```gradle
// settings.gradle.kts
projectDependencies(
    "libs",
    "com.jakewharton.timber:timber:5.0.1",
    library("io.coil-kt:coil:2.1.0", name = "coil"),
    bundle(name = "room", "androidx.room:room-runtime:2.5.1", /* … */),
    plugin("androidx.navigation:navigation-safe-args-gradle-plugin", "2.7.4"),
)
// modules: deps(libs.jakewhartonTimber, libs.coil, libs.bundles.room)
```

## Progress

DSL names match code entrypoints (`widget`, `util`, …). **Validation** =
project-dep type checks + content layout rules from live validators
([full matrix](docs/DEPENDENCY-MATRIX.md)).

| Supported target types | implemented | purpose | validation |
|:----------------------:|:-----------:|:-------:|:----------:|
| `androidBinary` | ✅ | Generate single APK | name + no `res/`; composition-root project-dep list |
| `androidApp` | ✅ | Application / root Android library | name + no `res/`; composition-root project-dep list |
| `androidLibrary` | ✅ | Android library | name + project-dep list (**no** `impl`) |
| `uiLibrary` | ✅ | Shared UI library for impl/widget | project-dep list |
| `widget` | ✅ | Custom View / UI component | project-dep list |
| `composeWidget` | ✅ | Compose UI component | always Compose + project-dep list |
| `androidRes` | ✅ | Resources only | only `res/` under `src/main` + project-dep list |
| `viewBinding` | ✅ | Layout-only view binding module | only `layout*` under `res` + project-dep list |
| `androidTestUtil` | ✅ | Shared code for Android tests | project-dep list |
| `androidUtil` | ✅ | Android library extensions | no `res/` + project-dep list |
| `testUtil` | ✅ | Shared code for unit tests | no `res/` + project-dep list |
| `util` | ✅ | JVM library extensions | no `res/` + project-dep list |
| `library` | ✅ | JVM library | project-dep list (shares `library` suffix with `androidLibrary`) |
| `api` | ✅ | Feature external APIs | no `res/` + project-dep list |
| `impl` | ✅ | Feature implementation | project-dep list (**no** other `impl`) |
| `androidNative` | ✅ | NDK / native | no `res/`; no project-dep validation yet |

## Dependency matrix

**Canonical reference (code truth):** [`docs/DEPENDENCY-MATRIX.md`](docs/DEPENDENCY-MATRIX.md).

That document is generated from each target’s
`applyDependencies(validator = …)` call and content helpers. Summary:

| Consumer | May depend on project suffixes… |
|----------|----------------------------------|
| `api` | `api`, `library` |
| `impl` | `api`, `android-util`, `test-util`, `util`, `library`, `ui-library`, `res`, `viewbinding`, `widget`, `compose-widget` |
| `library` (JVM) | `util`, `test-util` |
| `androidLibrary` | `library`, `util`, `android-util`, `test-util`, `res`, `api` |
| `uiLibrary` | `widget`, `compose-widget`, `util`, `android-util`, `res` |
| `util` | `util`, `library` |
| `androidUtil` | `android-util`, `test-util`, `res` |
| `testUtil` | `test-util`, `util` |
| `androidTestUtil` | `android-test-util`, `test-util` |
| `androidRes` | `res`, `widget`, `compose-widget` |
| `widget` | `ui-library`, `widget`, `compose-widget`, `util`, `android-util`, `res` |
| `composeWidget` | `ui-library`, `compose-widget`, `widget`, `util`, `android-util`, `res` |
| `viewBinding` | `api`, `widget`, `compose-widget`, `res`, `library`, `android-util` |
| `androidApp` | `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library` |
| `androidBinary` | `app`, `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library` |
| `androidNative` | *(no project-dep check)* |

Dagger2-friendly `api`/`impl` + composition roots: **F-011** (landed).
Jetpack Compose: per-target `compose` flag + `composeWidget` — **F-013**
([`docs/COMPOSE.md`](docs/COMPOSE.md)).
Sample multi-feature gold standard: **F-014**
([`docs/SAMPLE-APP.md`](docs/SAMPLE-APP.md)).
Android getting-started tutorial: **F-015**
([`docs/GETTING-STARTED.md`](docs/GETTING-STARTED.md)).
Plugin Portal publish path: **F-016**
([`docs/PLUGIN-PUBLISH.md`](docs/PLUGIN-PUBLISH.md)).
Configuration-time performance: **F-017**
([`docs/CONFIGURATION-PERFORMANCE.md`](docs/CONFIGURATION-PERFORMANCE.md)).
forma-core API design: **F-020** ([`docs/forma-core-api.md`](docs/forma-core-api.md)).
forma-core extraction (restriction engine + TargetType + Android matrix kit): **F-021** done (`plugins/core`).
forma-core validation SPI + content rules (Gradle facade in `:validation`): **F-022** done.
forma-core TargetRegistry + Android DSL consumers: **F-023** done.
Publish coordinates for `tools.forma:core` (library) + facade deprecation policy: **F-024** done (see [`docs/PLUGIN-PUBLISH.md`](docs/PLUGIN-PUBLISH.md)).
Pure JVM target set (`tools.forma.jvm`, `JvmTargetRegistry`): **F-030** done (see [`docs/JVM-TARGETS.md`](docs/JVM-TARGETS.md)).
JVM sample application + `binary` composition root: **F-031** done ([`docs/JVM-SAMPLE.md`](docs/JVM-SAMPLE.md), `jvm-application/`).
JVM getting-started tutorial: **F-032** done ([`docs/JVM-GETTING-STARTED.md`](docs/JVM-GETTING-STARTED.md)).
Bazel adapter mapping design (targets ↔ rules, visibility ↔ deps): **F-040** done (see [`docs/BAZEL-ADAPTER.md`](docs/BAZEL-ADAPTER.md)).
Bazel adapter spike (generate/check BUILD from portable model, JVM matrix): **F-041** done ([`bazel-adapter/`](bazel-adapter/), spike results in BAZEL-ADAPTER.md).
Minimal Bazel sample workspace (two features + library/util + binary, real `bazelisk build` + `run`): **F-042** done (see [`bazel-sample/`](bazel-sample/) + BAZEL-ADAPTER.md).

Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a>
from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>
