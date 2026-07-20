---
name: forma-target-plugins
description: Type-owned external Gradle plugins (Path A/B). Call sites attributes-only; never .withPlugin.
---

# Target plugins (agent skill)

External Gradle plugins (safe-args, GMS, Crashlytics, …) are **owned by the target type**,
not selected per module. Design: `docs/TARGET-PLUGINS.md`. Progressive step:
`examples/android/10-target-plugins/`. Sample: `build-dependencies/.../NavigationRes.kt`.

## When to load
- Adding Navigation safe-args, Firebase, or any third-party Gradle plugin to a Forma target
- Reviewing PRs that introduce `.withPlugin`, `plugins = plugins(…)`, or free-form plugin ids on DSL calls
- Scaffolding a derived type (Path B) or extending a pre-defined type (Path A)

## Model

| Layer | Owns |
|-------|------|
| Type / rule (`registerTargetPlugin` or `deriveTargetType`) | Plugin **identity** — auto-applies every call site |
| Call site (`build.gradle.kts`) | **Attributes only** (packageName, deps, rule attrs) |

Apply order inside DSL: validate → features → **type-owned plugins** → dependencies.

## Path A — extend pre-defined type (global for that kind)

```kotlin
registerTargetPlugin(
    AndroidTargetTypes.res,
    targetPlugin(id = "androidx.navigation.safeargs.kotlin"),
)
// Every androidRes(...) now applies safe-args — only if ALL res modules need it
androidRes(packageName = "…", dependencies = deps(…))
```

## Path B — derived type (preferred when selective)

```kotlin
private val navigationSafeArgs = targetPlugin(id = "androidx.navigation.safeargs.kotlin")
val NavigationResType = deriveTargetType(
    id = "sample.navigation-res",
    base = AndroidTargetTypes.res,
    nameSuffix = "res", // keep res suffix for matrix/name matcher unless isolating
    plugins = listOf(navigationSafeArgs),
)
fun Project.navigationRes(packageName: String, dependencies: FormaDependency = emptyDependency(), …) {
    resourcesTarget(type = NavigationResType, packageName = packageName, dependencies = dependencies, …)
}

// Call site
navigationRes(packageName = "…", dependencies = deps(…))
```

## Classpath vs apply

- `extraPlugins` on `androidProjectConfiguration` and catalog `plugin(...)` put jars on the
  **buildscript classpath**. They do **not** replace type-owned apply.
- Applying a plugin to a module = type binding + `applyTargetPlugins(type)` inside the DSL.

## APIs (Android)

```text
tools.forma.android.target.targetPlugin
tools.forma.android.target.registerTargetPlugin
tools.forma.android.target.deriveTargetType
// shared wiring for res-like types:
resourcesTarget(type = …, packageName = …, …)
```

## Forbidden

- `.withPlugin` / `.withPlugins` / `TargetBuilder` chains (deprecated F-072)
- Free-form `plugins = plugins(plugin("id"))` on targets
- Call-site `pluginConfig(binding)` that re-selects plugins
- New forever core DSL per vendor plugin when Path B derived type is enough
- Teaching removed `androidLibrary`

## Separate concerns (do not collapse)

- **FeatureDefinition** — AGP / Kotlin / Compose platform features
- **Catalog Mode-2** — KSP when companion lib is consumed
- **Type-owned plugins** — safe-args / GMS / Crashlytics style external plugins

## Checklist

1. Plugin id appears only in registration (Path A or B), never at call sites
2. Classpath entry exists (`extraPlugins` / `plugin(...)`) for the Gradle plugin jar
3. Derived type clones base allow-list/content rules (`deriveTargetType` + AndroidTargetRegistry)
4. Suffix choice documented (share `res` vs new suffix)
5. Progressive example or sample updated if teaching a new pattern
