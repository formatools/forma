# Target plugins API design (F-070)

Design contract for **external Gradle plugin support on Forma targets**.

**Status:** accepted design (revised). Prior free-form `plugins = plugins(…)`
list-on-every-target sketch is **rejected** (see §10).

**Implementation:** F-071…F-073.

**Related:** [`DEPS-CATALOG.md`](DEPS-CATALOG.md), [`ARCHITECTURE.md`](ARCHITECTURE.md),
[`forma-core-api.md`](forma-core-api.md), `TargetRegistry` / `TargetRegistration`.

---

## 1. Problem

Today external plugins are bolted on via **chain API**:

```kotlin
androidRes(…).withPlugin(Plugins.navigationSafeArgs)
// androidBinary(…).withPlugins(Plugins.googleServices, Plugins.crashlytics())
```

Issues:

1. **Plugin identity is chosen ad hoc at each module** (`PluginWrapper` + raw
   plugin ids) — not tied to target *types* or a registration surface.
2. **Not uniform** — only some DSLs return `TargetBuilder`.
3. **Split definition** — target call vs `.withPlugin` chain.
4. **No custom-target story** — can’t say “this target *kind* always carries
   plugin X; instances only pass config.”
5. GH **#36** (docs) never closed because the product API was wrong.

Catalog dependency-driven apply (KSP) and internal `FeatureDefinition` (AGP /
Kotlin / Compose) stay separate — they are not this design.

---

## 2. Design principle (product intent)

> **Plugin identity is not a free-form list on every target call.**

Two supported ways to attach an external Gradle plugin:

| Path | When | Where plugin id lives | What the target definition takes |
|------|------|------------------------|-----------------------------------|
| **A. Static registration** | Using **pre-defined** target types (`impl`, `api`, `androidRes`, `androidBinary`, …) | **Static registration API** (settings / platform bootstrap) | Optional **plugin config** only (uniform shape) |
| **B. Custom target type** | Plugin defines a reusable *kind* of module | **On the target type** registration | Same optional **plugin config** arg |

**Not supported as the product API:**

```kotlin
// REJECTED — free-form plugin shopping list on every module
androidRes(..., plugins = plugins(plugin("some.id"), plugin("other.id")))
```

That reintroduces unstructured Gradle habits Forma exists to remove.

---

## 3. Goals and non-goals

### Goals

1. **Deprecate** `TargetBuilder.withPlugin` / `withPlugins` and public
   `PluginWrapper` as the consumer UX.
2. **Static API** to register external plugins for use with **pre-defined**
   target types (handles / bindings — not raw id soup at each callsite).
3. **Custom target types** declare **plugin (identity) on the type**; instances
   pass optional **config**.
4. **Uniform plugin config** model for the per-target-definition argument
   (simple: omit or empty; complex: single config arg — not a second chain API).
5. Keep Mode “catalog / KSP” and Mode “platform features” unchanged and documented
   as different concerns.

### Non-goals

| Concern | Decision |
|---------|----------|
| Arbitrary multi-plugin lists on every built-in DSL | **Rejected** |
| Replacing `FeatureDefinition` for AGP/Kotlin/Compose | Out of scope |
| Replacing catalog `plugin()` + companion-lib apply | Out of scope |
| Plugin classpath / Portal resolution redesign | Out of scope |
| One new built-in DSL per popular plugin (`safeArgsRes` as forever core) | Prefer **custom type** in the consumer graph, or static binding + config |

---

## 4. Conceptual model

```
┌──────────────────────────────────────────────────────────────────────┐
│ Platform features (internal)                                         │
│   FeatureDefinition — AGP, kotlin-android, compose compiler, …       │
└──────────────────────────────────────────────────────────────────────┘
┌──────────────────────────────────────────────────────────────────────┐
│ Dependency-driven (catalog)                                          │
│   settings plugin(id, CustomConfiguration, companion libs)           │
│   → apply when target depends on companion artifact (KSP, …)         │
└──────────────────────────────────────────────────────────────────────┘
┌──────────────────────────────────────────────────────────────────────┐
│ External plugins (THIS DESIGN)                                       │
│                                                                      │
│  Path A — pre-defined types                                          │
│    static register(PluginBinding) → optional pluginConfig on target  │
│                                                                      │
│  Path B — custom target type                                         │
│    register type + plugin identity → optional pluginConfig on target │
└──────────────────────────────────────────────────────────────────────┘
```

**Invariant:** at a target *call site*, consumers configure plugins only through
a **uniform optional config argument** (and/or a registered binding reference).
They do not pass open-ended lists of plugin ids.

---

## 5. Path A — Static registration (pre-defined target types)

### 5.1 Registration API (settings / root / platform config time)

Register plugin bindings once. Registration creates a **typed handle** the rest
of the build refers to — plugin id, optional default companion deps, optional
allowed pre-defined target types, optional extension type.

```kotlin
// Conceptual public API (names flexible in F-071)

interface PluginBinding<E : Any = Any> {
    val id: String
    // implementation details: extension KClass, default deps, allowed types
}

// Static registration — e.g. next to androidProjectConfiguration / settings
fun <E : Any> registerTargetPlugin(
    id: String,
    extensionClass: KClass<E>? = null,
    allowedTypes: Set<TargetType> = emptySet(), // empty = any pre-defined type that accepts config
    dependencies: FormaDependency = emptyDependency(),
): PluginBinding<E>
```

**Sample registration (app or `build-dependencies`):**

```kotlin
val navigationSafeArgs = registerTargetPlugin(
    id = "androidx.navigation.safeargs.kotlin",
    allowedTypes = setOf(AndroidTargetTypes.res),
)

val googleServices = registerTargetPlugin(
    id = "com.google.gms.google-services",
    allowedTypes = setOf(AndroidTargetTypes.binary, AndroidTargetTypes.app),
)

val crashlytics = registerTargetPlugin(
    id = "com.google.firebase.crashlytics",
    extensionClass = CrashlyticsExtension::class,
    allowedTypes = setOf(AndroidTargetTypes.binary),
    dependencies = google.firebase,
)
```

Classpath / version resolution stays with existing `pluginManagement` /
`buildscript` / catalog markers — registration does not replace that.

### 5.2 Pre-defined target definition — optional config only

Built-in DSLs gain a **uniform optional** argument for plugin configuration —
not a vararg plugin id list.

```kotlin
/**
 * Uniform per-target plugin configuration.
 *
 * - [binding] — handle from [registerTargetPlugin] (required if any plugin apply is requested)
 * - [configure] — optional extension configuration when the binding has an extension type
 *
 * Multiple plugins on one pre-defined module: prefer Path B (custom type that
 * owns the plugin set), or a single composite binding if F-071 finds a clean shape.
 * v1 may allow a small ordered list of [PluginUse] inside one config arg — still
 * one parameter, still only registered bindings (no raw ids).
 */
class TargetPluginConfig private constructor(
    val uses: List<PluginUse>,
) {
    companion object {
        val None: TargetPluginConfig = TargetPluginConfig(emptyList())
    }
}

class PluginUse internal constructor(
    val binding: PluginBinding<*>,
    val configure: (Any.() -> Unit)? = null,
)

fun pluginConfig(binding: PluginBinding<*>): TargetPluginConfig =
    TargetPluginConfig(/* single use, no configure */)

fun <E : Any> pluginConfig(
    binding: PluginBinding<E>,
    configure: E.() -> Unit,
): TargetPluginConfig = …

fun pluginConfig(vararg uses: PluginUse): TargetPluginConfig = …
fun <E : Any> PluginBinding<E>.withConfig(configure: E.() -> Unit): PluginUse = …
```

**Call sites (pre-defined types):**

```kotlin
// Simple — registered binding, no extension config
androidRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(androidx.navigation),
    pluginConfig = pluginConfig(navigationSafeArgs),
)

// Complex — still one arg; registered bindings only (no raw ids)
androidBinary(
    packageName = "tools.forma.sample.app",
    versionCode = 1,
    versionName = "0.0.1",
    dependencies = deps(/* … */),
    pluginConfig = pluginConfig(
        googleServices,
        crashlytics.withConfig {
            mappingFileUploadEnabled = false
        },
    ),
)
**Rules**

1. Only **registered** bindings — raw plugin id strings are not accepted on
   target DSLs.
2. If `allowedTypes` is non-empty, binding must be allowed for that target’s
   `TargetType` or configuration fails fast.
3. Apply order: platform features → target plugins from config → dependencies
   (same as today’s need for AGP-before-safe-args).
4. Default: `pluginConfig = TargetPluginConfig.None` (no external plugins).

This is “static API for plugin registration” + “plugin config as part of target
definition” for pre-defined types.

---

## 6. Path B — Custom target type + plugin on the type

When a plugin (or fixed plugin set) **defines a reusable module kind**, put
plugin identity on the **type**, not on every instance.

### 6.1 Type registration

Extend registration beyond pure `TargetRegistration` (restriction/content) with
optional Gradle plugin identity owned by the type:

```kotlin
// Conceptual — platform Gradle layer (not pure forma-core)

data class CustomTargetType(
    val registration: TargetRegistration, // type id, suffix, allow-list, content rules
    /**
     * Plugin(s) always applied for this kind.
     * Identity lives here — instances do not re-state plugin ids.
     */
    val plugins: List<PluginBinding<*>> = emptyList(),
    // optional: factory for default pluginConfig
)

// Consumer / platform helper
fun registerCustomAndroidTarget(
    id: String,
    nameSuffix: String,
    allowedDependencies: Set<TargetType>,
    contentRules: List<ContentRule> = emptyList(),
    plugins: List<PluginBinding<*>> = emptyList(),
    // or single plugin: plugin: PluginBinding<*>? = null
): CustomTargetType
```

**Example — navigation graphs kind:**

```kotlin
val navigationSafeArgs = registerTargetPlugin(
    id = "androidx.navigation.safeargs.kotlin",
)

val navigationRes = registerCustomAndroidTarget(
    id = "sample.navigation-res",
    nameSuffix = "res", // or "navigation-res" if suffix isolation desired
    allowedDependencies = setOf(
        AndroidTargetTypes.res,
        AndroidTargetTypes.widget,
        AndroidTargetTypes.composeWidget,
    ),
    contentRules = listOf(OnlyResourcesUnderMain),
    plugins = listOf(navigationSafeArgs),
)
```

### 6.2 Instance definition — optional config only

```kotlin
// Generated or generic DSL — shape is what matters
navigationRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(androidx.navigation),
    // plugin id already on the type — only optional config:
    pluginConfig = TargetPluginConfig.None,
)

// With extension config (single uniform arg)
firebaseBinary(
    packageName = "…",
    versionCode = 1,
    versionName = "0.0.1",
    dependencies = deps(/* … */),
    pluginConfig = pluginConfig(
        crashlytics.withConfig { mappingFileUploadEnabled = false },
    ),
)
```

**Apply semantics for Path B**

1. Validate self-type / content from `TargetRegistration`.
2. Apply platform features for the base kind (library vs binary vs …).
3. Apply **type-owned** plugins (always).
4. Apply **instance** `pluginConfig` (extra bindings only if the type allows
   overrides; v1 can restrict instance config to configuring type-owned
   plugins’ extensions — simplest and clearest).
5. `applyDependencies`.

**Recommendation for v1 instance config on Path B:** `pluginConfig` only
**configures** plugins already declared on the type (extension lambdas keyed by
binding). It does **not** add new plugin ids. That keeps type = identity,
instance = config.

---

## 7. Uniform `pluginConfig` argument

Across **all** pre-defined DSLs and custom-target entrypoints:

```kotlin
pluginConfig: TargetPluginConfig = TargetPluginConfig.None
```

| Case | Shape |
|------|--------|
| No external plugin | omit / `None` |
| Simple (apply registered / type-owned plugin, no extension knobs) | `pluginConfig = pluginConfig(binding)` or type-owned with `None` |
| Complex extension settings | **single** `pluginConfig = pluginConfig(binding.withConfig { … })` |
| Complex *kind* (different role or permanent plugin set) | **Path B** custom target type |

No `.withPlugin` chain. No second structural style.

---

## 8. Deprecation plan

| Remove / deprecate | Replacement |
|--------------------|-------------|
| `TargetBuilder.withPlugin` | Path A `pluginConfig` or Path B custom type |
| `TargetBuilder.withPlugins` | same |
| Public `PluginWrapper` / sample `Plugins.kt` wrappers as apply API | `registerTargetPlugin` + bindings |
| `pluginConfiguration { }` user construction | `binding.withConfig { }` inside `pluginConfig` |
| Returning `TargetBuilder` solely for chaining | Return `Unit`; temporary deprecated shim OK for one minor |

**Sample migration**

```kotlin
// BEFORE
androidRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
).withPlugin(Plugins.navigationSafeArgs)

// AFTER — Path A
androidRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
    pluginConfig = pluginConfig(navigationSafeArgs), // registered handle
)

// AFTER — Path B (preferred if every navigation res always needs safe-args)
navigationRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
)
```

```kotlin
// BEFORE
androidBinary(… )
    .withPlugins(Plugins.googleServices, Plugins.crashlytics())

// AFTER — Path A with single config arg
androidBinary(
    …,
    pluginConfig = pluginConfig(
        googleServices,
        crashlytics.withConfig { mappingFileUploadEnabled = false },
    ),
)

// AFTER — Path B if this binary kind is always “firebase app binary”
firebaseBinary(
    …,
    pluginConfig = pluginConfig(
        crashlytics.withConfig { mappingFileUploadEnabled = false },
    ),
)
```

---

## 9. Implementation slices

| ID | Scope | Acceptance |
|----|--------|------------|
| **F-070** | This design (revised) | Merged on `v2` |
| **F-071** | `PluginBinding`, `registerTargetPlugin`, `TargetPluginConfig`, `applyTargetPluginConfig`; wire **optional `pluginConfig`** on all Android/JVM pre-defined DSLs; reject raw plugin ids | unit tests; plugins build green |
| **F-072** | Custom target type registration with type-owned plugins + instance config; migrate sample (safe-args ± binary); deprecate `TargetBuilder` / `PluginWrapper` | application green; no `.withPlugin` call sites |
| **F-073** | User docs + progressive example + agent skill; close GH #36 | docs match Paths A/B only |

---

## 10. Rejected alternatives

| Alternative | Why rejected |
|-------------|--------------|
| **Free-form `plugins = plugins(plugin("id"), …)` on every target** | Exactly the unstructured surface this work should avoid; plugin identity belongs on **registration** or **custom type** |
| Keep chain-only `TargetBuilder` | Split definition; inconsistent return types |
| Plugins only via catalog companion libs | Safe-args / GMS are not “consume this one library” |
| New core built-in DSL per plugin (`safeArgsAndroidRes`) | Explodes platform surface; Path B custom types cover consumer graphs |
| Multi-arg `safeArgs=`, `crashlytics=` booleans on every DSL | Not uniform; doesn’t scale; not a real config model |

---

## 11. Relationship to forma-core

| Concept | Layer |
|---------|--------|
| `TargetType`, `TargetRegistration`, allow-lists, content rules | `tools.forma:core` |
| `PluginBinding`, `registerTargetPlugin`, `TargetPluginConfig`, apply | Gradle / `:deps` + platform plugins |
| Custom type = `TargetRegistration` + type-owned plugin list | Registration in platform; pure graph fields stay core |

Core must not hard-code Google/AndroidX plugin ids.

---

## 12. Open questions (resolve in F-071)

1. **Multiple bindings in one `pluginConfig`** on Path A — allow ordered list of
   registered bindings (still one arg) vs force Path B whenever N>1.  
   **Lean allow list of registered bindings** for binary (GMS+Crashlytics).
2. **Path B instance config** — configure-only vs allow additive bindings.  
   **Lean configure-only** for v1.
3. **Suffix sharing** — custom `navigation-res` vs shared `res` suffix with
   distinct type id. Prefer distinct type id; suffix policy follows existing
   matrix / naming rules.
4. **Catalog `libs.plugins.*` handles** as `PluginBinding` source — nice
   follow-up; not required if registration takes plugin id string after
   classpath is already set up.

---

## 13. Summary

| | |
|--|--|
| **Deprecate** | Chain `withPlugin` / `PluginWrapper` UX |
| **Pre-defined types** | **Static** `registerTargetPlugin` → target takes optional **`pluginConfig`** |
| **Custom types** | **Plugin identity on the type** → instance optional **`pluginConfig`** |
| **Uniform config** | Single `pluginConfig: TargetPluginConfig` arg everywhere |
| **Never** | Free-form plugin id lists on every target definition |
