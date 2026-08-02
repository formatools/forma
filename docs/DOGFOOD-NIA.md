# Dogfood: Now in Android → Forma (F-115)

**Status:** `in_progress` — phase C through **analytics + notifications** green (F27 library flavors next)  
**Upstream:** [android/nowinandroid](https://github.com/android/nowinandroid) (Apache-2.0)  
**Pinned checkout (local):** `/Users/claw/work/nowinandroid` @ `7d45eae` (main tip when cloned 2026-07-29)  
**Dogfood fork:** `/Users/claw/work/nowinandroid-forma`  
**Spike tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`  
**Forma plugins:** `mavenLocal` version **`0.1.3-NIA`** (republish after `libraryTarget` + `androidUtilTarget` + prior KSP fix)  
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
- **Designsystem (2026-08-01):** full port stays **model-free** — Coil `DynamicAsyncImage` takes `String` URLs; features pass model fields. **F3 closed for designsystem.**
- **`core:ui` (2026-08-01):** **`composeWidget`** (not second `uiLibrary`) so matrix edge `compose-widget` → `ui-library` holds; presentation DTOs (`NewsResourceCardUi`) keep UI free of `core:model`. **F3 closed for core.ui** (see **F29**).

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
- **Resolved (binary v1, 2026-07-31):** `FormaProductFlavor` + `androidBinary(productFlavors=…)` — binary-only; `BuildConfiguration` stays build-types. NiA spike: `demo`/`prod` on `contentType`; `assembleDemoDebug` / `assembleProdDebug`.
- **Still gap (F27):** library-level productFlavors + `demoImplementation`/`prodImplementation` edges (e.g. sync FCM) — not in v1; unflavored libraries resolve against flavored APK.

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

**Not yet (phase C remainder):** Room/Firebase; full upstream designsystem/core.ui; remaining features (foryou/bookmarks/search/settings); flavors.

### Phase C — Expand features + cores (partial ✅ 2026-07-29)

Workspace: same **`forma-spike`** external tree.

| Step | Result |
|------|--------|
| Cores | `core-data-android-util` (`TopicsRepository` + in-memory), `core-domain-android-util` (`GetFollowableTopicsUseCase`), `core-designsystem-ui-library` (slim `NiaTheme` / loading / background) |
| Topic | `TopicViewModel` + designsystem-backed `TopicRoute` / `TopicScreen` (manual DI first) |
| Interests | `feature-interests-{api,res,impl}` — domain use case; navigates via **topic api** only |
| Root | Manual DI graph + multi-destination Navigator adapter |
| Verify | `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (198 tasks); APK ~9.2 MB |
| Finding F11 | Project-global `compose=true` forces Compose compiler on `androidUtil` unless `compose = false` — set on data/domain |

### Phase C — Hilt Path A ✅ (2026-07-29)

| Step | Result |
|------|--------|
| `forma-defs` | Local composite (`tools.forma.nia:forma-defs:0.0.1`) — Path A `registerTargetPlugin` on `impl` / `app` / `androidUtil` / `binary` + thin `hiltImpl` / `hiltApp` / `hiltAndroidUtil` / `hiltBinary` |
| Classpath | `extraPlugins`: forma-defs + `hilt-android-gradle-plugin` 2.59 + KSP 2.3.10 (classpath only) |
| Sources | `@HiltAndroidApp` on **binary**; `@AndroidEntryPoint` MainActivity; `@HiltViewModel` + assisted topicId; data `@Binds` module; domain `@Inject` use case |
| Engine fix | `applyTargetPlugins` now forwards `processorConfigurationFeatures` so companion `.ksp` deps create `ksp` config (was empty → “Configuration with name 'ksp' not found”) |
| F11+ | `compose = false` on data/domain **and** binary Application host (Compose compiler without runtime ICE) |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (242 tasks); Hilt tasks `hiltAggregateDepsDebug` / `hiltJavaCompileDebug` green |

**Still open in phase C (pre-Room):** Firebase binary, foryou/bookmarks/search/settings, full designsystem port.

### Phase C — Room Path B ✅ (2026-07-30)

| Step | Result |
|------|--------|
| Engine | `androidUtilTarget(type, …)` — Path B twin of `resourcesTarget`; `androidUtil` delegates |
| `forma-defs` | `RoomAndroidUtilType` = `deriveTargetType(base=androidUtil, suffix=android-util)` + thin `roomAndroidUtil` |
| Plugins on type | `androidx.room` (runtime/ktx + `room-compiler`.ksp) **and** Hilt re-bound (F14: derived types do not inherit base Path A plugins) |
| Module | `core-database-android-util` — slim `NiaDatabase` / `TopicEntity` / `TopicDao` / Hilt `DatabaseModule`; `exportSchema=true` → `schemas/…/1.json` |
| Data | `OfflineFirstTopicsRepository` seeds DAO when empty; UserData still in-memory |
| Classpath | `extraPlugins` += Room Gradle plugin (`libs.plugins.room`) |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (266 tasks); KSP `TopicDao_Impl` / `NiaDatabase_Impl`; APK ~10 MB |

**Still open in phase C (pre-Firebase):** remaining features, full designsystem, DataStore UserData.

### Phase C — Firebase Path A ✅ (2026-07-30)

| Step | Result |
|------|--------|
| `forma-defs` | `FirebaseBinary.kt` — Path A GMS + Crashlytics on `AndroidTargetTypes.binary` + thin **`hiltFirebaseBinary`** (stacks Hilt + Firebase via accumulate register) |
| Classpath | `extraPlugins` += `gmsServices` + `firebaseCrashlytics` (catalog names); settings `plugin(...)` GAVs |
| Binary | Dummy `google-services.json` (package `…spike`); `NiaSpikeApp` calls `FirebaseApp` / Crashlytics / Analytics |
| Companions | **`transitiveDeps`** for Crashlytics + Analytics (not `deps`/`.dep`) so `firebase-common` is on binary compile classpath |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (268 tasks); `processDebugGoogleServices` + Crashlytics inject tasks green |
| In-repo | Example **14** Path A companions aligned to `transitiveDeps` (same F17 lesson) |

**Still open in phase C (pre-DataStore):** remaining features, full designsystem, DataStore UserData, WorkManager sync.

### Phase C — DataStore Preferences UserData ✅ (2026-07-30)

| Step | Result |
|------|--------|
| Module | `core-datastore-android-util` — `hiltAndroidUtil` + Preferences DataStore |
| Source | `NiaPreferencesDataSource` (followed topic ids) + Hilt `DataStoreModule` (`PreferenceDataStoreFactory`) |
| Data | `OfflineFirstUserDataRepository` replaces in-memory UserData |
| Domain | `FollowTopicUseCase` so interests feature stays on domain only (no data types on feature classpath) |
| UI | Interests list Follow chip writes DataStore; topic detail already toggled via data |
| Companions | `transitiveDeps("androidx.datastore:datastore-preferences:1.1.7")` — **no** Gradle plugin (F18) |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (291 tasks) |
| Deferred | Proto DataStore + `datastore-proto` (upstream NiA shape) — later if needed |

**Still open in phase C (pre-For You):** remaining NiA features (foryou/bookmarks/search/settings), flavors, full designsystem, Proto DataStore parity, WorkManager sync.

### Phase C — For You feature + news feed ✅ (2026-07-30)

| Step | Result |
|------|--------|
| Model | `NewsResource` + `UserNewsResource` on JVM `library` (ISO date string; no kotlinx-datetime) |
| Room v2 | `NewsResourceEntity` / `NewsResourceDao`; schema `2.json`; destructive migrate on spike |
| Data | `NewsRepository` + bookmarks/onboarding on DataStore-backed `UserData` |
| Domain | `GetNewsFeedForFollowedTopics` / `BookmarkNewsResource` / onboarding use cases (F19) |
| Feature | `feature-foryou-{api,res,impl}` — home start; feed + onboarding + bookmark; → interests/topic **api** only |
| Root | `MainActivity` start = `ForYouNavKey`; multi-dest Navigator |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (335 tasks); APK ~12.8 MB |

**Still open in phase C (pre-Bookmarks):** search/settings features, flavors, full designsystem, Proto DataStore parity, WorkManager sync.


### Phase C — Bookmarks feature (Saved) ✅ (2026-07-30)

| Step | Result |
|------|--------|
| Domain | `GetBookmarkedNewsResourcesUseCase` — news ∩ bookmarked ids (F19) |
| Feature | `feature-bookmarks-{api,res,impl}` — Saved list + remove + snackbar undo |
| Edges | bookmarks.impl → topic.**api** + foryou.**api** only (F5); foryou → bookmarks.**api** |
| Root | `BookmarksNavKey` in Navigator; For You header opens Saved |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (377 tasks); APK ~13 MB |

**Still open in phase C (pre-Search):** search/settings features, flavors, full designsystem, Proto DataStore parity, WorkManager sync.

### Phase C — Search feature ✅ (2026-07-31)

| Step | Result |
|------|--------|
| Model | `SearchResult` / `UserSearchResult` / `RecentSearchQuery` on JVM `library` |
| Data | `SearchContentsRepository` (contains filter over Room topics+news; no FTS) + `RecentSearchRepository` (DataStore recent queries) |
| Domain | `GetSearchContents` / count / recent + insert/clear + follow/bookmark use cases (F19) |
| Feature | `feature-search-{api,res,impl}` — query field, recent list, topic/news results |
| **F2** | search **api** → navigation **api** only — **no** domain edge (upstream had api→domain) |
| Edges | search.impl → topic/interests/foryou **api** only (F5); foryou → search **api** |
| Root | `SearchNavKey` in Navigator; For You “Search” entry; **5-feature** composition (F21) |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (420 tasks); APK ~13 MB |

**Still open in phase C (pre-Settings):** settings feature, flavors, full designsystem, Proto DataStore parity, WorkManager sync.

### Phase C — Settings feature ✅ (2026-07-31)

| Step | Result |
|------|--------|
| DataStore | theme brand / dark config / dynamic color keys on Preferences (F18) |
| Data | `UserData` + `UserDataRepository` theme setters |
| Domain | `GetUserEditableSettings` + update brand/dark/dynamic use cases (F19) |
| Feature | `feature-settings-{api,res,impl}` — theme radios + links panel (no OSS licenses activity) |
| **F22** | upstream settings is **impl-only**; spike adds **settings-api** for NavKey ports (F1) |
| Edges | settings.impl → domain/model/designsystem/nav only (no other feature); foryou → settings **api** |
| Root | `SettingsNavKey`; For You “Settings” entry; **6-feature** composition |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (463 tasks); APK ~13 MB |

**Still open in phase C (pre-WorkManager):** flavors, full designsystem, Proto DataStore parity, WorkManager sync.

### Phase C — WorkManager sync ✅ (2026-07-31)

| Step | Result |
|------|--------|
| Data | `SyncManager` port on `core-data-android-util`; `TopicsRepository`/`NewsRepository.sync()` offline seed refresh |
| Module | `sync-work-android-util` — `hiltAndroidUtil` leaf (WorkManager + Hilt Work **library stack**, F18 sibling) |
| Sources | `SyncWorker` + `DelegatingWorker` + `WorkManagerSyncManager` + stub `SyncSubscriber` + `Sync.initialize` |
| Edges | sync → data only; **binary** → sync (composition root); no feature edge |
| Binary | `NiaSpikeApp.onCreate` → `Sync.initialize`; version `0.10.0-nia-forma-sync` |
| **F23** | `androidUtil` forbids `res/` — sync notification copy hardcoded (optional later `androidRes`) |
| **F24** | `deps()` cannot mix `.ksp` NamedDependency + `target()` in one overload — compose with `+` |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (486 tasks); APK ~13 MB |

**Still open in phase C (pre-Proto DataStore):** flavors, full designsystem, Proto DataStore parity.

### Phase C — Proto DataStore ✅ (2026-07-31)

| Step | Result |
|------|--------|
| Engine | `libraryTarget(type, …)` — Path B twin of `androidUtilTarget`/`resourcesTarget`; `library` delegates |
| `forma-defs` | `ProtobufLibraryType` = `deriveTargetType(base=jvmLibrary, suffix=library)` + thin **`protobufLibrary`** |
| Module | `core-datastore-proto-library` — slim `UserPreferences` + theme enums (lite Java/Kotlin) |
| DataStore | `core-datastore-android-util` — typed `DataStore<UserPreferences>` + `UserPreferencesSerializer`; no Preferences API |
| Companions | `transitiveDeps(androidx.datastore:datastore + protobuf-kotlin-lite)` — DataStore still **no** structure plugin (F18); protobuf lite transitive for consumers (F17 sibling / **F26**) |
| Classpath | `extraPlugins` += protobuf Gradle plugin; settings `plugin(protobuf-gradle-plugin)` |
| **F25** | Path B protobuf on JVM `library` — type owns `com.google.protobuf`; call site attrs-only |
| **F26** | Generated proto types expose lite supers → consumers need `protobuf-kotlin-lite` on compile classpath |
| Verify | `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (493 tasks); APK ~14 MB |

**Still open in phase C (pre-flavors):** full designsystem port.

### Phase C — Binary product flavors ✅ (2026-07-31)

| Step | Result |
|------|--------|
| Engine | `FormaProductFlavor` + pure `resolveProductFlavorPlan` + `ApplicationExtension.applyProductFlavors`; binary-only attr on `androidBinary` / feature config |
| Keep separate | `BuildConfiguration` = build-types only (not flavors) |
| Spike binary | `hiltFirebaseBinary(productFlavors = demo+prod contentType)`; demo `applicationIdSuffix = ".demo"` |
| Tasks | `:binary:assembleDemoDebug` + `:binary:assembleProdDebug` (bare `assembleDebug` gone once flavored) |
| **F7** | closed for APK root |
| **F27** | multi-module library flavors / variant deps (e.g. sync FCM) — not in v1; unflavored libraries resolve against flavored APK. |
| Verify | `forma-spike` assembleDemoDebug + assembleProdDebug green after `publish-local 0.1.3-NIA` |

**Still open in phase C (pre-designsystem):** full designsystem / core.ui port.

### Phase C — Full designsystem port ✅ (2026-08-01)

| Step | Result |
|------|--------|
| Module | `core-designsystem-ui-library` — replace slim theme/loading with **upstream main sources** (theme tokens, NiaIcons, components, Coil `DynamicAsyncImage`, Material3 adaptive NavigationSuite, scrollbars, placeholder drawable) |
| Type | still pure **`uiLibrary`** + `compose=true` — attrs-only; **no** model project edge (**F3**) |
| Companions | Coil + material-icons-extended + material3-adaptive (+ navigation-suite) via `transitiveDeps` — **library stack**, no structure plugin (**F28** / F18 sibling) |
| Root | `MainActivityViewModel` + `GetUserEditableSettings` → `NiaTheme(darkTheme, androidTheme, disableDynamicTheming)` so Settings brand/dark/dynamic recompose tree |
| Features | For You uses `NiaTopAppBar`/`NiaIcons`/`NiaButton`/`NiaFilterChip`; Interests/Topic use `NiaFilterChip` + `DynamicAsyncImage` |
| Verify | `forma-spike` `:binary:assembleDemoDebug` + `:binary:assembleProdDebug` → **BUILD SUCCESSFUL** (538 tasks); APKs ~25 MB |
| Version | `0.13.0-nia-forma-designsystem` |

**Still open in phase C (pre-core.ui):** optional remaining cores (network/analytics/notifications); F27 library flavors.

### Phase C — core:ui shared cards ✅ (2026-08-01)

| Step | Result |
|------|--------|
| Module | `core-ui-compose-widget` — NewsResourceCard / NewsFeed helpers / InterestsItem + strings |
| Type | **`composeWidget`** (not `uiLibrary`) — matrix allows `compose-widget` → `ui-library`; **uiLibrary ↛ uiLibrary** |
| F3 / F29 | Presentation DTOs `NewsResourceCardUi` / `NewsTopicChipUi` — **no** project edge to model; features map `UserNewsResource` at call site |
| Companions | Custom Tabs (`androidx.browser`) + Coil via `transitiveDeps` — library stack (F18 sibling) |
| Consumers | foryou / bookmarks / search use `newsResourceCardItems`; interests + search topics use `InterestsItem` |
| Verify | `forma-spike` `:binary:assembleDemoDebug` + `:binary:assembleProdDebug` → **BUILD SUCCESSFUL** (558 tasks) |
| Version | `0.14.0-nia-forma-core-ui` |

**Still open in phase C (pre-network):** optional remaining cores (network/analytics/notifications); F27 library flavors.

### Phase C — core:network demo data source ✅ (2026-08-01)

| Step | Result |
|------|--------|
| Module | `core-network-android-util` — `NiaNetworkDataSource` + `DemoNiaNetworkDataSource` + bundled upstream `topics.json` / `news.json` |
| Type | plain **`hiltAndroidUtil`** — attrs-only; no Retrofit yet |
| Companions | `kotlinx-serialization-json` via `transitiveDeps` — **library stack**, manual Json element parse (**F30** / F18 sibling; no `plugin.serialization`) |
| Data | OfflineFirst topics/news seed + `sync()` pull network → Room; remove hardcoded `TopicSeed`/`NewsSeed` |
| Flavors | Demo bind for **all** product flavors (F27 library/source-set flavors still deferred; no prod Retrofit) |
| Verify | `forma-spike` `:binary:assembleDemoDebug` + `:binary:assembleProdDebug` → **BUILD SUCCESSFUL** (581 tasks); APKs ~22 MB |
| Version | `0.15.0-nia-forma-network` |

**Still open in phase C (pre-analytics):** F27 library flavors — superseded by analytics/notifications section below.

### Phase C — core:analytics + core:notifications ✅ (2026-08-01)

| Step | Result |
|------|--------|
| Module | `core-analytics-android-util` — `AnalyticsHelper` / `AnalyticsEvent` / Stub + NoOp + `LocalAnalyticsHelper` |
| Type | plain **`hiltAndroidUtil`** + Compose **runtime** library stack for CompositionLocal (**F18** sibling; `compose=false` target — F11) |
| Bind | **StubAnalyticsHelper** for **all** product flavors (F27 prod `FirebaseAnalyticsHelper` + flavor source sets deferred) |
| Module | `core-notifications-res` **`androidRes`** + `core-notifications-android-util` **`hiltAndroidUtil`** |
| **F31 / F23** | Tray strings + vector icon on **res** module (unique namespace `…notifications.res`); util imports `R as NotificationsR` — androidUtil still no `res/` |
| Bind | **SystemTrayNotifier** for all flavors (validates res split + data edge; demo NoOp deferred with F27) |
| Data | UserData toggles → analytics events; News `sync()` → notifier for new followed-topic items after onboard |
| UI | `core-ui` → analytics; NewsFeed logs `news_resource_opened`; root `CompositionLocalProvider` + `TrackScreenViewEvent` |
| Verify | `forma-spike` `:binary:assembleDemoDebug` + `:binary:assembleProdDebug` → **BUILD SUCCESSFUL** (645 tasks); APKs ~22 MB |
| Version | `0.16.0-nia-forma-analytics-notifications` |

**Still open in phase C:** F27 library flavors / prod Firebase analytics + demo NoOp notifier source sets; optional case-study Phase D.

### Phase D — Case study write-up

- Before/after build files (LOC, plugin lines)  
- Config time optional (`CONFIGURATION-PERFORMANCE.md` recipe)  
- Upstream PR? **No** — dogfood stays fork unless Google interest  

## Findings log (living)

| ID | Date | Edge / issue | Resolution |
|----|------|--------------|------------|
| F1 | 2026-07-29 | feature api → navigation android lib | **Spike:** `core-navigation-api` pure `Navigator` port; `TopicNavKey` drops `NavKey`; adapter in `root-app` |
| F2 | 2026-07-29 | search api → domain | **Closed 2026-07-31:** search `api` = `SearchNavKey` + nav port only; contracts/use cases stay on domain for **impl**. No matrix exception. |
| F3 | 2026-07-29 | ui → model vs uiLibrary matrix | **Closed for designsystem 2026-08-01** and **core.ui 2026-08-01:** designsystem `uiLibrary` model-free; core.ui uses `composeWidget` + presentation DTOs (no model edge). Model still flows `impl` → JVM `library` only. |
| F28 | 2026-08-01 | designsystem Coil/icons/adaptive | No structure Gradle plugin — companions are **library stack** on `uiLibrary` via `transitiveDeps` (F18 sibling). Type remains plain `uiLibrary`. |
| F29 | 2026-08-01 | core.ui type + model | **`composeWidget`** (suffix `compose-widget`) depends on designsystem `ui-library` (matrix). Shared cards take `NewsResourceCardUi` DTOs; features own model→DTO mapping. Rejects second `uiLibrary` (no self-edge) and rejects matrix weaken `uiLibrary`→`library`. |
| F31 | 2026-08-01 | notifications res vs androidUtil | **Closed:** `core-notifications-res` (`androidRes`, namespace `…notifications.res`) + util → res edge. AGP requires unique namespace vs util. Validates F23 with real tray notifier (not only hardcoded sync copy). |
| F30 | 2026-08-01 | network kotlinx.serialization | Demo network uses `kotlinx-serialization-json` as **library stack** on `hiltAndroidUtil` (manual Json element parse). No `org.jetbrains.kotlin.plugin.serialization` structure plugin for dogfood demo path. Retrofit/prod + flavor source sets remain F27. |
| F5 | 2026-07-29 | test / runtime impl→impl | **Phase C runtime:** interests.impl → topic.**api** only. Test classpath still deferred |
| F8 | 2026-07-29 | upstream topic **api** ships `res/strings` | **Spike:** `feature/topic/res` `androidRes`; `api` has no `res/` (matrix content rule) |
| F9 | 2026-07-29 | `tools.forma.includer` **not** published to mavenLocal | External consumer used **flat** `include(":feature-topic-api")` + `projectDir`; Forma `target(":feature:topic:api")` → `:feature-topic-api`. Product gap: publish includer or document flat-name recipe in PLUGIN-PUBLISH |
| F10 | 2026-07-29 | First target() resolve failed with nested `:feature:topic:api` includes | Confirmed path mapping; flat names required without includer |
| F11 | 2026-07-29 | project-global `compose=true` + `androidUtil` | Compose compiler runs on data/domain without Compose runtime → ICE. **Fix:** `androidUtil(..., compose = false)` on non-UI cores; same on binary when only Application sources |
| F12 | 2026-07-29 | Path A companion `.ksp` deps | `applyTargetPlugins` called `applyDependencies` **without** `configurationFeatures` → no `ksp` configuration. **Engine fix:** optional `configurationFeatures` param forwarded from DSLs that already know processors (`impl`/`app`/`androidUtil`/`binary`/…) |
| F13 | 2026-07-29 | Hilt Application placement | `@HiltAndroidApp` must live on `com.android.application` (`androidBinary` / `hiltBinary`), not library-shell `androidApp` |
| F14 | 2026-07-30 | Path B does not inherit base type plugins | `deriveTargetType` clones matrix/content rules only; plugin registry is per-type. Room util must **re-register Hilt** (or stack plugins on the derived type) — not rely on Path A `androidUtil` Hilt alone |
| F15 | 2026-07-30 | No public `androidUtil(type=)` before helper | Engine needed `androidUtilTarget` (like `resourcesTarget`) so Path B Room DSL can pass derived type + processor features |
| F16 | 2026-07-30 | Hilt + Firebase both need Path A on binary | `TargetPluginRegistry` **accumulates** plugins per type (dedup by id). Thin `hiltFirebaseBinary` loads both binding objects then `androidBinary`. |
| F17 | 2026-07-30 | Firebase SDKs via `.dep` / `deps()` non-transitive | Crashlytics/Analytics AARs resolve but **without** `firebase-common` → `FirebaseApp` unresolved on binary. **Fix:** type-owned companions use `transitiveDeps(...)` (BOM still `transitivePlatform`). Example 14 companions fixed the same way. |
| F18 | 2026-07-30 | DataStore has no structure Gradle plugin | Preferences DataStore is a **library stack** on `hiltAndroidUtil` (companions at call site). No Path A/B plugin id. Proto DataStore would add JVM `library` + serializer — deferred; Preferences enough to validate UserData edge + follow toggle. |
| F19 | 2026-07-30 | Feature VM → data types without matrix edge | interests.impl depended on domain only; injecting `UserDataRepository` failed KSP resolve. **Fix:** `FollowTopicUseCase` on domain — feature stays domain-facing (cleaner than adding data edge). |
| F20 | 2026-07-30 | Multi-feature composition at root | foryou + bookmarks + interests + topic: each `impl` only → other feature **api**; root-app/binary compose all four. Start dest = For You (NiA home). Confirms F5 at 4-feature scale. |
| F21 | 2026-07-31 | Search + 5-feature root | search.impl → topic/interests/foryou **api** only; root composes five features. Recent queries on DataStore (F18). Contains-search over Room (no FTS) still proves matrix + F2. |
| F22 | 2026-07-31 | Settings upstream impl-only | NiA `:feature:settings:impl` has no api module. Spike adds `settings-api` (SettingsNavKey only) so root/other features navigate via **api** ports (F1) without depending on settings impl. Theme prefs on Preferences DataStore — still no Proto. |
| F23 | 2026-07-31 | sync work notification strings | `androidUtil` content rule = no `res/`. Spike hardcodes notification title/channel (system sync icon). Optional later: `sync-work-res` `androidRes` + matrix edge. |
| F24 | 2026-07-31 | `deps()` overload mix | Cannot pass `.ksp` NamedDependency and `target()` FormaTarget in one `deps(...)` call (distinct overloads). Compose `transitiveDeps + deps(ksp) + deps(target)`. |
| F25 | 2026-07-31 | Protobuf needs type-owned apply on JVM library | NiA `datastore-proto` is pure JVM + `com.google.protobuf`. Engine needed `libraryTarget` (like `androidUtilTarget`) so Path B `protobufLibrary` can pass derived type. Call sites stay attrs-only. |
| F26 | 2026-07-31 | Proto lite supers on consumers | `UserPreferences` / enums extend protobuf lite; androidUtil consumer must `transitiveDeps(protobuf-kotlin-lite)` even when proto module already depends on it (project dep does not re-export non-api runtime the same way as NiA `api(libs.protobuf…)`). |
| F27 | 2026-07-31 | Library product flavors / variant deps | Binary-only `FormaProductFlavor` closes F7 for APK. NiA also flavors libraries + `prodImplementation(FCM)` on sync + **analytics demo/prod binds** + **notifications demo NoOp / prod tray**. Spike binds Stub analytics + SystemTray notifier for **all** flavors until library flavor attrs exist. Future: optional library flavor attrs or type-owned missingDimensionStrategy — do **not** put free-form `android { productFlavors }` on every module. |

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
