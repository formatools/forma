# Target plugins API design (F-070)

Design contract for **uniform external Gradle plugin support on Forma
targets**. Grounded in live code under `plugins/android` + `plugins/deps`, the
sample `application/`, and open GH **#36** (docs for external plugins).

**Status:** accepted design (this document). **Implementation:** F-071…F-073.

**Related:** [`DEPS-CATALOG.md`](DEPS-CATALOG.md) (settings `plugin()` +
dependency-driven apply), [`ARCHITECTURE.md`](ARCHITECTURE.md),
[`forma-core-api.md`](forma-core-api.md).

---

## 1. Problem

Today external plugins are bolted on through **three overlapping paths**:

| Path | Where | Used for |
|------|--------|----------|
| **A. Chain API** | `TargetBuilder.withPlugin` / `withPlugins` + `PluginWrapper` | Safe-args on `androidRes`; commented Crashlytics/Google Services on `androidBinary` |
| **B. Dependency-driven** | Catalog `plugin(…, configuration, companionLibs)` + `applyDependencies` | KSP when a target depends on a processor registered with `CustomConfiguration("ksp")` |
| **C. Built-in features** | `FeatureDefinition` / `applyFeatures` | AGP, Kotlin Android, kapt-on-demand, Compose compiler |

Path **A** is the user-facing “external plugins” story and is broken as a product API:

1. **Not uniform.** Only some DSLs return `TargetBuilder`
   (`androidRes`, `uiLibrary`, `androidBinary`). `impl`, `api`, `widget`,
   `androidApp`, JVM targets, etc. return `Unit` — no chain, no plugins.
2. **Not part of the target definition.** Plugins are a post-hoc
   `.withPlugin(…)` after the target call, so configuration is split across
   two syntactic sites and easy to miss in reviews/examples.
3. **`PluginWrapper` is a second mini-DSL** (id + deps + typed extension)
   living in sample `build-dependencies` (`Plugins.kt`) rather than a first-class
   Forma target parameter.
4. **Docs never shipped** (GH #36) because there is no single story to document.
5. Paths **B** and **C** are the right *mechanisms* for their jobs; they must
   stay, but **must not** be confused with explicit target plugins.

---

## 2. Goals and non-goals

### Goals

1. **One uniform plugins argument** on every Forma target definition
   (Android + JVM), same shape everywhere.
2. **Simple cases** express plugins **inside** the target call (no chain).
3. **Complex cases** use the **same single argument** with richer
   `PluginSpec` values (typed extension config + optional companion deps) —
   **not** a new target type by default.
4. **Deprecate** `TargetBuilder.withPlugin` / `withPlugins` and the
   chain-return pattern as the supported plugins UX.
5. Keep **dependency-driven** catalog plugins (KSP/processors) and
   **built-in features** (AGP/Kotlin/Compose) as separate, documented modes.
6. Preserve type-safe extension configuration where Gradle extensions exist
   (today’s `pluginConfiguration<CrashlyticsExtension> { … }` capability).

### Non-goals

| Concern | Decision |
|---------|----------|
| New target types per popular plugin (e.g. `safeArgsRes`) | **Out** unless the plugin changes the *role* of the module enough to deserve a `TargetType` (see §6) |
| Replacing `FeatureDefinition` for AGP/Kotlin | **Out** — platform features stay internal |
| Replacing catalog `plugin()` + companion-lib apply | **Out** — path B stays for processor-style plugins |
| Plugin Portal / classpath resolution redesign | **Out** — consumers still put plugin markers on `buildscript` / `pluginManagement` as today |
| Arbitrary multi-step configuration builders beyond one plugins arg | **Out** for v1 — prefer recipes in shared modules |

---

## 3. Conceptual model — three plugin modes

Document and implement these as **distinct modes**:

```
┌─────────────────────────────────────────────────────────────┐
│ Mode 1 — Platform features (internal)                       │
│   applyFeatures(AGP, kotlin-android, compose compiler, …)   │
│   Not user-facing “plugins = …”                             │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│ Mode 2 — Dependency-driven (catalog)                        │
│   settings: plugin(id, version, CustomConfiguration, libs)  │
│   target: dependencies = deps(libs.roomCompiler)            │
│   → apply plugin + wire config when dep is consumed         │
│   See DEPS-CATALOG.md                                       │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│ Mode 3 — Target-declared plugins (THIS DESIGN)              │
│   target(…, plugins = plugins(…))                           │
│   Explicit apply + optional extension config + optional deps│
│   Safe-args, Google Services, Crashlytics, detekt, …        │
└─────────────────────────────────────────────────────────────┘
```

**Rule of thumb for authors**

| Situation | Mode |
|-----------|------|
| Always-on platform capability for this target kind | Mode 1 (Forma owns it) |
| “If I depend on this artifact, apply its plugin” | Mode 2 (catalog) |
| “This module needs plugin X regardless of a single lib” | Mode 3 (target `plugins`) |

---

## 4. Proposed public API

### 4.1 Single argument on every target

Every user-facing target DSL gains:

```kotlin
plugins: TargetPlugins = emptyPlugins()
```

**Examples — simple (id only)**

```kotlin
androidRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(androidx.navigation),
    plugins = plugins(
        "androidx.navigation.safeargs.kotlin",
    ),
)
```

**Examples — complex (typed config + companion deps) still one arg**

```kotlin
androidBinary(
    packageName = "tools.forma.sample.app",
    versionCode = 1,
    versionName = "0.0.1",
    dependencies = deps(/* … */),
    plugins = plugins(
        "com.google.gms.google-services",
        plugin(
            id = "com.google.firebase.crashlytics",
            dependencies = google.firebase,
        ) {
            // CrashlyticsExtension receiver
            mappingFileUploadEnabled = false
        },
    ),
)
```

**Shared recipes** (optional, still produce `PluginSpec` for the same arg):

```kotlin
// build-dependencies or app-owned module — not a second apply path
object AppPlugins {
    val navigationSafeArgs = plugin("androidx.navigation.safeargs.kotlin")
    val googleServices = plugin("com.google.gms.google-services")
    fun crashlytics(mappingFileUploadEnabled: Boolean = false) =
        plugin("com.google.firebase.crashlytics", dependencies = google.firebase) {
            this.mappingFileUploadEnabled = mappingFileUploadEnabled
        }
}

androidBinary(
    /* … */,
    plugins = plugins(AppPlugins.googleServices, AppPlugins.crashlytics()),
)
```

### 4.2 Types (plugins `:deps` or thin `:android` re-export)

Package suggestion: `tools.forma.deps.core` (next to today’s `PluginWrapper`)
with facades if needed.

```kotlin
/** Ordered list of external plugins to apply on a target. */
class TargetPlugins internal constructor(
    val specs: List<PluginSpec>,
) {
    companion object {
        val Empty: TargetPlugins = TargetPlugins(emptyList())
    }
}

fun emptyPlugins(): TargetPlugins = TargetPlugins.Empty

fun plugins(vararg specs: PluginSpec): TargetPlugins =
    TargetPlugins(specs.toList())

/** Convenience: bare plugin ids. */
fun plugins(vararg ids: String): TargetPlugins =
    TargetPlugins(ids.map { PluginSpec(id = it) })

/**
 * One external plugin application.
 *
 * @param id Gradle plugin id (already on classpath / pluginManagement)
 * @param dependencies optional deps applied with EmptyValidator (same as today)
 * @param configure optional extension configuration; null = apply only
 */
class PluginSpec internal constructor(
    val id: String,
    val dependencies: FormaDependency = emptyDependency(),
    val configure: ((Project) -> Unit)? = null,
)

/** Apply-only plugin. */
fun plugin(id: String, dependencies: FormaDependency = emptyDependency()): PluginSpec =
    PluginSpec(id = id, dependencies = dependencies)

/**
 * Type-safe extension configuration.
 * Replaces pluginConfiguration + PluginWrapper construction.
 */
inline fun <reified E : Any> plugin(
    id: String,
    dependencies: FormaDependency = emptyDependency(),
    noinline configure: E.() -> Unit,
): PluginSpec = PluginSpec(
    id = id,
    dependencies = dependencies,
    configure = { project ->
        configure(project.the(E::class))
    },
)
```

**Overload note:** `plugins("a", "b")` (strings) and `plugins(spec1, spec2)`
must not collide awkwardly. Prefer:

- `plugins(vararg specs: PluginSpec)` as the primary API
- `plugin("id")` always returns `PluginSpec`
- optional `plugins(ids: Iterable<String>)` if bare strings are desired without
  `plugin()` wrappers

Recommended call style in docs/examples:

```kotlin
plugins = plugins(
    plugin("androidx.navigation.safeargs.kotlin"),
)
```

Bare-string sugar is nice-to-have, not required for F-071.

### 4.3 Apply semantics

Internal helper (platform-agnostic enough for Android + JVM):

```kotlin
fun Project.applyTargetPlugins(plugins: TargetPlugins) {
    val applied = linkedSetOf<String>()
    for (spec in plugins.specs) {
        check(applied.add(spec.id)) {
            "Duplicate target plugin '${spec.id}' on project '$path'"
        }
        apply(plugin = spec.id)
        spec.configure?.invoke(this)
        if (spec.dependencies !== EmptyDependency) {
            applyDependencies(
                validator = EmptyValidator,
                dependencies = spec.dependencies,
                repositoriesConfiguration = EmptyRepositoriesConfiguration,
            )
        }
    }
}
```

**Ordering**

1. Self-name / content validation  
2. `applyFeatures` (Mode 1)  
3. `applyTargetPlugins` (Mode 3)  
4. `applyDependencies` (Mode 2 side effects + normal deps)

Rationale: feature plugins establish the project kind; external plugins often
expect AGP/Kotlin already applied (safe-args, Crashlytics).

**Idempotency:** Mode 2 may also `apply(plugin = …)`. Use Gradle’s natural
“already applied” behavior and/or a small per-project applied-id set shared
with `applyDependencies` if double-apply proves noisy. Do **not** invent a
global plugin registry in v1 beyond what’s needed for clear errors.

### 4.4 DSL surface changes

| Today | After F-071 |
|-------|-------------|
| `fun Project.impl(…): Unit` | `fun Project.impl(…, plugins: TargetPlugins = emptyPlugins())` |
| `fun Project.androidRes(…): TargetBuilder` | `fun Project.androidRes(…, plugins: TargetPlugins = emptyPlugins())` — return type **Unit** (or deprecated `TargetBuilder` shim) |
| Same for all Android + JVM targets | Same `plugins` param |

**Return type policy**

- Preferred end state: all target DSLs return **`Unit`**.
- Migration: keep returning `@Deprecated TargetBuilder` that only supports
  deprecated `withPlugin` forwarding to the same apply path for one minor
  (0.1.x), then hard-remove in the next breaking window (aligned with other
  DSL breaks).

---

## 5. Deprecation plan

### 5.1 API to deprecate (F-072)

| Symbol | Replacement |
|--------|-------------|
| `TargetBuilder.withPlugin` | `plugins = plugins(…)` on the target |
| `TargetBuilder.withPlugins` | same |
| Public construction of `PluginWrapper` | `plugin(…)` / `PluginSpec` |
| `pluginConfiguration { }` as user API | folded into `plugin(id) { }` |
| Sample `Plugins.kt` using `PluginWrapper` | rewrite to `PluginSpec` recipes |

### 5.2 Migration map (sample)

```kotlin
// BEFORE
androidRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
).withPlugin(Plugins.navigationSafeArgs)

// AFTER
androidRes(
    packageName = "…",
    dependencies = deps(androidx.navigation),
    plugins = plugins(plugin("androidx.navigation.safeargs.kotlin")),
    // or plugins(AppPlugins.navigationSafeArgs)
)
```

```kotlin
// BEFORE
androidBinary(… )
    .withPlugins(Plugins.googleServices, Plugins.crashlytics())

// AFTER
androidBinary(
    …,
    plugins = plugins(
        plugin("com.google.gms.google-services"),
        plugin("com.google.firebase.crashlytics", dependencies = google.firebase) {
            mappingFileUploadEnabled = false
        },
    ),
)
```

### 5.3 Compatibility shims

```kotlin
@Deprecated(
    message = "Pass plugins = plugins(…) on the target definition instead",
    replaceWith = ReplaceWith("/* move PluginWrapper into target plugins = */"),
)
class TargetBuilder(/* … */) {
    @Deprecated("…")
    fun withPlugin(pluginWrapper: PluginWrapper<*>): TargetBuilder { /* apply */ }
}
```

Convert `PluginWrapper` → internal adapter that builds `PluginSpec` once, or
mark `PluginWrapper` itself `@Deprecated` and implement `invoke` via
`applyTargetPlugins`.

---

## 6. When (not) to add a separate target type

User guidance: **prefer single-arg config**. Separate `TargetType` only if:

1. The plugin **changes the role / content rules / dependency matrix** of the
   module (new suffix, new allow-list), **and**
2. That role is common enough to teach in the progressive ladder, **and**
3. A boolean/plugins arg would **lie** about what the module is.

Examples:

| Case | Decision |
|------|----------|
| Navigation safe-args on a res module | **plugins arg** on `androidRes` |
| Crashlytics on binary | **plugins arg** on `androidBinary` |
| Detekt / Spotless on any module | **plugins arg** (or root convention — not Forma target type) |
| Hypothetical “proto codegen library” with unique deps matrix | **consider new target type** in a later ticket |

Do **not** mint `safeArgsAndroidRes` etc. in F-071–F-073.

---

## 7. Relationship to forma-core

- `TargetPlugins` / `PluginSpec` are **Gradle-facing** (need `Project.apply`,
  extensions). They live in **`:deps`** (or platform plugins), **not** in pure
  `tools.forma:core`.
- Core remains free of AGP and of “which Google plugin ids exist.”
- Bazel adapter does not need Mode 3 parity in v1; document as Gradle-only.

---

## 8. Docs and examples (F-073 / closes GH #36)

Ship as part of implementation, not a separate everlasting backlog item:

1. This design → short user guide section in `GETTING-STARTED.md` + dedicated
   “External plugins” page (can rename/trim this file to user voice once
   implemented).
2. `DEPS-CATALOG.md` — explicit “Mode 2 vs Mode 3” cross-link.
3. Progressive example step (Android ladder) showing safe-args via `plugins =`.
4. Agent skill blurb under `examples/agent-skills/`.
5. Close GH **#36** when user docs land on `v2`.

---

## 9. Implementation slices

| ID | Scope | Acceptance |
|----|--------|------------|
| **F-070** | This design doc + tickets | Merged on `v2`; no product code required |
| **F-071** | `TargetPlugins` / `PluginSpec` / `plugin()` / `applyTargetPlugins`; add `plugins` param to **all** Android + JVM target DSLs; apply after features | `plugins/` build green; unit tests for duplicate-id + empty path; sample still builds (may still use shim) |
| **F-072** | Migrate sample + `Plugins.kt` to new API; `@Deprecated` on `TargetBuilder` / `PluginWrapper` chain; binary Crashlytics TODO becomes real `plugins =` **or** stays commented but in new shape | `application/` green; no `.withPlugin` call sites in tree except tests for deprecation if any |
| **F-073** | User docs + progressive example + agent skill; close GH #36 | Docs-only PR ok if code already on `v2` |

---

## 10. Rejected alternatives

| Alternative | Why rejected |
|-------------|--------------|
| Keep chain-only, return `TargetBuilder` from every DSL | Still splits definition; teaches a builder pattern Forma otherwise avoids |
| Plugins only via catalog Mode 2 | Safe-args / Google Services are not “consume this one lib” |
| Separate target type per plugin | Explodes the matrix; fights flat role model |
| Multi-arg `plugin1=`, `plugin2=` or free lambda `plugins { }` block on Project | Harder to share recipes; single `TargetPlugins` value is copyable and testable |
| Configure plugins only in `androidProjectConfiguration` | Wrong scope — plugins are per-module |

---

## 11. Open questions (resolve during F-071 if needed)

1. **Duplicate apply** with Mode 2: warn vs silent — default silent (Gradle-safe).
2. **String vs PluginDependency** from version catalog (`libs.plugins.*`):
   nice follow-up to accept `Provider<PluginDependency>` in `plugin(…)`; not
   required for first slice if ids remain strings (classpath already resolved).
3. **Owner/visibility params** unused on some targets today — out of scope;
   don’t block plugins work on cleanup.

---

## 12. Summary

**Deprecate** chain-based `TargetBuilder` plugin support.  
**Standardize** on `plugins: TargetPlugins` as a **single argument of every
target definition**.  
**Simple** = list of plugin ids/specs in that arg.  
**Complex** = same arg with typed `plugin(id) { extension… }` + optional deps.  
**Separate target types** only when the plugin creates a new *role*, not for
wiring convenience.
