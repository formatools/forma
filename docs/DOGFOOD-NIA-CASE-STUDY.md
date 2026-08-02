# Case study: Now in Android → Forma (F-115 Phase D)

**Date:** 2026-08-02  
**Ticket:** F-115  
**Upstream:** [android/nowinandroid](https://github.com/android/nowinandroid) @ `7d45eae` (local pin)  
**Forma consumer:** `/Users/claw/work/nowinandroid-forma/forma-spike`  
**Forma plugins:** `mavenLocal` **`0.1.3-NIA`** (external — **no** `includeBuild` of Forma plugins, **no** NiA `build-logic`)  
**Living design / findings:** [`DOGFOOD-NIA.md`](DOGFOOD-NIA.md)

This write-up answers: *does Forma’s meta-build model hold on a real multi-module Google reference app, and what does the call-site surface look like compared with convention-plugin Gradle?*

---

## 1. Executive summary

| Claim | Evidence |
|-------|----------|
| **Structure over configuration works at NiA scale** | 36 role-typed modules + binary; every module is one DSL entry (`hiltImpl`, `roomAndroidUtil`, `uiLibrary`, …). Zero raw `android { }` blocks and zero `.withPlugin` in the spike. |
| **Convention soup collapses to types** | NiA **15** convention plugin classes + **~1.9k LOC** `build-logic` Kotlin → spike **5** thin Path A/B files (**401 LOC**) in local `forma-defs` + project-global classpath. |
| **Module build files shrink hard** | All module `build.gradle.kts` **1476 → 600 LOC** (~**59%** fewer lines), same order of module count (36 upstream product modules vs 36 spike targets + root build). |
| **Closed matrix forced better boundaries** | Findings F1–F3, F5, F8, F19, F29, F31 closed *by re-slicing* (nav ports, res modules, domain-facing features, `composeWidget` DTOs) — not by weakening `impl` ↛ `impl` or restoring `androidLibrary`. |
| **Type-owned plugins scale** | Hilt, Room, Firebase, Protobuf, flavors are type/rule-owned; call sites pass attrs (`packageName`, deps, `productFlavors`, versions). |
| **External consumer path is real** | `:binary:assembleDemoDebug` + `:binary:assembleProdDebug` **BUILD SUCCESSFUL** (945 tasks); APKs ~22 MB each (`0.17.0-nia-forma-library-flavors`). |

**Not claimed:** full NiA feature/test/tooling parity (Roborazzi, baseline profiles, dependency-guard, managed devices, Spotless, Retrofit prod network, screenshot tests). Phase B–C deliberately scoped a vertical product graph that exercises the meta-build axioms.

**Upstream PR:** **No** — dogfood stays on the fork unless Google asks.

---

## 2. Method

1. **Phase A** — inventory upstream modules, convention plugins, project edges; map roles → Forma types; record matrix stress findings (`DOGFOOD-NIA.md`).
2. **Phase B** — external spike from empty tree; publish Forma `0.1.3-NIA` to mavenLocal; attrs-only vertical slice (`assembleDebug`).
3. **Phase C** — expand cores/features/plugins/flavors against the live matrix; engine gaps fixed only when dogfood proved them (KSP companions, `androidUtilTarget` / `libraryTarget`, library product flavors, …).
4. **Phase D** — this document: LOC / plugin / call-site metrics from the pinned trees on the worker host (2026-08-02).

Commands used for metrics (re-runnable):

```bash
# Module build scripts (exclude build-logic / build dirs)
find "$UP" -name 'build.gradle.kts' -not -path '*/build/*' -not -path '*/build-logic/*' \
  -not -path '*/.gradle/*' | xargs wc -l | tail -1
find "$SP" -name 'build.gradle.kts' -not -path '*/build/*' -not -path '*/.gradle/*' \
  | xargs wc -l | tail -1

# Convention vs forma-defs
find "$UP/build-logic" -name '*.kt' -not -path '*/build/*' | xargs wc -l | tail -1
find "$SP/forma-defs" -name '*.kt' | xargs wc -l | tail -1
```

`$UP` = `/Users/claw/work/nowinandroid`, `$SP` = `/Users/claw/work/nowinandroid-forma/forma-spike`.

---

## 3. Before / after numbers

### 3.1 Graph size

| Metric | Upstream NiA | Forma spike |
|--------|--------------|-------------|
| Product `build.gradle.kts` files | **36** | **37** (36 targets + root `build.gradle.kts`; +`forma-defs` composite separate) |
| Settings LOC | 91 | 105 (flat `include` recipe — finding **F9**) |
| Gradle wrapper | 9.4.0 | 9.6.1 (Forma consumer pin) |
| Project-edge refs | ~106 `projects.*` | ~111 `target(` (explicit; no convention-injected hidden edges) |

Module counts are intentionally comparable: spike covers the main product graph (6 features × api/res/impl, cores, sync, root, binary), not benchmarks/lint/catalog apps.

### 3.2 Configuration surface (LOC)

| Surface | Upstream | Spike | Δ |
|---------|----------|-------|---|
| All module `build.gradle.kts` | **1476** | **600** | **−59%** |
| Org build logic (`build-logic` `*.kt` / `forma-defs` `*.kt`) | **1917** (28 files, 15 `*ConventionPlugin*`) | **401** (5 Kotlin files) | **−79%** |
| APK root module | `app` **152** LOC | `binary` **45** LOC | **−70%** |
| Feature For You impl | **43** LOC | **32** LOC | still smaller; spike lists deps explicitly (no convention force-deps) |
| `core:data` | **41** LOC | **17** LOC | **−59%** |
| `core:database` | **36** LOC (4 plugin aliases + `android {}`) | **11** LOC (`roomAndroidUtil` only) | **−69%** |
| `core:designsystem` | **48** LOC | **26** LOC | **−46%** |
| `core:analytics` | **31** LOC | **20** LOC (+ flavor source sets outside this file) | **−35%** |

Notes:

- Upstream LOC includes AOSP license headers (~15–20 lines/file). Even stripping those, module scripts remain longer because each file still has `plugins { }`, `android { namespace = … }`, and often test/tooling blocks.
- Spike **never** opens `plugins { }` or `android { }` in module scripts — **0** raw `android {` blocks, **0** `.withPlugin` occurrences under the spike tree.
- Spike **27** single-method target declarations across module builds (one role entry per module). Upstream **~113** `alias(`/`id(` plugin-apply lines across product modules alone (before counting what each convention applies internally).

### 3.3 What replaced `build-logic`

| NiA convention theme | Forma home in spike |
|----------------------|---------------------|
| `android.library` / feature api/impl / compose | Role types: `api`, `hiltImpl`, `androidRes`, `uiLibrary`, `composeWidget`, `hiltAndroidUtil`, JVM `library` |
| `hilt` | Path A `forma-defs/HiltTargets.kt` → `hiltImpl` / `hiltApp` / `hiltAndroidUtil` / `hiltBinary` |
| `android.room` | Path B `RoomAndroidUtil.kt` → `roomAndroidUtil` (re-registers Hilt — **F14**) |
| `android.application.firebase` | Path A `FirebaseBinary.kt` → `hiltFirebaseBinary` (stacked registers — **F16**) |
| flavors demo/prod | Shared `FormaProductFlavor` on binary **and** libraries (**F7/F27**); `NiaFlavors.kt` list helper |
| protobuf / datastore-proto | Path B `ProtobufLibrary.kt` → `protobufLibrary` + `libraryTarget` |
| jacoco / roborazzi / baseline / spotless / oss / dependency-guard | **Out of scope** (non-structure tooling) |

Classpath plugins live **once** in settings/`androidProjectConfiguration(extraPlugins=…)` — never as per-module identity.

### 3.4 Verify bar (host, 2026-08-01 cascade tip)

```text
forma-spike$ ./gradlew :binary:assembleDemoDebug :binary:assembleProdDebug
BUILD SUCCESSFUL in … (945 tasks)
APK demo ~21.8 MB  binary/build/outputs/apk/demo/debug/binary-demo-debug.apk
APK prod ~21.9 MB  binary/build/outputs/apk/prod/debug/binary-prod-debug.apk
versionName 0.17.0-nia-forma-library-flavors
```

Plugins published: `bash scripts/publish-local.sh 0.1.3-NIA` from the Forma repo.

### 3.5 Configuration-time performance

No same-machine cold `--profile` pair was locked for upstream NiA vs spike in this Phase D pass (different wrappers, SDKs, and composite `forma-defs` confound absolute seconds).

**Recipe** (from [`CONFIGURATION-PERFORMANCE.md`](CONFIGURATION-PERFORMANCE.md)) if a future worker wants a pair:

```bash
cd /Users/claw/work/nowinandroid-forma/forma-spike
./gradlew --stop
rm -rf .gradle/configuration-cache
./gradlew help --no-configuration-cache --profile --offline
# build/reports/profile/profile-*.html → "Configuring Projects"
```

Qualitative: spike configuration is dominated by AGP per Android module + Hilt/KSP/Room on annotated types — the same class of work NiA pays — not by Forma validator overhead on this graph size. Prefer same-host before/after if quoting wall-clock.

---

## 4. Side-by-side call sites

### 4.1 Feature impl (For You)

**Upstream** — pick convention plugins, then deps (force-deps from `feature.impl` / compose conventions are invisible here):

```kotlin
plugins {
    alias(libs.plugins.nowinandroid.android.feature.impl)
    alias(libs.plugins.nowinandroid.android.library.compose)
    alias(libs.plugins.roborazzi)
}
android {
    namespace = "com.google.samples.apps.nowinandroid.feature.foryou.impl"
    testOptions.unitTests.isIncludeAndroidResources = true
}
dependencies {
    implementation(projects.core.domain)
    implementation(projects.feature.foryou.api)
    // …
}
```

**Spike** — one type entry; Hilt/Compose owned by type + attrs; project edges explicit; **no** other feature `impl`:

```kotlin
hiltImpl(
    packageName = "com.google.samples.apps.nowinandroid.feature.foryou.impl",
    compose = true,
    productFlavors = niaContentTypeFlavors,
    dependencies = transitiveDeps(/* compose + lifecycle + hilt-compose */) + deps(
        target(":feature:foryou:api"),
        target(":feature:foryou:res"),
        target(":feature:interests:api"),
        // … other feature api only …
        target(":core:domain:android-util"),
        target(":core:designsystem:ui-library"),
        target(":core:ui:compose-widget"),
        target(":core:navigation:api"),
    ),
)
```

### 4.2 Room database core

**Upstream:** four plugin aliases + `android { namespace }`.  
**Spike:**

```kotlin
roomAndroidUtil(
    packageName = "com.google.samples.apps.nowinandroid.core.database",
    compose = false,
    dependencies = transitiveDeps("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2") + deps(
        target(":core:model:library"),
    ),
)
```

### 4.3 APK root

**Upstream `app`:** 10 plugin aliases (application, compose, flavors, jacoco, firebase, hilt, oss, baseline, roborazzi, serialization) + large `android { }` + long dependency list.  
**Spike `binary`:** single `hiltFirebaseBinary(…)` with `versionCode`/`versionName`, `productFlavors`, and composition deps (features + sync + analytics/notifications leaves). UI composition stays in `root-app` (`hiltApp`).

---

## 5. What the matrix taught us (high-signal findings)

Full table: [`DOGFOOD-NIA.md` § Findings log](DOGFOOD-NIA.md). Phase D highlights:

| ID | Lesson for Forma product |
|----|---------------------------|
| **F1 / F2 / F22** | Feature `api` must stay ports-only (nav keys, no domain/android navigation). Upstream convention force-deps fight this; Forma makes the illegal edge a hard error. |
| **F3 / F28 / F29** | Shared UI is not a generic library bucket: `uiLibrary` (designsystem) + `composeWidget` (cards) + presentation DTOs beat `uiLibrary`→`library` matrix weaken. |
| **F5 / F20 / F21** | Multi-feature composition only at roots; feature→feature is **api** only at runtime. |
| **F8 / F23 / F31** | Strings/icons belong on `androidRes`; `androidUtil` content rule is load-bearing. |
| **F9 / F10** | External consumers need published **includer** or a documented flat-name `include` recipe (`target(":a:b:c")` → `:a-b-c`). |
| **F11** | Project-global Compose ≠ every target; non-UI `androidUtil` / Application-only binary need `compose = false`. |
| **F12** | Type-owned KSP companions require `processorConfigurationFeatures` on the apply path (engine fix). |
| **F14 / F15 / F25** | Path B `deriveTargetType` does **not** inherit base plugins; need `*Target(type=)` helpers (`androidUtilTarget`, `libraryTarget`). |
| **F16 / F17** | Multiple Path A plugins accumulate per type; Firebase SDK companions must be `transitiveDeps`. |
| **F18 / F30** | Some “plugins” are library stacks only (DataStore Preferences, demo serialization JSON) — do not invent structure plugins. |
| **F19** | Keep features on **domain** use cases; don’t punch `impl`→`data` for convenience. |
| **F7 / F27** | One `FormaProductFlavor` model for binary **and** libraries; flavor-scoped deps via `.forProductFlavor` → `prodImplementation`, not raw AGP DSL. |

---

## 6. Product bar checklist (F-115)

| Bar item | Status |
|----------|--------|
| Structure over configuration | **Met** — role DSL only at call sites |
| Closed matrix / no `androidLibrary` | **Met** — findings re-sliced graph |
| Composition only at roots | **Met** — binary/root-app compose features; no feature `impl`→`impl` |
| Type-owned plugins | **Met** — Hilt/Room/Firebase/Protobuf/flavors |
| Measurable external spike | **Met** — mavenLocal consumer, dual-flavor debug APKs green |
| Case study metrics | **Met** — this doc |

---

## 7. Residual gaps (explicit non-goals / follow-ups)

Not failures of the dogfood bar — candidates if Stepan opens a new ticket:

1. **Publish `tools.forma.includer`** to mavenLocal/Portal (closes F9 properly).  
2. **Retrofit/prod network** flavor source sets (demo JSON path already proves network util + F30).  
3. **Test graph** — `androidTestUtil` / fakes without production `impl`→`impl` (F5 tests).  
4. **Non-structure tooling** — Jacoco/Roborazzi/baseline/dependency-guard as fleet or root config, not target identity.  
5. **Config-time profile pair** — optional same-host numbers for marketing/CI budgets.  
6. **F-094** Portal org — still human-blocked for public plugin coordinates (spike used mavenLocal).

---

## 8. Conclusion

On a ~36-module slice of Now in Android, Forma replaced **per-module plugin shopping + convention-plugin indirection** with **Bazel-like target types**: configure behavior on the type once (Path A/B + project-global classpath), keep call sites as **attributes and deps**.

The strongest product signal is not only LOC reduction (**~59%** module scripts, **~79%** org build-logic Kotlin) but **enforced structure**: illegal edges and content rules surfaced real design fixes (nav ports, res splits, UI DTOs, domain façades) that upstream’s generic `android.library` graph leaves optional.

Dogfood **Phase A–D complete** for F-115. Engine lessons already upstreamed on `v2` where needed (KSP apply path, util/library target helpers, library product flavors). Further NiA parity is optional product work, not a blocker on the meta-build thesis.

---

## Cross links

- Design + findings: [`DOGFOOD-NIA.md`](DOGFOOD-NIA.md)  
- Matrix: [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md)  
- Target plugins: [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md)  
- Call-site surface: [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md)  
- Nav ports: [`NAVIGATION-ABSTRACTION.md`](NAVIGATION-ABSTRACTION.md)  
- Config performance recipe: [`CONFIGURATION-PERFORMANCE.md`](CONFIGURATION-PERFORMANCE.md)  
- Local publish: [`PLUGIN-PUBLISH.md`](PLUGIN-PUBLISH.md)  
