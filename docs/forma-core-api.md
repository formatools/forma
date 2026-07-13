# forma-core public API design (F-020)

Design ticket contract for extraction **F-021…F-024**. Grounded in the live
plugins under `plugins/` and in [`ARCHITECTURE.md`](ARCHITECTURE.md) §6 +
[`VISION.md`](VISION.md).

**Status:** accepted design. **Implemented so far:** F-021 (types +
restriction graph + Android kit), **F-022** (validation SPI + content
predicates in `plugins/core`; `:validation` is a Gradle facade). Registry /
DSL consumption remains **F-023**.

---

## 1. Goals and non-goals

### Goals

1. Peel a **platform-agnostic** library (`forma-core`) that owns:
   - target **type identity**
   - **restriction / visibility** rules for project-to-project edges
   - **validation framework** (SPI + default suffix validators)
   - **target registry** (declare types + allowed deps + content rules)
   - portable pieces of the **dependency model** and apply-time validation path
2. Keep **Android** as the first consumer (F-023): sample `application/` stays
   green; user DSL (`api`, `impl`, …) remains in the Android plugin.
3. Enable later **JVM** (F-030+) and **Bazel** (F-040+) adapters without
   forking the restriction engine.
4. Preserve today’s product rules (suffix names, `impl` ↛ `impl`, composition
   roots, content layout checks) — see [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md).

### Non-goals (stay out of `forma-core`)

| Concern | Stays in |
|---------|----------|
| AGP / Kotlin Android / Compose / kapt feature appliers | `:android` (or future platform plugins) |
| User-facing Gradle DSL entrypoints (`fun Project.impl(…)`) | platform plugins |
| Plugin Portal publish DSL (`formaPluginConfiguration`) | `plugins/buildSrc` |
| Includer / depgen | separate builds |
| Demo catalogs in `build-dependencies/` | sample only |
| Concrete Android SDK numbers, AGP/Kotlin versions | `AndroidProjectSettings` (platform config) |

Core **may** depend on a thin Gradle API surface where the live code already
does (`Project` name, `Project` as dep edge). It must **not** depend on AGP,
AndroidX, or Dagger.

---

## 2. Package and artifact layout (target end state)

### Maven / plugin coordinates (F-024 final; preview here)

| Artifact | Role |
|----------|------|
| `tools.forma:core` (library, not a settings plugin) | Pure framework types + registry + validators |
| `tools.forma.android` (existing plugin id) | Android DSL + AGP features; depends on core |
| Sibling plugins today (`tools.forma.target`, `.validation`, `.deps`, `.config`, `.owners`) | **Consolidate into core** over F-021–F-023; keep plugin ids as thin facades or deprecate after one minor |

During extraction, monorepo layout suggestion:

```
plugins/
  core/          ← new Gradle subproject (library)
  android/       ← depends on :core; keeps DSL + features
  # target, validation, deps, config, owners shrink → re-export or merge
```

Internal Java/Kotlin packages (proposed):

```
tools.forma.core.target        // TargetType, TargetRef, registry
tools.forma.core.validation    // Validator SPI, name rules, content SPI
tools.forma.core.restriction   // RestrictionGraph / allow-lists
tools.forma.core.deps          // FormaDependency model + apply path (Gradle-facing)
tools.forma.core.config        // SettingsStore + plugin/dep registration maps
tools.forma.core.owners        // Owner metadata (optional module or same jar)
```

Binary compatibility: extraction may keep **typealiases / deprecated type
moves** from old packages (`tools.forma.target.TargetTemplate`, …) for one
release so composite consumers do not break mid-migration.

---

## 3. Core concepts

### 3.1 Target type (`TargetType`)

Today: `tools.forma.target.TargetTemplate(suffix: String)` plus singleton
objects in `AndroidTargets.kt`.

**Public API (conceptual):**

```kotlin
// tools.forma.core.target
interface TargetType {
    /** Stable id for registry and docs, e.g. "android.impl", "jvm.library". */
    val id: String
    /**
     * Gradle project name suffix used by default name matching.
     * e.g. "impl", "compose-widget". Not necessarily unique across platforms
     * until disambiguation lands (see §7).
     */
    val nameSuffix: String
    /** Optional human label for error messages. */
    val displayName: String get() = id
}

/** Lightweight reference to a configured project/target (Gradle adapter). */
interface TargetRef {
    val name: String
    // Platform adapters may expose underlying Project; core only needs name
    // for suffix validation.
}
```

**Migration mapping**

| Today | Core |
|-------|------|
| `TargetTemplate(suffix)` | `TargetType` with `nameSuffix = suffix` |
| `FormaTarget(project)` | `TargetRef` (Gradle impl holds `Project`) |
| `object ImplTargetTemplate` | Registered `TargetType` in Android platform kit |

Platforms own **concrete type instances** and register them; core does not
hard-code `api`/`impl`.

### 3.2 Name matching

Today (`Validator.kt`): project name equals `suffix` **or** ends with
`-$suffix` (case-sensitive).

Core keeps that as the **default** `NameMatcher`:

```kotlin
fun interface NameMatcher {
    fun matches(projectName: String, type: TargetType): Boolean
}

object SuffixNameMatcher : NameMatcher {
    override fun matches(projectName: String, type: TargetType): Boolean {
        val s = type.nameSuffix
        return projectName == s || projectName.endsWith("-$s")
    }
}
```

Registry may attach a custom matcher per type later (Bazel labels, etc.) without
changing restriction tables.

### 3.3 Restriction engine

Today: each DSL calls `validator(AllowedType1, AllowedType2, …)` and
`applyDependencies` runs that validator on every **project** dependency.

**Public API:**

```kotlin
// tools.forma.core.restriction
/** Edge kind for future expansion (implementation / api / test). */
enum class EdgeKind { IMPLEMENTATION, TEST, ANDROID_TEST }

data class RestrictionRule(
    val from: TargetType,
    val allowedDependencies: Set<TargetType>,
    val edgeKinds: Set<EdgeKind> = setOf(EdgeKind.IMPLEMENTATION),
)

interface RestrictionGraph {
    fun allowedTypes(consumer: TargetType, edge: EdgeKind = EdgeKind.IMPLEMENTATION): Set<TargetType>
    fun isAllowed(consumer: TargetType, dependency: TargetType, edge: EdgeKind = EdgeKind.IMPLEMENTATION): Boolean
    fun ruleFor(consumer: TargetType): RestrictionRule?
}

class MutableRestrictionGraph : RestrictionGraph {
    fun allow(from: TargetType, vararg to: TargetType): MutableRestrictionGraph
    fun allow(from: TargetType, to: Collection<TargetType>): MutableRestrictionGraph
    // deny is implicit: anything not allowed fails validation
}
```

**Semantics (match product today):**

- Only **project/target** edges are checked (not external GAV / catalog deps).
- Allow-lists are **closed**: missing edge ⇒ validation error.
- No separate deny-list API in v1 (implicit deny is enough for Android matrix).
- Test / androidTest project deps are **not** suffix-validated in current
  `applyDependencies` — preserve that unless a later ticket tightens it; model
  `EdgeKind` so we can opt in without another rewrite.

Android platform registers the live matrix from
[`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) once at plugin apply /
`androidProjectConfiguration` time (or as a static `AndroidRestrictionKit`).

### 3.4 Validation framework

#### Validator SPI

```kotlin
// tools.forma.core.validation
fun interface TargetValidator {
    fun validate(target: TargetRef)
}

object AcceptAny : TargetValidator {
    override fun validate(target: TargetRef) = Unit
}

/** Factory: allow dependency names matching any of the given types. */
fun dependencyTypeValidator(
    allowed: Collection<TargetType>,
    nameMatcher: NameMatcher = SuffixNameMatcher,
    error: ValidationMessages = DefaultValidationMessages,
): TargetValidator
```

Caching (F-017): identity-cache validators keyed by allowed `TargetType`
instances (same as today’s `ConcurrentHashMap` on `TargetTemplate` lists).
Document as a **required** implementation property of the default factory so
extraction does not regress configuration time.

#### Self-type validation

```kotlin
fun selfTypeValidator(expected: TargetType, nameMatcher: NameMatcher = SuffixNameMatcher): TargetValidator
```

Replaces `target.validate(ImplTargetTemplate)` call sites.

#### Content validation SPI

Today: Gradle-coupled `Project.validateDirectoryContent` + Android helpers
(`disallowResources`, `onlyAllowResources`, `onlyAllowLayouts`).

```kotlin
fun interface ContentRule {
    /** @return null if ok, else human-readable failure. */
    fun check(filesUnderMain: List<String> /* relative names */): String?
}

object NoResourcesUnderMain : ContentRule // reject res/
object OnlyResourcesUnderMain : ContentRule
object OnlyLayoutResources : ContentRule   // only layout* under res — platform helper may specialize paths
```

**Split:** pure predicates + message builders live in core; **filesystem
listing** stays in a Gradle adapter (`ProjectContentInspector`) inside the
platform or a `forma-core-gradle` shim if we need a second artifact. v1 may
keep one `tools.forma.core` jar with an `…gradle` package that references
`org.gradle.api.Project` to avoid a premature multi-jar split — acceptable
because JVM/Bazel adapters will supply their own inspectors.

#### Errors

```kotlin
class FormaValidationException(message: String) : RuntimeException(message)
// Gradle adapter may wrap as ProjectValidationError / BuildException for parity
```

Keep message shape close to today’s strings (allowed suffixes list) so sample
and docs stay recognizable.

### 3.5 Target registry

Central registration API — the main **new** surface relative to today’s
scattered objects.

```kotlin
// tools.forma.core.target
data class TargetRegistration(
    val type: TargetType,
    val allowedDependencies: Set<TargetType>,
    val contentRules: List<ContentRule> = emptyList(),
    val nameMatcher: NameMatcher = SuffixNameMatcher,
    val metadata: Map<String, String> = emptyMap(), // e.g. platform=android
)

interface TargetRegistry {
    fun register(registration: TargetRegistration)
    fun get(id: String): TargetType?
    fun getBySuffix(suffix: String): Collection<TargetType> // may be multi after §7
    fun all(): Collection<TargetRegistration>
    fun restrictionGraph(): RestrictionGraph
    fun validatorFor(consumer: TargetType): TargetValidator
    fun selfValidator(type: TargetType): TargetValidator
}

class DefaultTargetRegistry(
    private val nameMatcher: NameMatcher = SuffixNameMatcher,
) : TargetRegistry { /* … */ }
```

**Platform bootstrap sketch (Android):**

```kotlin
fun TargetRegistry.registerAndroidDefaults() {
    val api = type("android.api", "api")
    val impl = type("android.impl", "impl")
    val library = type("android.library", "library") // see §7 collision
    // …
    register(TargetRegistration(api, allowedDependencies = setOf(api, library), contentRules = listOf(NoResourcesUnderMain)))
    register(TargetRegistration(impl, allowedDependencies = setOf(api, /* … no impl */)))
    // …
}
```

DSL entrypoints become thin:

```kotlin
fun Project.impl(/* platform params */) {
    val reg = FormaCore.registry // or settings-scoped service
    reg.selfValidator(AndroidTypes.Impl).validate(project.asTargetRef())
    // apply AGP features (platform)
    applyDependencies(validator = reg.validatorFor(AndroidTypes.Impl), …)
}
```

### 3.6 Dependency model (portable + Gradle apply)

**Keep in core (mostly as today):**

| Type | Notes |
|------|--------|
| `FormaDependency` sealed hierarchy | `Empty`, `Named`, `Target`, `File`, `Platform`, `Mixed` |
| `DepSpec` / `ConfigurationType` | `Implementation`, `Kapt`, `CustomConfiguration`, … |
| `deps(…)`, `plus`, `forEach` | pure merge helpers (F-017 hot path) |
| Project-edge validation hook | call `TargetValidator` before adding project dep |

**Gradle-specific apply** (`applyDependencies` repositories block, plugin
auto-apply from `FormaSettingsStore`) lives in a `tools.forma.core.deps.gradle`
package or stays in `:deps` module that depends on `:core` types — either is
fine for F-021 as long as **restriction checks** call into core.

**Catalog generators** (`library()`, `bundle()`, `plugin()`, GAV parse) are
tooling: prefer `tools.forma.deps.catalog` remaining adjacent; not required
inside the minimal core jar for JVM/Bazel.

### 3.7 Settings / configuration store

Today: `SettingsStore<T>`, `PluginInfoStore`, `FormaSettingsStore` +
`AndroidProjectSettings`.

**Core:**

```kotlin
interface SettingsStore<T : Any> {
    val settings: T
    fun store(configuration: T)
}

interface PluginBindingStore {
    // registerConfiguration / registerPlugin / registerDependency / pluginFor
    // (same behavior as PluginInfoStore today)
}
```

**Platform:** `AndroidProjectSettings` (SDK, AGP, Compose flags, …) remains
Android-owned. Optional `FormaPlatformSettings` marker interface if useful.

Singleton object `Forma` / `FormaSettingsStore` can thin-delegate to core stores
after extraction.

### 3.8 Owners

`Owner` / `Person` / `Team` / `NoOwner` are platform-agnostic metadata. Include
in `tools.forma.core.owners` (or same jar). No behavior change. Wiring
“mandatory owners” flag stays in platform settings.

### 3.9 Feature definitions

**Not in core.** `FeatureDefinition`, `applyFeatures`, AGP library/binary/native
stay in `:android`. Future JVM plugin defines its own feature appliers.

---

## 4. Runtime collaboration (sequence)

```
settings plugin / androidProjectConfiguration
    → store AndroidProjectSettings
    → TargetRegistry.registerAndroidDefaults()  // once

subproject build.gradle.kts: impl(...)
    → selfValidator(impl).validate(this)
    → ContentRule checks (disallow/allow res)
    → apply platform FeatureDefinitions (AGP, Kotlin, …)
    → applyDependencies:
         for each project dep:
            registry.validatorFor(impl).validate(dep)
            add to Gradle configuration
         for each named dep:
            optional plugin side-effect from PluginBindingStore
```

Bazel adapter (later): same registry + restriction graph; replace
`applyDependencies` with BUILD rule emission / check (F-040/F-041).

---

## 5. What each extraction ticket implements

| Ticket | Scope |
|--------|--------|
| **F-020** (this doc) | Public API design only |
| **F-021** | Create `plugins/core` (or move packages); target types + **restriction graph** + wire Android matrix data; keep binary/API facades so sample builds |
| **F-022** | **Done:** validation SPI, identity-cached factories, content predicates in `plugins/core`; Android helpers call core; `:validation` facade |
| **F-023** | Android DSL uses registry; delete duplicated allow-lists from individual `*.kt` entrypoints where safe; sample green |
| **F-024** | Publishing coordinates, README/Portal metadata, deprecate old plugin jars if merged |

Do not skip to JVM targets (F-030) until F-023 is done.

---

## 6. Public API surface checklist (v1 freeze)

**Must ship in forma-core v1**

- [x] `TargetType`, `TargetRef`, `NameMatcher` / `SuffixNameMatcher` (F-021)
- [ ] `TargetRegistry` + `TargetRegistration` + `DefaultTargetRegistry` (F-023)
- [x] `RestrictionGraph` / `RestrictionRule` / `EdgeKind` (F-021)
- [x] `TargetValidator`, `AcceptAny`, `dependencyTypeValidator`, `selfTypeValidator` (F-022)
- [x] Content rule interfaces + `NoResourcesUnderMain` / `OnlyResourcesUnderMain` / `OnlyLayoutResources` (F-022)
- [x] Validation error type + message helpers (suffix lists) — core `FormaValidationException`; facade keeps `ProjectValidationError` (F-022)
- [ ] `SettingsStore`, `PluginBindingStore` (from today’s config interfaces)
- [ ] Dependency model types needed for project-edge validation
- [x] Identity-cached validator factory (F-017 behavior) (F-022)

**Explicitly deferred**

- Multi-suffix disambiguation API beyond `id` (design in §7; implement when registry lands)
- Test-edge project validation
- Bazel label matchers
- Stable Kotlin binary ABI guarantees / explicit Careful API annotations
- Version catalog DSL

---

## 7. Design decision: `library` suffix collision

**Problem:** JVM `library { }` and Android `androidLibrary { }` both use
`LibraryTargetTemplate` / suffix `library`. Name validation and dependency
allow-lists **cannot distinguish** them today
([`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) collision note).

**Decision for forma-core:**

1. Every `TargetType` has a **unique `id`** (`jvm.library` vs `android.library`)
   even when `nameSuffix` is shared.
2. Restriction rules key off **`TargetType` identity / id**, not suffix alone.
3. **Default name matching remains suffix-based** for Gradle project names so
   existing samples keep working without renames.
4. When resolving “what type is this dependency project?”, registry uses:
   - optional **explicit type marker** on the project (future: extra property /
     convention plugin sets `forma.target.id`), else
   - **suffix → types** lookup; if multiple types share a suffix, treat them as
     **interchangeable for allow-list checks** if *any* registered type with
     that suffix is allowed (preserves today’s behavior), and document the
     ambiguity.
5. Optional later hardening (not F-020): rename Android library suffix to
   `android-library` or require path conventions — product change, needs its
   own ticket after core lands.

This unblocks registry design without forcing a breaking sample rename in
F-021.

---

## 8. Compatibility and migration strategy

1. **Additive first:** introduce `:core` and implement interfaces by delegating
   to existing classes (or move files and leave typealiases).
2. **Single composite consumer:** `application/` via `includeBuild("../plugins")`
   must stay green every PR.
3. **No force-push / no master promotion** from workers; PRs target `v2`.
4. **Docs:** update ARCHITECTURE §6, DEPENDENCY-MATRIX “how validation works”,
   README Progress when code lands (F-021+), not only this design file.
5. **Performance:** re-run configuration timing notes from
   [`CONFIGURATION-PERFORMANCE.md`](CONFIGURATION-PERFORMANCE.md) after
   validator move; keep identity caches.

---

## 9. Example: registering part of the Android matrix

Illustrative only — full table remains [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md).

```kotlin
val api = TargetType.simple("android.api", "api")
val impl = TargetType.simple("android.impl", "impl")
val library = TargetType.simple("android.library", "library")
val util = TargetType.simple("jvm.util", "util")
// …

registry.register(
    TargetRegistration(
        type = api,
        allowedDependencies = setOf(api, library),
        contentRules = listOf(NoResourcesUnderMain),
    )
)
registry.register(
    TargetRegistration(
        type = impl,
        allowedDependencies = setOf(
            api, /* android-util, test-util, util, library, ui-library,
                   res, viewbinding, widget, compose-widget — no impl */
        ),
    )
)
```

Composition roots (`androidApp`, `androidBinary`) register the widest allow-lists
including `impl` / `app` as they do today.

---

## 10. Testing strategy for core

| Layer | Where | What |
|-------|-------|------|
| Pure unit | `plugins/core` tests (no AGP) | name matcher, restriction allow/deny, registry validator cache identity |
| Plugin integration | existing `plugins/` build | Android DSL still configures |
| Product | `application/` `./gradlew build` | gold-standard sample |

Prefer pure unit tests for restriction tables so JVM/Bazel work does not need
an Android SDK.

---

## 11. Open questions (resolve during F-021, not blockers for design accept)

1. Single jar `tools.forma:core` vs `core` + `core-gradle` split in the first
   extraction PR — **default: single jar** with gradle package.
2. Whether `:owners` merges into core jar or stays a one-class sibling — **default: merge**.
3. Exact deprecation timeline for `tools.forma.target` / `.validation` plugin
   ids on Portal — decide in F-024 with publish docs.
4. Whether `EdgeKind.TEST` project validation should turn on with a flag —
   default **off** (parity).

---

## 12. References (code truth)

| Area | Path |
|------|------|
| Target templates | `plugins/android/.../AndroidTargets.kt` |
| TargetTemplate / FormaTarget | `plugins/target/...` |
| Validators | `plugins/validation/.../Validator.kt` |
| Content checks | `plugins/validation/.../ContentValidator.kt`, `plugins/android/.../commonValidators.kt` |
| Dep model + apply | `plugins/deps/.../FormaDependency.kt`, `applyDependencies.kt`, `dependencies.kt` |
| Settings | `plugins/config/.../AndroidProjectSettings.kt` |
| Matrix | `docs/DEPENDENCY-MATRIX.md` |
| Extraction map | `docs/ARCHITECTURE.md` §6 |
| Vision | `docs/VISION.md` |

---

## 13. Acceptance criteria for F-020

- [x] This document exists at `docs/forma-core-api.md`.
- [x] Covers types, restriction engine, validation SPI, target registry, deps/settings split, coordinates preview, library suffix decision, ticket sequencing.
- [x] Linked from ARCHITECTURE / README / TICKETS (this PR).
- [ ] No requirement to move code in F-020 (code starts F-021).
