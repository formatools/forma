# Kotlin Multiplatform (KMP) targets — design (F-105)

**Status:** design accepted (F-105). Implementation: **F-106…F-110** (see `TICKETS.md` § P11).

**North star:** KMP is a **third platform adapter** on forma-core (after Android + pure JVM), not a free-form
`kotlin { targets { … } }` escape hatch. Call sites stay **Bazel-like**: type + attributes. The
**target type (rule)** owns `org.jetbrains.kotlin.multiplatform` and the platform set. Platforms are
chosen **once** for the project (or once on a derived type) — never re-selected per module.

**Related:** [`VISION.md`](VISION.md) · [`forma-core-api.md`](forma-core-api.md) · [`JVM-TARGETS.md`](JVM-TARGETS.md) ·
[`TARGET-PLUGINS.md`](TARGET-PLUGINS.md) · [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) ·
[`ARCHITECTURE.md`](ARCHITECTURE.md)

Toolchain baseline at design time: **Gradle 9.6.x / Kotlin 2.3.x / AGP 9.3.x / JVM 11** (see F-019 / F-087).

---

## 1. Goals and non-goals

### Goals

1. Let teams share **common Kotlin** across **JVM + Android** (v1) with the same Forma discipline:
   role-typed modules, closed project-dep matrix, attributes-only call sites.
2. Ship a **portable platform plugin** `tools.forma.kmp` that depends on forma-core + deps/validation
   facades — parallel to `tools.forma.jvm`, not a fork of the restriction engine.
3. Make Android and pure-JVM composition roots **first-class consumers** of KMP libraries
   (`android.impl` / `androidApp` / `androidBinary` / `jvm.impl` / `jvm.binary` → `kmp.library`).
4. Keep **one global way** to enable platforms and apply the multiplatform plugin (type-owned +
   project-global configuration).
5. Teach via a **progressive example** + agent skill (F-050 ladder style), without rewriting the gold
   sample app to KMP on day one.

### Non-goals (v1)

| Out of scope for F-105…F-110 | Why / later |
|------------------------------|-------------|
| iOS / watchOS / tvOS / macOS native targets | Host + CI tooling; separate phase once JVM+Android green |
| JS / Wasm / native linux/mingw | Same |
| Replacing Android `api`/`impl` with KMP everywhere | Android product stays single-platform first |
| Hierarchical multi-module “one KMP root owns all features” | Fights flat graph; one Gradle project = one Forma target |
| Call-site `kotlin { androidTarget(); ios() }` | Plugin shopping; rejected |
| Restoring `androidLibrary` as “shared” bucket | Flat-structure axiom (F-063) |
| Full cocoapods / XCFramework publish pipeline | Follow-up after iOS targets |
| Changing forma-core restriction SPI for source sets | Project edges stay suffix/type based; source-set wiring is platform apply |

---

## 2. Problem

Today Forma has two platform plugins:

| Plugin | Module shape | Kotlin plugin |
|--------|--------------|---------------|
| `tools.forma.android` | One role per Gradle project; AGP library/app | AGP 9 built-in Kotlin / `kotlin-jvm` for pure JVM rows |
| `tools.forma.jvm` | One role per Gradle project | `org.jetbrains.kotlin.jvm` |

There is **no** first-class way to declare a shared multiplatform module. Teams would fall back to
raw Gradle KMP — the opposite of Forma’s meta-build pitch (structure declaration, type-owned tools,
closed matrix).

Raw KMP also encourages **fat modules** (many source sets, many targets, optional hierarchical
projects) and **per-module target shopping**. Forma needs the opposite: small role-typed targets and
fleet-global platform policy.

---

## 3. Design principle

> **The KMP target type owns the multiplatform plugin and the platform set.**
> Call sites set package + deps (+ rare platform-specific dep attrs). Platforms are not re-listed
> on every module.

| Layer | Owns | Analogy |
|-------|------|---------|
| **Project configuration** | Which platforms exist for this product (v1: jvm + android) | Workspace toolchain / `.bazelrc` |
| **Type / rule** | `kotlin-multiplatform` + default source-set graph + matrix row | `rule()` in `.bzl` |
| **Call site** | `packageName`, `dependencies`, optional `androidDependencies` / `jvmDependencies` | `BUILD` attributes |

### Rejected shapes

```kotlin
// REJECTED — free-form KMP DSL at every module
plugins { kotlin("multiplatform"); id("com.android.library") }
kotlin {
    androidTarget()
    jvm()
    iosArm64() // shopping list
}

// REJECTED — plugin id / target list as call-site attrs
kmpLibrary(..., platforms = listOf("ios", "jvm"), plugins = …)

// REJECTED — generic shared android library escape hatch
androidLibrary(...) // removed F-063; do not revive for “common”

// REJECTED — dual happy path: raw KMP modules next to Forma KMP types without matrix
```

---

## 4. Platform product shape (v1)

### 4.1 New plugin module

```
plugins/
  kmp/                    ← new Gradle subproject
    tools.forma.kmp       ← Settings plugin id (empty apply, Portal pattern)
    depends on: :core :deps :validation :target :owners
                + Kotlin Multiplatform Gradle plugin (embeddedKotlin / kotlin-gradle-plugin)
                + AGP compile-only or api as needed for androidTarget wiring
```

**Do not** put KMP apply logic into `:android` or `:jvm` as a permanent home. Cross-links:

- `:android` / `:jvm` registries **allow** edges **to** KMP types (F-108).
- `:kmp` owns types, registry, DSL, feature applicators.

Optional later: thin re-exports so `import tools.forma.android.kmpLibrary` works — **not** v1;
consumers use `tools.forma.kmp.*` or a single umbrella later.

### 4.2 Target types (v1)

Mirror the pure-JVM kit roles that make sense for **shared** code. Suffixes are **KMP-prefixed** so
they never collide with `api` / `impl` / `library` on Android or JVM (critical: name matching is
suffix-based).

| DSL (`tools.forma.kmp`) | Type id | Name suffix | Role |
|--------------------------|---------|-------------|------|
| `kmpLibrary` | `kmp.library` | `kmp-library` | Shared multiplatform library (default workhorse) |
| `kmpApi` | `kmp.api` | `kmp-api` | Shared public contracts (expect/actual-friendly) |
| `kmpUtil` | `kmp.util` | `kmp-util` | Shared utilities / extensions |
| `kmpTestUtil` | `kmp.test-util` | `kmp-test-util` | Shared test helpers (commonTest + platform tests) |

**No `kmpImpl` / `kmpBinary` in v1.**

- Feature **implementation** stays on Android `impl` / JVM `impl` (platform UI, DI graphs, apps).
- Composition roots stay `androidBinary` / `androidApp` / `jvm.binary`.
- Putting `impl` on KMP would blur “where composition happens” and invite `impl`→`impl` via common.

**Why not reuse suffix `library`?** Android/JVM already use `library` for pure JVM
(`jvm.library`). A project named `shared-library` would match the wrong type. Explicit
`…-kmp-library` keeps validators unambiguous.

### 4.3 Platforms enabled (v1)

| Platform | Kotlin target | Android wiring |
|----------|---------------|----------------|
| **JVM** | `jvm()` with `compilerOptions.jvmTarget` from settings (default `"11"`) | n/a |
| **Android** | Target created by AGP KMP library plugin (extension on `kotlin { }`) | **Shipped (F-107 spike):** `com.android.kotlin.multiplatform.library` |

#### Android plugin id — F-107 spike result (AGP 9.3.0 / Kotlin 2.3.21)

| Choice | Value |
|--------|--------|
| **Plugin id (apply)** | `com.android.kotlin.multiplatform.library` |
| **Implementation class** | `com.android.build.gradle.api.KotlinMultiplatformAndroidPlugin` |
| **Classpath coordinate** | `com.android.tools.build:gradle:<agpVersion>` (same as `androidProjectConfiguration`) |
| **Confirmed in** | AGP 9.3.0 jar `META-INF/gradle-plugins/com.android.kotlin.multiplatform.library.properties` |
| **Classic fallback** | **Not used.** `com.android.library` + `androidTarget()` is rejected for the happy path. |
| **Extension on `kotlin { }`** | Prefer name `android`, then `androidLibrary` (AGP registers both eras; Forma configures via extension lookup + reflective `namespace` / `compileSdk` / `minSdk`) |
| **SDK source of truth** | `androidProjectConfiguration` → `AndroidProjectSettings` (minSdk / compileSdk). Android platform without Android settings → fail-fast at KMP DSL apply. |
| **`:kmp` → `:android`** | **Forbidden.** Applicator uses string plugin ids + reflection; `compileOnly` AGP only. |

**Single project-global default** (both on):

```kotlin
// Root buildscript — ScriptHandlerScope (parallel to androidProjectConfiguration)
buildscript {
    androidProjectConfiguration(project = rootProject, agpVersion = "9.3.0", /* … */)
    kmpProjectConfiguration(
        project = rootProject,
        // Platforms are NOT optional per module in the happy path:
        platforms = KmpPlatforms(jvm = true, android = true), // defaults
        jvmTarget = "11",
    )
}
```

**One global way:** every built-in KMP type receives the same platform set from
`kmpProjectConfiguration` (or defaults if configuration is implicit on first DSL use — parity with
`registerJvmDefaults()`). Pure KMP+JVM trees: `platforms = KmpPlatforms(jvm = true, android = false)`.

**Path B later (not v1):** `deriveTargetType` + `iosSharedLibrary` that adds iOS platforms on a
**derived type** only — still no per-call-site target list.

### 4.4 Source layout (convention)

Forma fleet layout today assumes `src/main/{java,kotlin}/…` ([`SourceLanguage`](../plugins/core/src/main/java/tools/forma/core/fleet/SourceLanguage.kt)).
KMP needs multiplatform source sets:

```
shared-kmp-library/
  build.gradle.kts          # kmpLibrary(packageName = "com.example.shared", …)
  src/
    commonMain/kotlin/com/example/shared/…
    commonTest/kotlin/…
    androidMain/kotlin/…    # optional actuals
    jvmMain/kotlin/…        # optional actuals
```

**packageName** still drives fleet check/generate for the **commonMain** tree (and documents the
root package). F-107/F-109 extend fleet helpers or add `KmpLayout` so `formaLayoutCheck` understands
`commonMain` (do not force `src/main/kotlin` on KMP modules).

Content rules (v1):

- `kmp.*` modules: **no Android `res/`** under common (resources stay Android `androidRes` / platform UI).
- Platform-specific Android resources inside a KMP module are **discouraged** in v1 (prefer
  `androidRes` + consume KMP for code). If AGP KMP library requires a minimal manifest, keep it
  empty/skeleton owned by the feature applicator — not a call-site concern.

---

## 5. Call-site API (attributes only)

```kotlin
// shared-kmp-library/build.gradle.kts
import tools.forma.kmp.kmpLibrary
import tools.forma.deps.core.deps

kmpLibrary(
    packageName = "com.example.shared",
    dependencies = deps(
        // → commonMain; project deps must be other kmp.* (matrix) or external KMP-friendly GAVs
        target(project(":core-kmp-api")),
        libs.some.multiplatform.lib,
    ),
    // Optional platform-only deps (attrs, not plugin shopping) — implement if spike needs them:
    // androidDependencies = deps(…),  // androidMain
    // jvmDependencies = deps(…),      // jvmMain
    testDependencies = deps(/* commonTest */),
)
```

Same shape for `kmpApi` / `kmpUtil` / `kmpTestUtil` (stricter matrices).

**Unit return** — no builder chains (F-081).

**Target plugins:** KMP types participate in Path A/B (`registerTargetPlugin` /
`deriveTargetType`) once F-106 registry exists — e.g. a future `serializationKmpLibrary` type owns
`kotlinx-serialization` plugin. v1 ships registry hooks; no serialization example required.

---

## 6. Dependency matrix

### 6.1 KMP → KMP (project edges)

| Consumer ↓ \ Dep → | kmp-api | kmp-library | kmp-util | kmp-test-util |
|--------------------|---------|-------------|----------|---------------|
| **kmp-api** | Y | — | — | — |
| **kmp-library** | Y | Y* | Y | Y (test) |
| **kmp-util** | — | — | Y | — |
| **kmp-test-util** | Y | Y | Y | Y |

\* `kmp-library` → `kmp-library` allowed for layered shared stacks (unlike `impl`↛`impl`). Keep
shallow; composition of **features** still happens at Android/JVM roots.

**Rules of thumb**

- **No KMP → Android `impl` / `uiLibrary` / `res` / `widget`.** Shared code must not pull UI/graph
  leaves.
- **No KMP → pure `jvm.impl`.** Same directionality.
- KMP **may** depend on **external** multiplatform libraries (catalog / GAV) — not suffix-validated.
- Whether KMP may depend on pure `jvm.library` / `jvm.util`: **v1 = no** (those modules are
  Kotlin-JVM plugin artifacts; consuming them from `commonMain` is incorrect). Shared code that is
  truly common should be `kmp-*`. JVM-only helpers stay on the JVM side and depend **on** KMP if
  needed.

### 6.2 Android / JVM → KMP (consumer edges)

Extend existing registries (F-108):

| Consumer | May depend on KMP |
|----------|-------------------|
| `android.api` | `kmp.api` (contracts only; optional `kmp.library` **no** — keep api thin) |
| `android.impl` | `kmp.api`, `kmp.library`, `kmp.util` |
| `android.android-util` | `kmp.library`, `kmp.util` |
| `android.app` / `android.binary` | `kmp.api`, `kmp.library`, `kmp.util` |
| `jvm.api` | `kmp.api` |
| `jvm.impl` / `jvm.binary` | `kmp.api`, `kmp.library`, `kmp.util` |
| `jvm.library` / `jvm.util` | `kmp.library`, `kmp.util` (optional; prefer depending upward only if needed) |

Update [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) **when code lands** (code truth rule).

### 6.3 How Gradle edges resolve

Project dependency from Android `impl` → `shared-kmp-library` must resolve to the **Android**
variant/outgoing configuration of the KMP project (KMP plugin metadata). Feature applicator (F-107)
must enable the Android target + publishing/consumable configuration that AGP/Kotlin expect so
`implementation(project(":shared-kmp-library"))` works without manual `attributes` at call sites.

`applyDependencies` today maps `Implementation` → `implementation` configuration names. KMP common
deps need `commonMainImplementation` (or the Kotlin dependency handler API). F-107 either:

1. Uses `CustomConfiguration("commonMainImplementation")` / platform customs for KMP DSL only, or
2. Adds a small `applyKmpDependencies` that uses `kotlin.sourceSets.commonMain.dependencies { … }`
   while still running the **same** project-dep validators.

Prefer (2) for correctness with the KMP plugin; keep validation on project deps identical to
Android/JVM.

---

## 7. Configuration surface

### 7.1 `kmpProjectConfiguration` (new)

Single entrypoint (F-082 spirit), `ScriptHandlerScope` or root `Project` — match whatever pattern
`:jvm` grows into; Android keeps `androidProjectConfiguration` for AGP/SDK.

Responsibilities:

- Register `KmpTargetRegistry` defaults
- Store `KmpPlatforms` + `jvmTarget` in a small `KmpProjectSettings` / store
- Ensure Kotlin MPP (+ Android KMP library) plugins are on the **buildscript/classpath** the same
  way Android puts AGP on classpath (`extraPlugins` / version alignment with
  `Forma.settings.kotlinVersion` when Android plugin also applied)

**Classpath rule:** consumers still do not apply plugins in modules. Root puts Kotlin MPP on
classpath once (document exact coordinates in F-107 + `PLUGIN-PUBLISH` / getting started).

### 7.2 Interaction with Android project configuration

When both Android and KMP plugins are used (expected for mobile monorepos):

| Concern | Source of truth |
|---------|-----------------|
| `minSdk` / `compileSdk` / AGP version | `androidProjectConfiguration` |
| Kotlin version | Android settings today; KMP must **not** introduce a second pin |
| JVM target | Align with `javaVersionCompatibility` (11) |
| KMP platforms | `kmpProjectConfiguration` only |

Pure KMP+JVM tree without Android: allow KMP-only config with jvm platform; android platform
requires Android settings present or fails fast with a clear error.

---

## 8. Implementation slices (ticket map)

| ID | Deliverable | Verify |
|----|-------------|--------|
| **F-105** | This design doc + board + VISION/ARCHITECTURE pointers | Review / merge |
| **F-106** | `:kmp` module skeleton, `KmpTargetTypes`, `KmpTargetRegistry` + unit matrix tests, empty Settings plugin, publish metadata | `plugins/` `:kmp:test` + root plugins build |
| **F-107** | Feature applicator: apply KMP (+ Android KMP lib), jvm+android targets, source sets, `kmpLibrary` DSL + deps apply path | Unit tests + compile; minimal smoke module if feasible |
| **F-108** | Extend Android + JVM restriction matrices to allow KMP deps; docs matrix | Registry unit tests; sample edge compile |
| **F-109** | Progressive example `examples/kmp/01-shared-library` (shared kmp-library + jvm binary and/or tiny android binary consumer) | Documented `./gradlew` tasks green on host |
| **F-110** | User docs (`KMP-GETTING-STARTED` or section), agent skill, PROGRESSIVE-EXAMPLES ladder, README links | Doc-only + example already green |

Do **not** merge F-107 without a written spike note in PROGRESS if the Android KMP library plugin
API differs from this doc — update §4.3 in the same PR.

---

## 9. Consumer sketch (end state)

```kotlin
// settings.gradle.kts
plugins {
    id("tools.forma.android") version "0.1.x"
    id("tools.forma.kmp") version "0.1.x"
    id("tools.forma.includer")
}

// root build.gradle.kts (buildscript)
androidProjectConfiguration(project = rootProject, /* existing */)
kmpProjectConfiguration(project = rootProject) // defaults: jvm + android

// shared-kmp-library/build.gradle.kts
kmpLibrary(
    packageName = "com.example.shared",
    dependencies = deps(/* common */),
)

// feature-hello-impl/build.gradle.kts  (Android)
impl(
    packageName = "com.example.hello.impl",
    dependencies = deps(
        target(project(":shared-kmp-library")),
        target(project(":feature-hello-api")),
    ),
)
```

Directory naming (includer flat names):

```
shared-kmp-library/          # suffix kmp-library
core-kmp-api/                # suffix kmp-api
feature/hello/impl/          # existing android impl
```

---

## 10. Risks and open decisions

| Risk | Mitigation |
|------|------------|
| AGP 9 × Kotlin 2.3 Android KMP library plugin API churn | Spike in F-107 first commit; pin exact plugin ids in PROGRESS |
| `applyDependencies` configuration names wrong for MPP | Prefer Kotlin source set dependency API for KMP DSL |
| Suffix explosion (`kmp-library` long names) | Accept clarity over brevity; includer already uses flat names |
| Dual registries (Android vs KMP) and shared consumers | F-108 explicit allow-lists; never merge registries into one singleton |
| CI without Android SDK for pure KMP tests | Keep `:kmp` unit tests pure; example with Android stays in `examples/kmp` job or application job |
| Expect/actual overuse | Docs: prefer common expect-free design; actuals only at platform edges |

**Open / resolved:**

1. ~~Exact Android plugin id for KMP library under AGP 9.3~~ → **Resolved F-107:** `com.android.kotlin.multiplatform.library` (see §4.3).
2. Whether `androidDependencies` / `jvmDependencies` attrs ship in F-107 or wait until a consumer needs them → **Deferred** (KDoc on `kmpLibrary`; commonMain/commonTest only in v1).
3. CI matrix row for `examples/kmp` (extend main workflow vs document manual-only until F-109).

---

## 11. Principles checklist

| Principle | How KMP honors it |
|-----------|-------------------|
| Bazel-like rules | Type owns MPP plugin + platforms |
| One global way | `kmpProjectConfiguration` platforms; no per-module target lists |
| Explicit structure + tooling | Suffix types + matrix; fleet layout for commonMain |
| Flat role-typed graph | `kmp-api` / `kmp-library` / `kmp-util`; no generic shared bucket |
| Composition at roots | Apps/binaries on Android/JVM; no kmpBinary v1 |
| Closed matrix | §6; code truth in registries + DEPENDENCY-MATRIX |
| Portable core | Types/registry only; apply in `:kmp` |
| No plugin shopping | Rejected shapes §3 |

---

## 12. See also

- Implementation plan (Hermes): `.hermes/plans/*-kmp-multiplatform.md` (workspace)
- JVM precedent: [`JVM-TARGETS.md`](JVM-TARGETS.md), `plugins/jvm/`
- Target plugins: [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md)
- Tickets: `TICKETS.md` § P11
