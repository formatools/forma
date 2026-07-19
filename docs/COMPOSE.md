# Jetpack Compose support (F-013 / GH #96)

Forma enables Compose **per target** (with an optional project-wide default),
and provides a dedicated **`composeWidget`** target for Compose UI components.

## Project-wide settings

In the root `build.gradle.kts` `buildscript` block:

```kotlin
androidProjectConfiguration(
    project = rootProject,
    minSdk = 21,
    targetSdk = 33,
    // compileSdk 34 when using modern Compose transitive AARs
    compileSdk = 34,
    agpVersion = "8.1.2",
    compose = false, // default for per-target flags
    composeCompilerVersion = "1.5.10", // must match Kotlin (1.9.22 → 1.5.10)
)
```

| Setting | Role |
|---------|------|
| `compose` | Default value for per-target `compose = …` parameters on `impl`, `androidUtil`, `androidApp`, `uiLibrary`, `androidBinary`. |
| `composeCompilerVersion` | Written to AGP `composeOptions.kotlinCompilerExtensionVersion` when Compose is enabled. |

`composeWidget` modules **always** enable Compose; they ignore the project default
(they do not need `compose = true`).

## Per-target `compose` flag

```kotlin
impl(
    packageName = "com.example.feature.home.impl",
    compose = true,
    dependencies = deps(/* … */),
)

uiLibrary(
    packageName = "com.example.shared.ui.library",
    compose = true,
)

androidBinary(
    packageName = "com.example.app",
    versionCode = 1,
    versionName = "1.0",
    compose = true,
    dependencies = deps(target(":root-app"), /* … */),
)
```

When `compose = true`, Forma sets:

- `android.buildFeatures.compose = true`
- `android.composeOptions.kotlinCompilerExtensionVersion = <composeCompilerVersion>`

You still declare Compose **libraries** yourself (runtime, UI, material, …)
via catalogs / `deps(...)`. Forma does not inject Compose artifacts automatically.

## `composeWidget` target

Suffix: **`compose-widget`** (project name must end with `-compose-widget` or equal
`compose-widget`).

```kotlin
// application/common/greeting/compose-widget/build.gradle.kts
composeWidget(
    packageName = "com.example.common.greeting.compose.widget",
    dependencies = deps(
        androidx.compose // sample catalog cluster
    )
)
```

Parallel to `widget` (View system). View and Compose may depend on each other:

- `widget` → may depend on `compose-widget`
- `compose-widget` → may depend on `widget`

so hybrid screens can mix View and Compose (GH #96).

## Who may depend on `compose-widget`

| Consumer | `compose-widget` allowed? |
|----------|---------------------------|
| `impl` | yes |
| `androidApp` / `androidBinary` | yes |
| `uiLibrary` | yes |
| `widget` | yes |
| `composeWidget` | yes (self) |
| `androidRes` / `viewBinding` | yes (resources/layout wiring) |
| `api` / JVM `library` / `util` | no |

Full matrix: [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md).

## Compiler / Kotlin pairing

Default `composeCompilerVersion = "1.5.10"` matches Kotlin **1.9.22** (Gradle 8.7
embedded Kotlin used by the sample). If you bump Kotlin, update
`composeCompilerVersion` using the
[Compose Compiler compatibility map](https://developer.android.com/jetpack/androidx/releases/compose-kotlin).

## Sample

`application/common/greeting/compose-widget` — tiny `@Composable` card depended on
by `binary` with `compose = true`.
