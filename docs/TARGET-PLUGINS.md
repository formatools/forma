# Target plugins API design (F-070)

Design contract for **external Gradle plugin support on Forma targets**.

**North star:** call sites should feel like **Bazel `BUILD` rules** — declare the
target kind + attributes. Plugin identity is part of the **rule / target type**,
not restated on every module. Extending a type with a plugin **auto-applies** that
plugin on every call site of that type.

**Status:** F-070–F-073 **shipped** on `v2`. Registry + auto-apply · sample `navigationRes` Path B · user docs + progressive example `examples/android/10-target-plugins` + agent skill `forma-target-plugins` · GH #36 closed. **F-081:** chain API **hard-removed**. **F-082:** single `androidProjectConfiguration` path (classpath-only `extraPlugins`) — see [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) and [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md).

**Related:** [`DEPS-CATALOG.md`](DEPS-CATALOG.md), [`ARCHITECTURE.md`](ARCHITECTURE.md),
[`forma-core-api.md`](forma-core-api.md), `TargetRegistry` / `TargetRegistration`.


## Quick start (users)

1. Put the Gradle plugin jar on the **classpath** (`extraPlugins` / catalog `plugin(...)`).
   See [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) — `extraPlugins` is buildscript-classpath only.
   Local convention plugins: **includeBuild + catalog plugin GAV**, not `project(":…")`
   ([`BUILDSCRIPT-PROJECT-CLASSPATH.md`](BUILDSCRIPT-PROJECT-CLASSPATH.md), F-100).
2. Bind the plugin to a **type** once:
   - **Path B (typical):** `targetPlugin` + `deriveTargetType` + thin DSL (see sample `NavigationRes.kt` / example `forma-defs/`).
   - **Path A:** `registerTargetPlugin(AndroidTargetTypes.res, …)` only if *every* module of that kind should get it.
3. Call sites set **attributes only** — never plugin ids, never `.withPlugin`.

Hands-on: [`examples/android/10-target-plugins`](../examples/android/10-target-plugins)
(safe-args) · [`examples/android/11-metro-di`](../examples/android/11-metro-di) (Metro DI).  
Agent skill: [`forma-target-plugins`](../examples/agent-skills/forma-target-plugins.md).

Global configuration lives at root `buildscript { androidProjectConfiguration(...) }` (F-082).

---

## 1. Problem

Today external plugins are bolted on via a **chain API** at each module:

```kotlin
androidRes(…).withPlugin(Plugins.navigationSafeArgs)
// androidBinary(…).withPlugins(Plugins.googleServices, Plugins.crashlytics())
```

That is the opposite of Bazel:

| Bazel | Forma today (broken) |
|-------|----------------------|
| Rule definition owns toolchains / aspects / implicit deps | Each `build.gradle.kts` re-selects plugins |
| `BUILD` file only sets **attributes** | `.withPlugin` after the target call |
| Using `my_rule(...)` always gets the rule’s behavior | Easy to forget safe-args on one res module |

Also: only some DSLs return `TargetBuilder`; docs (GH #36) never shipped.

Catalog dependency-driven apply (KSP) and internal `FeatureDefinition` (AGP /
Kotlin / Compose) stay separate.

---

## 2. Design principle

> **The target type (rule) owns the plugin. Call sites never pass plugin ids.**
>
> Extending a type with a plugin **automatically applies** it on **every** call
> site of that type — Bazel semantics in Gradle files.

| Layer | Owns | Analogy |
|-------|------|---------|
| **Type / rule registration** | Plugin identity (+ optional default config schema) | `rule()` / macro definition in `.bzl` |
| **Target definition** (`build.gradle.kts`) | Attributes only: package, deps, flags, optional **plugin config attrs** | `BUILD` target |

### Rejected shapes

```kotlin
// REJECTED — free-form plugin list on every module
androidRes(..., plugins = plugins(plugin("some.id")))

// REJECTED — re-state binding at every call site (still not Bazel)
androidRes(..., pluginConfig = pluginConfig(navigationSafeArgs))

// REJECTED — chain API
androidRes(...).withPlugin(Plugins.navigationSafeArgs)
```

---

## 3. Bazel → Forma mapping

```text
Bazel                              Forma (Gradle KTS)
─────────────────────────────      ──────────────────────────────────────────
load(":defs.bzl", "nav_res")       // type registered once (settings / buildSrc / included build)
nav_res(                           navigationRes(          // or generated DSL name
    name = "navigation",               // project name = directory (includer)
    package_name = "...",              packageName = "...",
    deps = ["//lib:nav"],              dependencies = deps(...),
)                                  )
```

Plugin `androidx.navigation.safeargs.kotlin` is attached when **`navigationRes`**
(the rule/type) is defined — not when each target is written.

```text
Bazel                              Forma
─────────────────────────────      ──────────────────────────────────────────
firebase_android_binary(           firebaseBinary(
    name = "app",                      packageName = "...",
    mapping_file_upload = False,       versionCode = 1,
    ...                                versionName = "0.0.1",
)                                      mappingFileUploadEnabled = false, // attr
                                       dependencies = deps(...),
                                   )
```

Config looks like **rule attributes**, not a separate plugin DSL bolted on the side.

---

## 4. Two registration paths

Both paths put plugin identity on the **type**. Neither puts plugin ids on call sites.

### Path A — Extend a **pre-defined** target type (static API)

Use when many/all modules of an existing kind should carry a plugin, or when you
want a thin alias over a built-in kind without a new suffix.

```kotlin
// Registered once (settings / androidProjectConfiguration / platform bootstrap)
// Conceptual API — names finalized in F-071

registerTargetPlugin(
    // which pre-defined type is being extended
    targetType = AndroidTargetTypes.res,
    pluginId = "androidx.navigation.safeargs.kotlin",
)

// From this point, EVERY androidRes(...) call site auto-applies safe-args.
androidRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(androidx.navigation),
) // ← no plugin mention; type already carries it
```

**Caution:** Path A is global for that pre-defined type in the registry scope.
If only *some* res modules need safe-args, use **Path B** (derived type) so
plain `androidRes` stays clean.

Static API responsibilities:

- Associate `pluginId` (+ optional companion deps, extension class) with a
  `TargetType` already in `TargetRegistry`
- Optional: default attribute values / schema for plugin config
- Idempotent apply at configuration time for every project that uses that type’s DSL

### Path B — **Custom / derived target type** (preferred for selective use)

Use when the plugin defines a **reusable kind** of module (Bazel: new rule or
macro wrapping a base rule).

```kotlin
// defs — once
val navigationSafeArgs = targetPlugin(
    id = "androidx.navigation.safeargs.kotlin",
)

val navigationRes = deriveTargetType(
    id = "sample.navigation-res",
    base = AndroidTargetTypes.res,       // matrix, content rules, AGP library features
    nameSuffix = "res",                  // or "navigation-res" if isolation desired
    plugins = listOf(navigationSafeArgs),
)

// Optional: expose a DSL entrypoint bound to that type
fun Project.navigationRes(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    // plugin-specific attrs only if needed — see §5
) = androidTarget(
    type = navigationRes,
    packageName = packageName,
    dependencies = dependencies,
)
```

**Every** `navigationRes { … }` call site auto-applies safe-args. Call sites stay
Bazel-flat:

```kotlin
// application/core/navigation/res/build.gradle.kts
navigationRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(androidx.navigation),
)
```

No `.withPlugin`, no `pluginConfig = pluginConfig(binding)`.

**Build cache (F-089 / GH #110):** keep
`navigation-safe-args-gradle-plugin` on a line where
`ArgumentsGenerationTask.navigationFiles` is `@PathSensitive(RELATIVE)`
(sample **2.9.8**). See [`CONFIGURATION-PERFORMANCE.md`](CONFIGURATION-PERFORMANCE.md)
§ Navigation Safe Args task cache — do not paper over with call-site plugin APIs.

**Presentation vs graphs (F-102 / GH #46):** `navigationRes` owns **graphs +
safe-args apply**. Keeping Jetpack Navigation / `*Directions` out of feature
`impl` is **app architecture** (ports + root adapter), not a new Forma router
DSL — see [`NAVIGATION-ABSTRACTION.md`](NAVIGATION-ABSTRACTION.md).

---

## 5. Plugin config = rule attributes (uniform, optional)

When a plugin needs per-target settings (Crashlytics mapping upload, etc.):

1. **Simple / no knobs** — type has the plugin; call site has **zero** plugin surface.
2. **Complex** — either:
   - **Derived type** with extra attributes on that type’s DSL (Bazel rule attrs), or
   - **Single optional config arg** on that type’s DSL whose shape is defined with
     the type (not a free-form plugin id list)

```kotlin
// Type definition owns plugin + config schema
val crashlytics = targetPlugin(
    id = "com.google.firebase.crashlytics",
    extensionClass = CrashlyticsExtension::class,
    dependencies = google.firebase,
)
val googleServices = targetPlugin(id = "com.google.gms.google-services")

val firebaseBinary = deriveTargetType(
    id = "sample.firebase-binary",
    base = AndroidTargetTypes.binary,
    plugins = listOf(googleServices, crashlytics),
    // schema: which attrs map into plugin extensions
)

// Call site — Bazel-like attributes only
firebaseBinary(
    packageName = "tools.forma.sample.app",
    versionCode = 1,
    versionName = "0.0.1",
    mappingFileUploadEnabled = false, // rule attr → CrashlyticsExtension
    dependencies = deps(/* … */),
)
```

**Uniform config model (implementation detail):** types may accept one optional
`pluginConfig: T? = null` where `T` is a **type-associated** config data class —
not `List<PluginId>`. Prefer first-class named attrs on the DSL when there are
few knobs (more Bazel-like).

```kotlin
// Acceptable single-arg form when attrs would explode the signature
firebaseBinary(
    packageName = "…",
    versionCode = 1,
    versionName = "0.0.1",
    dependencies = deps(/* … */),
    pluginConfig = CrashlyticsConfig(mappingFileUploadEnabled = false),
)
```

Still: **no plugin ids at the call site.** Config type is fixed by the rule.

---

## 6. Runtime sequence (Bazel “rule implementation”)

For a project using type `T`:

```
1. Resolve TargetRegistration for T (suffix, allow-list, content rules)
2. Self-validate project name; run content rules
3. applyFeatures — platform FeatureDefinitions for base kind (AGP, Kotlin, …)
4. apply type-owned external plugins   ← automatic from type; never from call-site ids
5. apply plugin config attributes (if any) onto plugin extensions
6. applyDependencies (catalog Mode-2 side effects may still apply KSP, etc.)
```

Extending the type updates step 4 for **all** existing and future call sites.

---

## 7. Pre-defined DSLs after this work

Built-in entrypoints (`impl`, `api`, `androidRes`, `androidBinary`, …):

- Return **`Unit`** (no `TargetBuilder` chain).
- Do **not** grow a `plugins = …` parameter.
- If Path A registered plugins on their `TargetType`, those apply automatically
  inside the DSL implementation via registry lookup.
- Optional plugin-related **attributes** only appear on **derived** DSLs (Path B)
  or on a pre-defined DSL if Path A attached a config schema (rare).

---

## 8. Deprecation → removal (F-072 / F-081)

| Removed (F-081) | Replacement |
|-----------------|-------------|
| `TargetBuilder.withPlugin` / `withPlugins` | Type-owned plugins (Path A or B) |
| Public `PluginWrapper` + sample `Plugins.kt` | `targetPlugin` + `deriveTargetType` / `registerTargetPlugin` |
| Call-site plugin id lists / binding re-selection | Impossible in the new API |
| Non-`Unit` builder returns | All target DSLs return **`Unit`** |

Call-site attribute inventory: [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md).

**Sample migration**

```kotlin
// BEFORE
androidRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
).withPlugin(Plugins.navigationSafeArgs)

// AFTER — Path B (typical)
navigationRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
)

// AFTER — Path A only if ALL androidRes modules should get safe-args
// registerTargetPlugin(AndroidTargetTypes.res, safeArgs) once, then:
androidRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
)
```

```kotlin
// BEFORE
androidBinary(…).withPlugins(Plugins.googleServices, Plugins.crashlytics())

// AFTER
firebaseBinary(
    packageName = "…",
    versionCode = 1,
    versionName = "0.0.1",
    mappingFileUploadEnabled = false,
    dependencies = deps(/* … */),
)
```

---

## 9. Implementation slices

| ID | Scope | Acceptance |
|----|--------|------------|
| **F-070** | This design | Merged on `v2` |
| **F-071** | `targetPlugin`, type→plugin registry, auto-apply in pre-defined DSLs (Path A lookup), `deriveTargetType` core hooks | unit tests: type with plugin applies without call-site API; plugins build green |
| **F-072** | Path B DSL helpers; migrate sample navigation (+ optional firebase binary type); hard-deprecate then **F-081 remove** `TargetBuilder` / `PluginWrapper` | application green; **zero** `.withPlugin` call sites; call sites Bazel-flat |
| **F-073** | Docs + progressive example + agent skill; close GH #36 | **done** — `docs/TARGET-PLUGINS.md` how-to, `examples/android/10-target-plugins`, `forma-target-plugins` skill |

---

## 10. Rejected alternatives (history)

| Alternative | Why rejected |
|-------------|--------------|
| Free-form `plugins = plugins(plugin("id")…)` on every target | Unstructured; anti-Bazel |
| Call-site `pluginConfig = pluginConfig(registeredBinding)` | Still re-selects plugins per module; first “Path A” example was **wrong** |
| Chain `TargetBuilder.withPlugin` | Split definition; inconsistent returns |
| New forever built-in core DSL per vendor plugin | Prefer consumer Path B derived types |
| Catalog-only | Safe-args / GMS are not companion-lib driven |

---

## 11. forma-core vs Gradle layer

| Concept | Layer |
|---------|--------|
| `TargetType`, allow-lists, content rules, registry | `tools.forma:core` |
| Type → external plugin bindings, auto-apply, config→extension | Gradle `:deps` / platform plugins |
| Derived type = core registration + plugin list | Platform registration API |

Core does not hard-code AndroidX/Google plugin ids.

---

## 12. Open questions (F-071)

1. **Path A scope** — settings-wide vs project hierarchy. Lean: same scope as
   `TargetRegistry` / `androidProjectConfiguration`.
2. **Suffix** for derived types — share `res` vs `navigation-res`. Document
   tradeoff; sample may keep `res` suffix with distinct type id.
3. **Config wiring** — named DSL attrs vs single `pluginConfig: T`. Prefer named
   attrs when ≤3 knobs; single typed config arg otherwise.
4. **Multiple plugins on one type** — ordered list on the type (GMS then
   Crashlytics); call site still silent.

---

## 13. Summary

| | |
|--|--|
| **Model** | Bazel rules in Gradle files |
| **Plugin identity** | On the **target type** (static extend pre-defined **or** derived type) |
| **Call site** | Attributes only — **auto-apply** type plugins every time |
| **Config** | Rule attributes or one type-associated config arg — **never** plugin ids |
| **Removed (F-081)** | `.withPlugin` / `PluginWrapper` / sample `Plugins` chain |
| **Never** | Per-module plugin shopping lists or re-binding at each target |
