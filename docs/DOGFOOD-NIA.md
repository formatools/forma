# Dogfood: Now in Android → Forma (F-115)

**Status:** `in_progress` — phase B vertical spike **green**  
**Upstream:** [android/nowinandroid](https://github.com/android/nowinandroid) (Apache-2.0)  
**Pinned checkout (local):** `/Users/claw/work/nowinandroid` @ `7d45eae` (main tip when cloned 2026-07-29)  
**Dogfood fork:** `/Users/claw/work/nowinandroid-forma`  
**Spike tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`  
**Forma plugins:** `mavenLocal` version **`0.1.3-NIA`**  
**Goal:** Validate Forma’s meta-build approach on a **real multi-module OSS** graph — not another in-repo sample.

## Product bar (what “success” means)

1. **Structure over configuration** — NiA `build-logic` convention plugins collapse to **target types** + project-global config; call sites stay attrs-only (`packageName`, deps, binary version).
2. **Closed matrix** — every first-party project edge is either allowed by [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) or recorded as a **finding** (re-slice / derived type / ports) — never a silent `androidLibrary` / free-form plugin escape.
3. **Composition only at roots** — feature `impl` ↛ feature `impl` at runtime classpath (test edges called out separately).
4. **Type-owned plugins** — Hilt/KSP, Room, Compose, Firebase, serialization apply via type/rule (Path A/B), not per-module plugin shopping.
5. **Measurable spike** — a vertical slice builds `assembleDemoDebug` (or Forma-equivalent flavor) with local-published Forma plugins.

Out of scope for phase A–B: full parity of Roborazzi, baseline profiles, dependency-guard, managed devices, Spotless, OSS licenses plugin, benchmarks APK.

## Why NiA

| Signal | Evidence on tip `7d45eae` |
|--------|---------------------------|
| Feature **api/impl** already | `:feature:{foryou,interests,bookmarks,topic,search}:{api,impl}` + `:feature:settings:impl` |
| Convention-plugin soup | `build-logic/convention` — feature api/impl, library, compose, hilt, room, firebase, jacoco, flavors, … |
| Google reference modularization | Linked from Android modularization guide |
| Real product plugins | Hilt, Room, WorkManager, Firebase, Navigation 3, Compose Material3 adaptive |
| Size | ~36 Gradle modules + build-logic (enough to hurt, still human-finishable) |

## Module inventory (settings includes)

| Gradle path | Role today | Plugins (abbrev.) |
|-------------|------------|-------------------|
| `:app` | APK root | application + compose + flavors + jacoco + firebase + hilt + oss + baseline + roborazzi + serialization |
| `:app-nia-catalog` | Catalog demo app | designsystem/ui consumer |
| `:benchmarks` | Macrobenchmark | skip phase B |
| `:lint` | Lint checks jar | skip / optional later |
| `:ui-test-hilt-manifest` | Test harness | test support |
| `:core:model` | JVM models | `jvm.library` |
| `:core:common` | JVM common + Hilt core | `jvm.library` + hilt |
| `:core:data` | Repositories | android.library + hilt + serialization |
| `:core:data-test` | Fake data | android test helpers |
| `:core:database` | Room | android.library + room + hilt |
| `:core:datastore` / `-proto` / `-test` | DataStore + protos | android/jvm mix |
| `:core:designsystem` | Compose design system | android.library + compose |
| `:core:domain` | Use cases | android.library |
| `:core:navigation` | Nav3 runtime types | android.library + hilt + compose + serialization |
| `:core:network` | Retrofit/OkHttp | android.library + hilt + serialization |
| `:core:notifications` | Notifications | android.library |
| `:core:analytics` | Analytics façade | android.library |
| `:core:ui` | Shared Compose UI | android.library + compose |
| `:core:testing` / `screenshot-testing` | Test utils | android test helpers |
| `:feature/*/{api,impl}` | Features | feature.api / feature.impl + compose on impl |
| `:sync:work` / `sync-test` | WorkManager sync | android.library + hilt |

**Project edges:** 104 explicit `projects.*` / `project()` edges in module `build.gradle.kts` files (excludes convention-plugin injected deps such as feature.api → `core:navigation` and feature.impl → `core:ui` / `core:designsystem`).

## Convention plugins → Forma ownership

| NiA convention | Forma home | Notes |
|----------------|------------|-------|
| `android.library` | **Eliminate as product API** | Map each module to a **role** (`androidUtil`, `uiLibrary`, `library`, …) — never restore `androidLibrary` |
| `android.library.compose` | Type-owned Compose / `compose=true` / `composeWidget` / `uiLibrary` | Prefer type; project-global `Forma.settings.compose` |
| `android.feature.api` | `api { }` (+ optional derived) | Today also force-deps `core:navigation` — see findings |
| `android.feature.impl` | `impl { compose = true }` | Today force-deps ui + designsystem + lifecycle + nav3 — move to type defaults or explicit attrs |
| `android.application*` | `androidBinary` (+ `androidApp` shell if split) | versionCode/Name on binary only (F-092) |
| `android.application.firebase` | Path A `firebaseBinary` / derived binary | See [`GOOGLE-LIBRARIES.md`](GOOGLE-LIBRARIES.md) / example 14 |
| `hilt` | Path A on `impl` / selected `androidUtil` (like Metro F-095) | **Do not** `.withPlugin` Hilt per module |
| `android.room` | Path B derived type e.g. `roomAndroidUtil` | schema dir = call-site or type default attr |
| `jvm.library` | JVM `library` / `util` | `tools.forma.jvm` |
| flavors `demo`/`prod` | `buildConfiguration` / project flavors story | May stay hybrid early; document gap |
| jacoco / lint / roborazzi / baseline / spotless / dependency-guard | **Non-structure** tooling | Keep Gradle-side or later fleet tasks; not target identity |

## Proposed Forma type map (first-pass)

Suffixes must match Forma templates (`*-api`, `*-impl`, `*-library`, `*-android-util`, `*-ui-library`, `*-binary`, …). Paths can stay nested; **project names** must carry the suffix (includer flat names).

### Composition roots

| NiA | Forma type | Rename / path note |
|-----|------------|--------------------|
| `:app` | `androidBinary` | Prefer `:app/binary` or name `app-binary` so suffix validates |
| `:app-nia-catalog` | `androidBinary` (secondary) or defer | Optional phase C |

### Features

| NiA | Forma | Call-site deps (concept) |
|-----|-------|---------------------------|
| `:feature/X/api` | `api` | **Only** other `api` + JVM `library` after ports fix |
| `:feature/X/impl` | `impl` | own `api` + other feature `api` + `androidUtil` cores + `uiLibrary` / design |
| `:feature/settings/impl` | `impl` | no api module today — OK; optional add empty `settings-api` later |

### Core → role-typed (no generic library)

| NiA | Forma type | Rationale |
|-----|------------|-----------|
| `:core:model` | JVM `library` (`model-library` or `core-model-library`) | Pure JVM models |
| `:core:common` | JVM `library` or `util` | Coroutines helpers; Hilt-core via type if needed |
| `:core:data` | `androidUtil` | Repos; no UI res |
| `:core:database` | `androidUtil` **or** derived `roomAndroidUtil` | Room type-owned |
| `:core:datastore` | `androidUtil` | |
| `:core:datastore-proto` | JVM `library` | Proto stubs |
| `:core:network` | `androidUtil` | |
| `:core:notifications` | `androidUtil` | |
| `:core:analytics` | `androidUtil` | |
| `:core:domain` | `androidUtil` | Use cases over data (see finding F2 if api leaks) |
| `:core:designsystem` | `uiLibrary` | Shared Compose design |
| `:core:ui` | `uiLibrary` or `composeWidget` stack | Shared feature UI chrome |
| `:core:navigation` | **Split** — see finding F1 | Ports `api` + optional android adapter `androidUtil` |
| `:core:testing` | `androidTestUtil` / `testUtil` | |
| `:core:data-test` / `datastore-test` / `screenshot-testing` | `androidTestUtil` / `testUtil` | |
| `:sync:work` | `androidUtil` | WorkManager leaf consumed by binary |
| `:sync:sync-test` | `androidTestUtil` | |

## Matrix stress findings (pre-migration)

These are the **value** of dogfooding — expected friction, not blockers to ignore.

### F1 — Feature `api` → `:core:navigation` (convention + explicit)

- **Today:** `AndroidFeatureApiConventionPlugin` api-deps `:core:navigation`; feature api modules are Android libraries exposing Nav3 route types.
- **Matrix:** `api` may depend only on `api` + `library` (+ `kmp-api`). **Not** `android-util` / UI.
- **Forma fix (aligned with [`NAVIGATION-ABSTRACTION.md`](NAVIGATION-ABSTRACTION.md)):**
  - `core-navigation-api` as pure JVM/`api` ports (routes + Navigator interfaces, **no** AndroidX Navigation in feature api).
  - Jetpack/Nav3 adapter only in `androidApp` / `androidBinary` (or `androidUtil` adapter module **not** depended on by feature `api`).
- **Dogfood metric:** feature api modules compile with **zero** `androidx.navigation3` / Compose.

### F2 — `:feature:search:api` → `:core:domain`

- **Today:** search api implements dependency on domain (Android library).
- **Matrix:** illegal for Forma `api`.
- **Fix:** move contracts into search `api` or a JVM `library`; keep domain as `androidUtil` for impl only.

### F3 — Shared UI as unrestricted library

- **Today:** feature.impl convention force-deps `:core:ui` + `:core:designsystem` (generic android.library + compose).
- **Forma:** map to `uiLibrary` / `composeWidget`; `impl` **may** depend on `ui-library` (allowed).
- **Watch:** `uiLibrary` ↛ JVM `library` in matrix — `:core:ui` → `:core:model` must be `model` as something `uiLibrary` can use, **or** model types only flow via feature/domain edges, **or** record matrix gap ticket if product should allow `uiLibrary` → `library`.

### F4 — `:core:data` façade api-exposes database/network/datastore

- **Today:** `api(projects.core.database)` etc. leaks infrastructure types to all data consumers.
- **Forma:** still representable as `androidUtil` → `androidUtil`, but product-wise prefer narrower facades (optional cleanup while migrating).

### F5 — Test classpath `impl` → `impl`

- **Today:** `feature/interests/impl` has `testImplementation(projects.feature.topic.impl)`.
- **Product:** runtime `impl` ↛ `impl` stays hard; tests may need `testUtil` fakes or binary-level instrumentation — **do not** weaken production matrix for this.

### F6 — Plugin identity explosion on `:app`

- Many application convention plugins + third-party ids.
- **Forma:** one `androidBinary` / derived `firebaseBinary` + project-global classpath (`extraPlugins`); flavors/jacoco/baseline as non-identity config.

### F7 — Product flavors `demo` / `prod`

- Affects source sets and dependency variants (`prodImplementation` FCM on sync).
- **Gap:** document whether Forma `buildConfiguration` covers flavor dimensions or temporary raw AGP remains behind binary type only.

## Phased execution plan

### Phase A — Inventory + mapping ✅

- [x] Clone upstream, pin SHA  
- [x] Module + convention + edge inventory  
- [x] Type map + matrix findings  
- [x] Ticket F-115 on board  

### Phase B — Vertical spike ✅ (2026-07-29)

Workspace: **`/Users/claw/work/nowinandroid-forma/forma-spike`** (external consumer; upstream clone stays reference-only).

| Step | Result |
|------|--------|
| `bash scripts/publish-local.sh 0.1.3-NIA` | **ok** — `~/.m2/.../0.1.3-NIA` |
| Settings | `plugins { id("tools.forma.android") version "0.1.3-NIA" }` + `mavenLocal()` — **no** `includeBuild` Forma plugins, **no** NiA `build-logic` |
| Graph | `core-model-library` + `core-navigation-api` + `feature-topic-{api,res,impl}` + `root-app` + `root-res` + `binary` |
| Verify | `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (~1m, 108 tasks) |
| Artifact | `forma-spike/binary/build/outputs/apk/debug/binary-debug.apk` (~8.9 MB) |

**Call sites:** attrs-only (`library` / `api` / `androidRes` / `impl(compose=true)` / `androidApp` / `androidBinary`). Zero `.withPlugin` / free-form plugin lists.

**Real NiA DNA in spike:** `Topic` / `FollowableTopic` model sources; topic package namespace; topic strings moved to `androidRes`; Navigator port instead of Nav3 on api.

**Not yet (phase C):** full upstream `TopicScreen`/`TopicViewModel` (Hilt, `core.data`, designsystem, `core.ui`); multi-feature app; flavors; Room/Firebase Path A on this tree.

### Phase C — Expand features + cores

- Grow spike toward real topic impl deps (data/domain as `androidUtil`)  
- foryou / interests / bookmarks / search / settings  
- Hilt Path A on impl (+ binary)  
- Room derived type on database  
- Firebase on binary  
- Optional: hybrid stub pairs for IDE  

### Phase D — Case study write-up

- Before/after build files (LOC, plugin lines)  
- Config time optional (`CONFIGURATION-PERFORMANCE.md` recipe)  
- Upstream PR? **No** — dogfood stays fork unless Google interest  

## Findings log (living)

| ID | Date | Edge / issue | Resolution |
|----|------|--------------|------------|
| F1 | 2026-07-29 | feature api → navigation android lib | **Spike:** `core-navigation-api` pure `Navigator` port; `TopicNavKey` drops `NavKey`; adapter in `root-app` |
| F2 | 2026-07-29 | search api → domain | Still open (search not in spike) |
| F3 | 2026-07-29 | ui → model vs uiLibrary matrix | Deferred (no `uiLibrary` in spike); model consumed as JVM `library` from `impl` (**allowed**) |
| F5 | 2026-07-29 | test impl→impl | Deferred |
| F8 | 2026-07-29 | upstream topic **api** ships `res/strings` | **Spike:** `feature/topic/res` `androidRes`; `api` has no `res/` (matrix content rule) |
| F9 | 2026-07-29 | `tools.forma.includer` **not** published to mavenLocal | External consumer used **flat** `include(":feature-topic-api")` + `projectDir`; Forma `target(":feature:topic:api")` → `:feature-topic-api`. Product gap: publish includer or document flat-name recipe in PLUGIN-PUBLISH |
| F10 | 2026-07-29 | First target() resolve failed with nested `:feature:topic:api` includes | Confirmed path mapping; flat names required without includer |

## Local reference commands

```bash
# Upstream reference (read-only baseline)
cd /Users/claw/work/nowinandroid
./gradlew :app:assembleDemoDebug   # needs JDK 17+ + Android SDK

# Forma plugins for consumer
cd /Users/claw/work/forma && source scripts/env-mac.sh
./scripts/publish-local.sh 0.1.3-NIA
```

## Cross links

- Vision / axioms: [`VISION.md`](VISION.md)  
- Matrix: [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md)  
- Target plugins: [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md)  
- Nav ports: [`NAVIGATION-ABSTRACTION.md`](NAVIGATION-ABSTRACTION.md)  
- Call sites: [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md)  
- Publish local: [`PLUGIN-PUBLISH.md`](PLUGIN-PUBLISH.md)  
- Hybrid stubs (IDE later): [`HYBRID-CONFIGURATION.md`](HYBRID-CONFIGURATION.md)  

## Non-goals / reject list

- Reintroducing `androidLibrary` to “match NiA libraries”  
- Per-module Hilt/Room/Compose plugin lists as the happy path  
- Weakening `impl` ↛ `impl` because interests tests need topic impl  
- Teaching raw Gradle as the migration destination  
