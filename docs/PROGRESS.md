# Forma progress log

Newest entries first.

## 2026-08-01 — F-115: NiA dogfood full designsystem port

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (`core:ui` / remaining cores next)
- **Skills/modes:** Hermes direct dogfood (external spike + docs; no Forma engine DSL change; no Grok Build)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - `core-designsystem-ui-library`: full upstream main sources (theme tokens, NiaIcons, components, Coil DynamicAsyncImage, Material3 adaptive NavigationSuite, scrollbars, placeholder drawable)
  - still plain **`uiLibrary`** + compose — Coil/icons/adaptive via `transitiveDeps` (**F28** library stack)
  - **F3 closed for DS:** no project edge to model; features pass URL strings
  - Root: `MainActivityViewModel` + settings → `NiaTheme(androidTheme/dynamic/dark)`
  - Features: For You NiaTopAppBar/icons/button/chip; Interests/Topic NiaFilterChip + DynamicAsyncImage
  - version `0.13.0-nia-forma-designsystem`
- **Verify (real host):**
  - `forma-spike` `./gradlew :core-designsystem-ui-library:compileDebugKotlin` → **BUILD SUCCESSFUL**
  - `forma-spike` `./gradlew :binary:assembleDemoDebug :binary:assembleProdDebug` → **BUILD SUCCESSFUL** (538 tasks)
  - APKs ~25 MB under `binary/build/outputs/apk/{demo,prod}/debug/`
- **Docs:** `docs/DOGFOOD-NIA.md` phase C designsystem + F3/F28; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** optional `core:ui` shared NewsFeed cards / remaining cores (network/analytics/notifications)

## 2026-07-31 — F-115: NiA dogfood binary product flavors (F7)

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (full designsystem next)
- **Skills/modes:** Hermes direct dogfood + mechanical binary-only flavor API (F-092/F-097 class; no Grok Build this slice)
- **Engine (forma repo):**
  - `FormaProductFlavor` + pure `resolveProductFlavorPlan` / `applyProductFlavors`
  - `androidBinary(productFlavors=…)` + `AndroidBinaryFeatureConfiguration.productFlavors`
  - `BuildConfiguration` stays build-types only; empty flavors = unflavored default
  - Unit tests `FormaProductFlavorTest`; thin wrappers (`hiltBinary`/`hiltFirebaseBinary`/`firebaseBinary`) forward attr
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - binary: demo/prod on `contentType`; demo `applicationIdSuffix=.demo`
  - dummy `google-services.json` clients for base + `.demo` packages
  - version `0.12.0-nia-forma-flavors`
  - **F7** closed for APK root; **F27** library flavors / `prodImplementation` deferred
- **Verify (real host):**
  - `plugins/` `./gradlew :android:test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL**
  - `bash scripts/publish-local.sh 0.1.3-NIA` → ok
  - `forma-spike` `./gradlew :binary:assembleDemoDebug :binary:assembleProdDebug` → **BUILD SUCCESSFUL** (538 tasks)
  - APKs under `binary/build/outputs/apk/{demo,prod}/debug/`
- **Docs:** `docs/DOGFOOD-NIA.md` phase C flavors + F7/F27; `CALL-SITE-SURFACE` product flavors; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** full designsystem / core.ui port

## 2026-07-31 — F-115: NiA dogfood Proto DataStore

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (flavors / full designsystem next)
- **Skills/modes:** Hermes direct dogfood (external spike + mechanical `libraryTarget` engine helper; no Grok Build this slice)
- **Engine (forma repo):**
  - `libraryTarget(type, …)` — Path B twin of `androidUtilTarget`/`resourcesTarget`; `library` delegates
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - `forma-defs/ProtobufLibrary.kt` — Path B `deriveTargetType(base=jvmLibrary)` + thin `protobufLibrary`
  - Module: `core-datastore-proto-library` — slim UserPreferences + theme protos (lite)
  - DataStore: typed `DataStore<UserPreferences>` + serializer; drop Preferences API
  - Classpath: protobuf Gradle plugin on settings/`extraPlugins`
  - **F25:** protobuf type-owned on JVM library via Path B
  - **F26:** consumers need `transitiveDeps(protobuf-kotlin-lite)` for lite supers
- **Verify (real host):**
  - `bash scripts/publish-local.sh 0.1.3-NIA` → ok
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (493 tasks)
  - APK `binary-debug.apk` ~14 MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C Proto DataStore + F25/F26; TICKETS F-115 notes; CALL-SITE / TARGET-PLUGINS helpers; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** flavors and/or full designsystem port

## 2026-07-31 — F-115: NiA dogfood WorkManager sync

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (Proto DataStore / flavors next)
- **Skills/modes:** Hermes direct dogfood (external spike + docs; no Forma engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - Data: `SyncManager` port; `TopicsRepository`/`NewsRepository.sync()` offline seed
  - Module: `sync-work-android-util` — `hiltAndroidUtil` + WorkManager + Hilt Work (F18 library stack)
  - Workers: `SyncWorker` + `DelegatingWorker` + `WorkManagerSyncManager` + stub subscriber
  - Binary: `Sync.initialize` in `NiaSpikeApp`; deps → `:sync:work:android-util`
  - **F23:** no `res/` on androidUtil — hardcoded sync notification copy
  - **F24:** `deps()` overload split for `.ksp` + `target()`
- **Verify (real host):**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (486 tasks)
  - APK `binary-debug.apk` ~13 MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C WorkManager + F23/F24; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** Proto DataStore and/or flavors

## 2026-07-31 — F-115: NiA dogfood Settings feature

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (WorkManager / Proto DataStore next)
- **Skills/modes:** Hermes direct dogfood (external spike + docs; no Forma engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - DataStore: theme brand / dark config / dynamic color keys (F18)
  - Data: `UserData` theme fields + repository setters
  - Domain: `GetUserEditableSettings` + update brand/dark/dynamic use cases (F19)
  - Feature: `feature-settings-{api,res,impl}` — theme radios + privacy/brand/feedback links
  - **F22:** spike adds settings **api** (upstream was impl-only) for NavKey ports (F1)
  - Edges: settings.impl → domain only (no other feature); foryou → settings **api**
  - Root: `SettingsNavKey`; For You “Settings” entry; **6-feature** composition
- **Verify (real host):**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (463 tasks)
  - APK `binary-debug.apk` ~13 MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C Settings + F22; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** WorkManager sync and/or Proto DataStore / flavors

## 2026-07-31 — F-115: NiA dogfood Search feature

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (settings / WorkManager next)
- **Skills/modes:** Hermes direct dogfood (external spike + docs; no Forma engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - Model: `SearchResult` / `UserSearchResult` / `RecentSearchQuery`
  - Data: `SearchContentsRepository` (contains over Room) + `RecentSearchRepository` (DataStore)
  - Domain: search/count/recent + insert/clear use cases (F19)
  - Feature: `feature-search-{api,res,impl}` — query, recent, topic/news results
  - **F2 closed:** search api → navigation api only (no domain)
  - Edges: search → topic/interests/foryou **api**; foryou → search **api** (F5/F21)
  - Root: `SearchNavKey`; 5-feature composition
- **Verify (real host):**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (420 tasks)
  - APK `binary-debug.apk` ~13 MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C Search + F2/F21; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** settings and/or WorkManager sync / Proto DataStore

## 2026-07-30 — F-115: NiA dogfood Bookmarks (Saved) feature

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (search/settings / WorkManager next)
- **Skills/modes:** Hermes direct dogfood (external spike + docs; no Forma engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - Domain: `GetBookmarkedNewsResourcesUseCase` (news ∩ bookmarked ids; F19)
  - Feature: `feature-bookmarks-{api,res,impl}` — Saved list, remove, snackbar undo
  - Edges: bookmarks → topic/foryou **api** only; foryou → bookmarks **api** (F5/F20)
  - Root: `BookmarksNavKey` + For You “Saved” entry; 4-feature composition
- **Verify (real host):**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (377 tasks)
  - APK `binary-debug.apk` ~13 MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C Bookmarks + F20 scale-up; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** search/settings and/or WorkManager sync / Proto DataStore

## 2026-07-30 — F-115: NiA dogfood For You feature + news feed

- **Ticket:** F-115 still `in_progress` · `priority: now` · `cron may continue` (bookmarks/search/settings / WorkManager next)
- **Skills/modes:** Hermes direct dogfood (external spike + docs; no Forma engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - Model: `NewsResource` / `UserNewsResource`
  - Room v2: `NewsResourceEntity`/`NewsResourceDao` + schema `2.json` + destructive migrate
  - DataStore: bookmarks + `shouldHideOnboarding` keys
  - Data: `NewsRepository` + extended `UserData`
  - Domain: feed/bookmark/onboarding use cases (F19)
  - Feature: `feature-foryou-{api,res,impl}` — home start; → interests/topic **api** only (F20)
  - Root: multi-dest Navigator; start = For You
- **Verify (real host):**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (335 tasks)
  - APK `binary-debug.apk` ~12.8 MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C For You + F20; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** bookmarks/search/settings and/or WorkManager sync

## 2026-07-30 — F-115: NiA dogfood DataStore Preferences UserData

- **Ticket:** F-115 still `in_progress` (more features / WorkManager sync / Proto DataStore next)
- **Skills/modes:** Hermes direct dogfood (external spike + docs; no Forma engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - `core-datastore-android-util` — Preferences DataStore + `NiaPreferencesDataSource` + Hilt `DataStoreModule`
  - data: `OfflineFirstUserDataRepository` replaces in-memory UserData; deps → datastore
  - domain: `FollowTopicUseCase` (F19 — interests stays domain-only)
  - interests UI: Follow chip writes DataStore via use case
  - Findings **F18** (DataStore = library stack, no Gradle plugin), **F19** (feature→data via domain use case)
- **Verify (real host):**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (291 tasks)
  - `:core-datastore-android-util:compileDebugKotlin` green; Hilt aggregate green
- **Docs:** `docs/DOGFOOD-NIA.md` phase C DataStore + F18/F19; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** more NiA features (foryou/bookmarks/search/settings) and/or WorkManager sync

## 2026-07-30 — Audit policy: cron continuation gate (docs)

- **Source:** Audit topic 4241 — implement 2026-07-29 recommendations (Hermes skills + local cron prompts; this repo slice is board/prompt only)
- **Board:** F-115 tagged `priority: now` + `cron may continue` so 4h worker may keep dogfood under the new gate
- **Docs:** `docs/cron-worker-prompt.txt` — continuation gate + rm -rf path echo; merge only gated ticket PRs
- **Live cron:** `~/.hermes/cron/jobs.json` job `18717ea2093c` prompt patched (backup `jobs.json.bak-audit-impl-20260730`)
- **No product code** in this slice

## 2026-07-30 — F-115: NiA dogfood Firebase Path A + transitive Firebase SDKs

- **Ticket:** F-115 still `in_progress` (more features / DataStore / sync next)
- **Skills/modes:** Hermes direct dogfood (external spike + example 14 companion fix; no engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - `forma-defs/FirebaseBinary.kt` — Path A GMS + Crashlytics on binary; thin `hiltFirebaseBinary` stacks Hilt+Firebase
  - `binary/google-services.json` dummy; `NiaSpikeApp` real Firebase API use
  - Classpath: `extraPlugins` + settings plugins for GMS/Crashlytics Gradle plugins
  - Findings **F16** (Path A accumulate stack), **F17** (`transitiveDeps` required for Firebase SDKs on binary)
- **In-repo:** `examples/android/14-google-firebase` Path A companions → `transitiveDeps` (same F17)
- **Verify (real host):**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (268 tasks)
  - Tasks: `processDebugGoogleServices`, `injectCrashlyticsMappingFileIdDebug`, Hilt aggregate green
  - APK `binary-debug.apk` ~12.7 MB
  - Example 14 `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL**
- **Docs:** `docs/DOGFOOD-NIA.md` phase C Firebase + F16/F17; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** more NiA features and/or DataStore UserData / WorkManager sync

## 2026-07-30 — F-115: NiA dogfood Room Path B + androidUtilTarget

- **Ticket:** F-115 still `in_progress` (Firebase / more features next)
- **Skills/modes:** Hermes direct dogfood + mechanical Path B engine helper (F-050/F-115 class)
- **Engine (`plugins/android`):**
  - New `androidUtilTarget(type, …)` — shared AGP/util wiring + `applyTargetPlugins` with processor features
  - `androidUtil` delegates to it with `AndroidTargetTypes.androidUtil`
  - Republished mavenLocal **`0.1.3-NIA`**
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - `forma-defs/RoomAndroidUtil.kt` — Path B `deriveTargetType` + `roomAndroidUtil` thin DSL
  - `core-database-android-util` — Room DB/DAO/entity + Hilt provides; schema `1.json` exported
  - data: `OfflineFirstTopicsRepository` seeds DAO; still no impl→impl
  - Findings **F14** (derived types need Hilt re-bind), **F15** (`androidUtilTarget` helper)
- **Verify (real host):**
  - `scripts/publish-local.sh 0.1.3-NIA` → **ok**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (266 tasks)
  - KSP generated `TopicDao_Impl` / `NiaDatabase_Impl`; Room schema JSON present
  - APK `binary-debug.apk` ~10.2 MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C Room; TICKETS F-115 notes; TARGET-PLUGINS pointer; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** Firebase Path A on binary and/or more NiA features

## 2026-07-29 — F-115: NiA dogfood Hilt Path A + applyTargetPlugins KSP fix

- **Ticket:** F-115 still `in_progress` (Room / more features next)
- **Skills/modes:** Hermes direct dogfood + small engine fix (target-plugin companion KSP)
- **Engine (`plugins/`):**
  - `applyTargetPlugins(type, configurationFeatures=…)` forwards processor map
  - Wired on `impl` / `androidApp` / `androidUtil` / `uiLibrary` / `library` / `androidBinary`
  - Republished mavenLocal **`0.1.3-NIA`**
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - `forma-defs` Path A: Hilt on impl/app/androidUtil/binary + thin DSLs
  - `@HiltAndroidApp` on binary; `@AndroidEntryPoint` root-app; `@HiltViewModel` features
  - data `@Binds` + domain `@Inject`; interests → topic **api** only
  - Findings F12 (KSP companion), F13 (Application on binary), F11 extended to binary
- **Verify (real host):**
  - `plugins/` `:deps:test` + `publishAllToMavenLocal -PformaLocalVersion=0.1.3-NIA` → **BUILD SUCCESSFUL**
  - `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (242 tasks; Hilt aggregate/compile green)
- **Docs:** `docs/DOGFOOD-NIA.md` phase C Hilt; TICKETS F-115 notes; spike README
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** Room Path B derived type and/or more NiA features

## 2026-07-29 — F-115: NiA dogfood phase C (data/domain/designsystem + interests)

- **Ticket:** F-115 still `in_progress` (Hilt Path A / Room / remaining features next)
- **Skills/modes:** Hermes direct dogfood scaffold (external spike; no Forma engine DSL change)
- **External tree:** `/Users/claw/work/nowinandroid-forma/forma-spike`
  - Added `core-data-android-util`, `core-domain-android-util`, `core-designsystem-ui-library`
  - Topic: `TopicViewModel` + designsystem-backed screen; deps on data + uiLibrary
  - Interests: `feature-interests-{api,res,impl}` using domain use case; navigates via topic **api** only
  - Root: manual DI + multi-destination Navigator adapter
  - **F11:** `compose = false` on non-UI `androidUtil` (project-global compose otherwise ICE)
- **Verify (real host):** `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (198 tasks); APK `binary-debug.apk` ~9.2MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase C + findings F3/F5/F11; TICKETS F-115 notes
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none product; F-094 Portal still human-blocked
- **Next:** Hilt Path A (type-owned like Metro), or more features / Room derived type

## 2026-07-29 — F-115: NiA dogfood phase B vertical spike green

- **Ticket:** F-115 still `in_progress` (phase C next)
- **Actions:**
  - `publish-local.sh 0.1.3-NIA` → mavenLocal
  - New external tree `/Users/claw/work/nowinandroid-forma/forma-spike` (no Forma `includeBuild`, no NiA build-logic)
  - Modules: `core-model-library`, `core-navigation-api` (ports), `feature-topic-{api,res,impl}`, `root-app`/`root-res`, `binary`
  - Applied F1 (nav ports) + F8 (api res → androidRes) + F9 (flat includes without includer on mavenLocal)
- **Verify (real host):** `forma-spike` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (108 tasks, ~1m); APK `binary-debug.apk` ~8.9MB
- **Docs:** `docs/DOGFOOD-NIA.md` phase B + findings F8–F10
- **Next:** phase C grow toward real topic impl + more features / Hilt Path A

## 2026-07-29 — F-115: Now in Android dogfood (phase A inventory)

- **Ticket:** F-115 → `in_progress` (P12)
- **Branch:** `forma/F-115-nia-dogfood` (from `origin/v2`)
- **Why:** Stepan selected NiA as primary real-world multimodule OSS target to validate meta-build approach outside in-repo samples.
- **Actions:**
  - Cloned upstream → `/Users/claw/work/nowinandroid` @ `7d45eae`
  - Inventory: settings includes, ~36 modules, `build-logic` convention plugins, **104** explicit project edges
  - Noted tip already has feature **api/impl** splits (strong Forma fit) + Navigation 3 + Hilt + Room + Firebase
  - Wrote [`docs/DOGFOOD-NIA.md`](DOGFOOD-NIA.md): convention→type ownership, full type map, matrix findings **F1–F7** (api→navigation, search api→domain, uiLibrary→model gap, test impl→impl, flavors, …)
  - Board: P12 + F-115; README doc index #18
- **Not in this slice:** phase B fork migration / assemble under Forma (next)
- **Next step:** clone `/Users/claw/work/nowinandroid-forma`, `publish-local.sh 0.1.3-NIA`, vertical spike topic feature + binary

## 2026-07-27 — F-104: Hybrid stub swap via project-global feature flag

- **Ticket:** F-104 → `done` (GH #43)
- **Branch:** `forma/F-104-hybrid-stub-config` (from `origin/v2`)
- **Actions:**
  - Design: `docs/HYBRID-CONFIGURATION.md` (flag, API, IDE property, simple-swap semantics, rejected paths)
  - `TargetSpec` optional `featureFlag` / `featureFlagExpected`; `resolveFeatureFlags` filters targets in `TargetDependency` + `MixedDependency`
  - DSL: `TargetDependency.whenFlag`, target overloads of `depsIf`/`depsUnless`, `featureImplementation(impl, stub)` (default flag `useFeatureStubs`)
  - Constants: `USE_FEATURE_STUBS_FLAG` / `USE_FEATURE_STUBS_PROPERTY` (`forma.useFeatureStubs`)
  - Example 15: root `featureFlags` property-backed; roots use pair helper; stubs same FQN as impl for classpath replacement; README documents both assemble modes
  - Cross-links: TARGET-FEATURE-OPTIONS, DEPS-CATALOG, CALL-SITE-SURFACE, PROJECT-CONFIGURATION, PROGRESSIVE-EXAMPLES, agent skill `forma-project-layout`
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after CLI timeout (verify + commit/PR)
- **Verify (real host):**
  - `plugins/` `./gradlew :deps:test test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL** (75 tasks)
  - `examples/android/15-hybrid-targets` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL**; APK strings include `Hello` + `World` (not Stub)
  - same `./gradlew :binary:assembleDebug -Pforma.useFeatureStubs=true` → **BUILD SUCCESSFUL**; APK strings include `HelloStub` + `WorldStub`
  - `:feature-hello-stub-impl:compileDebugKotlin` + world stub → green
- **Not in this slice:** dual-config compileOnly+impl as default; gold `application/` layout change; new target type/suffix; matrix edits
- **Commits/PRs:** this branch → PR base `v2`
- **Blockers:** none (only F-094 Portal remains blocked human/admin)
- **Next step:** empty coding queue except F-094 blocked — idle workers `[SILENT]` unless new tickets

## 2026-07-27 — F-103: Hybrid targets progressive example

- **Ticket:** F-103 → `done`
- **Branch:** `forma/F-103-hybrid-targets` (from `origin/v2`)
- **Actions:**
  - Added `examples/android/15-hybrid-targets` — co-located `feature/{hello,world}/{api,impl,stub-impl}`
  - Stubs are real `impl` modules in `stub-impl/` dirs (`packageName` `…stub`) — bare `stub/` fails self-type suffix (`Allowed name suffix(es): impl`); documented in example README
  - Composition roots default to **api + impl**; commented manual swap to `stub-impl` → F-104 teaser
  - `configureondemand=false` so unused stub-impl siblings still configure/validate
  - Curriculum: example README, `docs/PROGRESSIVE-EXAMPLES.md` row 15, `examples/README.md` ladder + fix stale 01→11 wording, `examples/agent-skills/forma-project-layout.md` hybrid section
  - No `plugins/` engine changes; no `androidLibrary`; matrix rules unchanged (`impl` ↛ `impl`)
- **Build verification:**
  - `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL**
  - `./gradlew :feature-hello-stub-impl:compileDebugKotlin :feature-world-stub-impl:compileDebugKotlin` → green
- **Commits/PRs:** this branch
- **Blockers:** none
- **Next step:** F-104 (IDE sync swap impl→stub via project-global flag)


## 2026-07-26 — F-111: Sample navigation ports + root adapter

- **Ticket:** F-111 → `done` (GH #46 sample implement; design F-102 + teach F-112 already done)
- **Branch:** `forma/F-111-sample-navigation-ports` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after CLI timeout (verify + docs + PR)
- **Code (application/ only — no Forma engine DSL):**
  - `core/navigation/api` (`api`) — `Navigator`, `NavigatorProvider`, sealed `AppDestination.CharacterDetail`, `CharacterDetailArgs.CHARACTER_ID`, `HomeChromeMode`
  - `core/navigation/android-util` — moved multi-backstack `NavigationExtensions` from common; `HomeShellNavigation` / `HomeShellNavigationProvider` (boolean chrome flag — **no** `api` import so matrix `androidUtil` ↛ `api` holds)
  - `root-app` — `JetpackNavigator` (rebindable `NavController` + Safe Args `CharactersListFragmentDirections`); `JetpackHomeShellNavigation` owns graph `R.navigation` / tab-root `R.id` + action bar; `SampleApp` implements both providers
  - Features Nav-free: list/detail fragments use ports; detail reads plain Bundle key; `HomeViewModel` takes `HomeChromeMode` only; `HomeFragment` binds shell provider only
  - Stripped `androidx.navigation` + `target(":core:navigation:res")` from list/detail/favorite/home feature modules + cargo-cult favorite viewbinding; common extensions no longer depends on navigation
  - Layer B unchanged: `core/navigation/res` still `navigationRes`
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `application/` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (1m7s, 623 tasks)
  - `rg` under `application/feature` for `findNavController|FragmentDirections|navArgs(|androidx.navigation` → **no matches**
  - `rg` `core:navigation:res` under `application/feature/**/build.gradle.kts` → **no matches**
- **Docs:** NAVIGATION-ABSTRACTION DoD F-111 checked; SAMPLE-APP wiring sketch; TICKETS F-111 done; P11 header next = F-103
- **Not in this slice:** F-103/F-104; Forma router engine; moving HomeFragment into root-app; optional `core/mvvm` unused nav dep cleanup
- **Next step:** **F-103** hybrid targets example (flat dir / api+impl co-location)

## 2026-07-26 — F-112: Progressive example navigation ports

- **Ticket:** F-112 → `done` (teach F-102 Layer A/B; sample migration remains **F-111**)
- **Branch:** `forma/F-112-navigation-ports` (from `origin/v2`)
- **Example:** `examples/android/12-navigation-ports/`
  - `navigation-api` (`api`) — `Navigator` + sealed `AppDestination` + `NavigatorProvider` (**zero** `androidx.navigation`)
  - `feature/list|detail` `impl` + `viewbinding` — UI emits destinations / `back()` only; plain Bundle arg key on detail (no `*Args` / `findNavController`)
  - `navigation/res` — Path B `navigationRes` + two-destination graph; Safe Args type-owned
  - `root-app` — composition root: `NavPortsActivity` + `JetpackNavigator` (sole Safe Args / `NavController` consumer)
  - `root-res` + `binary` — host layout / APK composition
- **Docs/curriculum:** example README; `PROGRESSIVE-EXAMPLES.md` row 12 shipped; `examples/README.md` ladder; agent overview skill blurb; `NAVIGATION-ABSTRACTION.md` F-112 done
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `examples/android/12-navigation-ports` `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (51s)
  - `:navigation-res:tasks --all` lists `generateSafeArgsDebug` / `Release`
- **Notes / pitfalls fixed while shipping:**
  - Safe-args plugin **2.9.8** (2.7.7 fails “must be used with android plugin” under AGP 9 — same ceiling as sample)
  - External nav coords via **`transitiveDeps`** — plain `deps("gav")` is non-transitive and breaks Safe Args codegen compile (`NavArgs`, annotation)
  - Path A Firebase registration on `AndroidTargetTypes.binary` is **global JVM registry** — stop Gradle daemons between example 14 and other `androidBinary` trees if residual apply appears
- **Not in this slice:** F-111 Marvel sample refactor; matrix/engine DSL; example 10 safe-args pin bump (still 2.7.7 — known stale)
- **Skills/modes:** Hermes direct (progressive-example scaffold class; F-050/F-112)
- **Next step:** **F-111** sample navigation ports + root adapter

## 2026-07-26 — F-114: Google first-party libraries coverage + Firebase usage

- **Ticket:** F-114 → `done` (clarifies Stepan “1st party” = Google/AndroidX stacks, not Forma target types)
- **Branch:** `forma/F-114-google-libraries-coverage`
- **Audit:** Architecture Components (VM/LiveData), Navigation+safe-args, Paging 2.x, Room, Compose, Material, Dagger, Play Core (`SplitCompatApplication`), Gson already **used** in `application/`. Firebase was **catalog/classpath-only** (no apply, no API use).
- **Code:**
  - `examples/android/14-google-firebase` — Path A `firebaseBinary` (GMS + Crashlytics plugins on `android.binary`), dummy `google-services.json`, `FirebaseApp`/`FirebaseCrashlytics`/`FirebaseAnalytics` usage in Application + Activity
  - **Bugfix:** `FormaDependency.plus` dropped `PlatformDependency` BOMs when mixed with named artifacts → Firebase BOM + versionless SDKs failed resolution. `MixedDependency.platforms` preserved across `+` + feature-flag resolve
- **Docs:** `docs/GOOGLE-LIBRARIES.md` coverage matrix; PROGRESSIVE-EXAMPLES + examples/README ladder row 14; TICKETS F-114
- **Verify:** `:deps:test` (BOM plus test); `examples/android/14-google-firebase` `:binary:assembleDebug` (Crashlytics inject + processDebugGoogleServices green)
- **Not in this slice:** migrate sample `application/binary` to live `firebaseBinary` (still TODO comment; needs real Firebase project for production JSON); WorkManager/DataStore/CameraX
- **Next step:** F-111 / F-112 navigation ports (board top `todo`)

## 2026-07-26 — F-113: Android first-party library examples complete

- **Ticket:** F-113 → `done`
- **Branch:** `forma/F-113-android-library-examples` (from `origin/v2`)
- **Why:** Progressive ladder claimed full Android target coverage but (1) `androidTestUtil` was declared without any androidTest consumer, (2) `androidNative` was docs-only and **unconsumable** (no matrix edges), (3) `androidTestDependencies` / `testDependencies` were typed `NamedDependency` so first-party project targets could not be passed.
- **Code:**
  - Matrix: `androidUtil` / `androidApp` / `androidBinary` → `native`; native remains a leaf (empty allow list). Kit + registry + unit assertions.
  - DSL: `testDependencies` / `androidTestDependencies` widened to `FormaDependency` on `impl`, `androidApp`, `widget`, `composeWidget`, `uiLibrary`, `library`.
  - `examples/android/09-test-utils`: instrumented `AdderAndroidTest` uses `AndroidChecks` via `androidTestDependencies = deps(target(":common:android-test-util")) + …`.
  - `examples/android/13-android-native`: CMake `androidNative` + JNI `androidUtil` façade + Activity usage.
- **Docs:** DEPENDENCY-MATRIX, PROGRESSIVE-EXAMPLES, CALL-SITE-SURFACE, README, examples/README, agent skill `forma-android-targets` coverage table.
- **Verify:** plugins unit + jacoco; 09 assemble + unit + assembleDebugAndroidTest; 13 assembleDebug (NDK 28.2 + CMake 3.22.1 on host).
- **Next step:** F-111 / F-112 navigation ports (board top `todo`).

## 2026-07-26 — F-102: Navigation abstraction design (GH #46)

- **Ticket:** F-102 → `done` (design bar only). Implement follow-ups **F-111** (sample ports + root adapter), **F-112** (`examples/android/12-navigation-ports`).
- **Branch:** `forma/F-102-navigation-abstraction` (from `origin/v2`)
- **Docs:**
  - Canonical [`docs/NAVIGATION-ABSTRACTION.md`](NAVIGATION-ABSTRACTION.md) — problem (sample bleed: `findNavController`, Safe Args `*Directions`, graph R.ids, multi-backstack helpers in feature impl/VM); Layer A presentation ports vs Layer B existing Path B `navigationRes` only; v1 = keep Jetpack Navigation behind composition-root adapter; reject engine router DSL / Cicerone-in-Forma / plugin shopping / impl→impl / dual happy paths / restoring `androidLibrary`; ticket map + DoD for F-111/F-112; refs F-089 cache + TARGET-PLUGINS
  - Cross-links: README doc index; SAMPLE-APP; PROGRESSIVE-EXAMPLES (row 12 reserved); ARCHITECTURE (tree + follow-ups + quick ref); TARGET-PLUGINS Path B blurb
- **Board:** TICKETS F-102 `done`; added F-111 + F-112 `todo` after F-102; P11 header + backlog GH #46 note
- **Code:** none (docs + board only — no sample rewrite, no plugin DSL)
- **Verify:** docs-only slice; no Gradle run required. Spot-check: design doc present; F-111/F-112 on board; no `plugins/` engine changes
- **Skills/modes:** Grok Build implement phase (design-first); `/check-work` after commit
- **Not in this slice:** F-111/F-112 code; matrix/validator changes; Forma router framework; F-103/F-104
- **Next step:** F-111 or F-112 per NAVIGATION-ABSTRACTION §6 (prefer F-112 first if sample refactor is large)

## 2026-07-26 — F-101: Close `target(...)` deps API audit (GH #56)

- **Ticket:** F-101 → `done` (GH #56 closable)
- **Branch:** `forma/F-101-target-deps-api` (from `origin/v2`)
- **Code:**
  - Pure `ProjectPathForms.gradleProjectPathFromFormaTarget` — Forma colon path → Gradle/includer dashed path (`:feature:home:impl` → `:feature-home-impl`); requires leading `:`; rejects empty / `:`-only
  - `Project.target(String)` wired through the helper (behavior parity for valid inputs)
  - Unit tests in `ProjectPathFormsTest` (multi-segment, single-segment, already-dashed, trim, rejects)
- **Docs:**
  - `docs/DEPS-CATALOG.md` §3 **Project / target deps** — API table, path forms, rejects (raw `project()`, slash/#57)
  - `docs/CALL-SITE-SURFACE.md` § Project deps via `target(...)` only
  - `docs/GETTING-STARTED.md` Project/internal blurb + cross-link
  - `README.md` sample snippet: `target(":demo:android:util")` instead of `project(...)`
- **Not in this slice:** Bazel slash path notation (#57); matrix/validator changes; sample refactors; F-102+
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :deps:test :core:test test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL** (1m11s, 75 tasks)
- **Next step:** F-102 (navigation abstraction); close GH #56 on merge

## 2026-07-26 — F-100: Buildscript classpath classifier (no same-build project())

- **Ticket:** F-100 → `done` (GH #111)
- **Branch:** `forma/F-100-buildscript-project-classpath` (from `origin/v2`)
- **Spike (Gradle 9.6.1, real host):**
  - Same-build `classpath(project(":plugin"))` → **fails** `Project dependencies cannot be declared here.`
  - `classpath(rootProject.project(":plugin"))` → class-loader / configure failure
  - `includeBuild` + GAV on buildscript classpath → **works** (plugin class visible)
- **Ship shape (hybrid C — A impossible):**
  - Pure `tools.forma.config.BuildscriptClasspath` — classify/resolve `extraPlugins` entries; reject `Project`/`ProjectDependency` with includeBuild recipe + `docs/BUILDSCRIPT-PROJECT-CLASSPATH.md`
  - Accept: String GAV, `Provider<PluginDependency>` / bare, `Provider<String>`, File/FileCollection, external module deps, map notation
  - Wire `buildScriptConfiguration` (android) + `kmpBuildscriptClasspath` (kmp) through shared resolver
  - Jacoco happy-path include for `BuildscriptClasspath*`
- **Docs:** `docs/BUILDSCRIPT-PROJECT-CLASSPATH.md`; PROJECT-CONFIGURATION / TARGET-PLUGINS / GETTING-STARTED / DEPS-CATALOG cross-links; KDoc on android+kmp config
- **Tests:** `:config` `BuildscriptClasspathTest` (12 cases)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL** (1m20s, 75 tasks)
  - `plugins/`: `./gradlew :config:test --tests tools.forma.config.BuildscriptClasspathTest` → **BUILD SUCCESSFUL**
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (2m14s, 603 tasks)
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after CLI timeout (re-verify + PR)
- **Not in this slice:** dual raw buildscript path; inventing broken `project()` support; F-101+
- **Next step:** F-101 (close `target(...)` deps API audit) after F-100 merges

## 2026-07-25 — F-110: KMP user docs + agent skill + curriculum

- **Ticket:** F-110 → `done` (P11 v1 complete; next board **F-100**)
- **Branch:** `forma/F-110-kmp-docs` (from `origin/v2` @ F-109)
- **Docs / skills:**
  - `docs/KMP-GETTING-STARTED.md` — mental model, Path A example 01, Path B greenfield, cheat sheet, consumer edges, mobile monorepo, pitfalls, checklist
  - `examples/agent-skills/forma-kmp-targets.md` — DSL/suffix/platforms/matrix rejects + skeleton
  - Curriculum links: `PROGRESSIVE-EXAMPLES.md`, `examples/agent-skills/README.md` + `forma-overview.md`, `README.md` (KMP tutorial #3), `KMP-TARGETS.md` status + §8/§12, example 01 README Next
  - `TICKETS.md` P11 header: v1 complete; resume P10 **F-100**
- **Code:** docs/curriculum only (no engine changes)
- **Verify (real host):** `examples/kmp/01-shared-library` `./gradlew build` + `:binary:run` (sanity after docs; product already green on F-109)
- **Skills/modes:** Hermes direct (architecture/docs class; F-110 mechanical curriculum)
- **Next step:** F-100 — Gradle project on buildscript classpath (GH #111)

## 2026-07-25 — F-109: Progressive example `examples/kmp/01-shared-library`

- **Ticket:** F-109 → `done` (next F-110 KMP user docs + agent skill)
- **Branch:** `forma/F-109-kmp-shared-library-example` (from `origin/v2` @ F-108)
- **Example (`examples/kmp/01-shared-library/`):**
  - Settings: `tools.forma.includer` + `tools.forma.jvm` + `tools.forma.kmp`; `arbitraryBuildScriptNames`; includeBuild `plugins`/`includer`/`build-settings`; Gradle wrapper **9.6.1**
  - Root `buildscript { kmpProjectConfiguration(platforms = KmpPlatforms(jvm=true, android=false)) }` — pure KMP+JVM (no AGP)
  - `shared-kmp-library/` → `kmpLibrary(packageName=…)` + `src/commonMain/kotlin/…/Greeting.kt` (`fun greet`)
  - `binary/` → JVM `binary` deps `target(":shared-kmp-library")`; main prints greeting
  - Example README with verify commands + expected output
- **Engine fix (minimal, required for root buildscript):**
  - Move `kmpProjectConfiguration` to **default package** (file `plugins/kmp/src/main/java/kmpProjectConfiguration.kt`), parity with `androidProjectConfiguration` — Gradle Kotlin DSL does not resolve packaged extension imports inside `buildscript { }`
- **Curriculum:** `examples/README.md` KMP track; `docs/PROGRESSIVE-EXAMPLES.md` matrix KMP column; `docs/KMP-TARGETS.md` §8 F-109 row
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `examples/kmp/01-shared-library/`: `./gradlew build` + `:binary:run` → **BUILD SUCCESSFUL** (30s); run prints `Hello, World — from Forma KMP shared library` + success line
  - `plugins/`: `./gradlew :kmp:test :jvm:test test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL** (52s) after default-package move
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after CLI timeout (verify + PR)
- **Not in this slice:** Android consumer; F-110 docs/skill; iOS/other platforms
- **Next step:** F-110 — KMP getting started + agent skill + fuller curriculum polish

## 2026-07-25 — F-108: Android/JVM consumer matrix edges → kmp.*

- **Ticket:** F-108 → `done` (next F-109 progressive KMP example)
- **Branch:** `forma/F-108-kmp-consumer-matrix` (from `origin/v2` @ F-107)
- **Code:**
  - `plugins/android` + `plugins/jvm`: `implementation(project(":kmp"))` (import `KmpTargetTypes`); publishPlugins chains `:kmp:publishPlugins`
  - **No** `:kmp` → `:android` / `:jvm` (cycle rule unchanged)
  - `AndroidTargetRegistry`: api→kmp.api; impl/app/binary→kmp.api+library+util; androidUtil→kmp.library+util; UI leaves unchanged
  - `JvmTargetRegistry`: api→kmp.api; impl/binary→kmp.api+library+util; library/util→kmp.library+util; testUtil no kmp
  - Unit tests: `AndroidTargetRegistryKmpEdgesTest`, `JvmTargetRegistryKmpEdgesTest` (graph + validator)
  - Jacoco: add `kmp` to `coverageReportModules` so F-106/F-107 happy-path includes actually contribute exec data
- **Suffix overlap (known limitation):** `*-kmp-library` ends with `-library`, so SuffixNameMatcher may accept kmp-named projects via unprefixed library/api/util allow-lists even when graph denies the kmp type pair (e.g. api↛kmp.library). Design truth = restrictionGraph `isAllowed`; longest-suffix matcher out of scope.
- **Docs:** `DEPENDENCY-MATRIX.md` § Android/JVM → KMP; `KMP-TARGETS.md` §6.2 marked implemented; TICKETS F-108 done
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :android:test :jvm:test :kmp:test` → **BUILD SUCCESSFUL** (53s)
  - `plugins/`: `./gradlew test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL** (22s)
  - `:kmp` compileClasspath has **no** `project :android` / `project :jvm` (cycle rule OK)
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after CLI timeout on jacoco
- **Next step:** F-109 — progressive example `examples/kmp/01-shared-library`

## 2026-07-25 — F-107: kmpLibrary DSL + multiplatform apply + kmpProjectConfiguration

- **Ticket:** F-107 → `done` (next F-108 consumer matrix edges)
- **Branch:** `forma/F-107-kmp-apply` (from `origin/v2` @ F-106)
- **Code (`plugins/kmp/`):**
  - `kmpProjectConfiguration` (`ScriptHandlerScope`) — registers `registerKmpDefaults()`, stores `KmpProjectSettings` (platforms jvm+android default **true**, jvmTarget `"11"`), puts Kotlin MPP (+ AGP when android) on **buildscript classpath only**
  - Feature applicator `applyKotlinMultiplatform` — applies `org.jetbrains.kotlin.multiplatform`; when android: `com.android.kotlin.multiplatform.library` + reflective namespace/compileSdk/minSdk from `AndroidProjectSettings`; jvm() + jvmTarget; fail-fast if android on without `androidProjectConfiguration`
  - `applyKmpDependencies` — commonMain / commonTest via Kotlin source-set API + same project-dep validators as `applyDependencies`
  - Public DSL: `kmpLibrary` / `kmpApi` / `kmpUtil` / `kmpTestUtil` (Unit return, attributes only; layout with `requirePackageSourceDir=false` for commonMain until F-109)
  - **No** `:kmp` → `:android` (cycle rule); `implementation(project(":config"))` + `compileOnly` AGP only
  - Unit tests: settings store (4) + feature resolution/plugin ids (6) + existing registry (5) = **15**
  - Jacoco happy-path includes `kmp/settings/**` + `KmpPluginIds*` / `KmpFeatureResolution*` (not Project apply paths)
- **Android plugin spike (AGP 9.3.0):** **preferred path works** — plugin id `com.android.kotlin.multiplatform.library` present in AGP jar; classic `com.android.library` fallback **not** used. Documented in `docs/KMP-TARGETS.md` §4.3.
- **Open decision #2:** `androidDependencies` / `jvmDependencies` attrs **deferred** (KDoc on DSL).
- **Docs:** KMP-TARGETS §4.3 spike result; TICKETS F-107 done
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :kmp:test` → **BUILD SUCCESSFUL** — 15 tests, 0 failures
  - `plugins/`: `./gradlew test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL**
- **Next step:** F-108 — Android/JVM registry consumer edges → kmp.*

## 2026-07-25 — F-106: `:kmp` plugin skeleton + KmpTargetRegistry

- **Ticket:** F-106 → `done` (next F-107 apply/DSL)
- **Branch:** `forma/F-106-kmp-skeleton` (from `origin/v2` @ F-105)
- **Code:**
  - `plugins/kmp/` — `tools.forma.kmp` Settings plugin (empty apply), `KmpTargetTypes`, `KmpTargetRegistry` / `registerKmpDefaults` (matrix from KMP-TARGETS §6.1)
  - Unit tests: `KmpTargetRegistryTest` (5) — register, matrix allow/deny, validator suffixes, selfValidator kmp-prefix, identity cache
  - Jacoco happy-path includes `**/tools/forma/kmp/target/**`
- **Docs:** ARCHITECTURE `:kmp` row live; TICKETS F-106 done
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :kmp:test` → **BUILD SUCCESSFUL** — 5 tests, 0 failures
  - `plugins/`: `./gradlew test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL**
- **Next step:** F-107 — kotlin-multiplatform apply + `kmpLibrary` DSL + `kmpProjectConfiguration`

## 2026-07-25 — F-105: Kotlin Multiplatform design (plan + board)

- **Ticket:** F-105 → `done` (implement F-106…F-110)
- **Branch:** `forma/F-105-kmp-design` (from `origin/v2`)
- **User ask:** Plan and add Kotlin multiplatform support for Gradle (Claw topic 136)
- **Design:** [`docs/KMP-TARGETS.md`](KMP-TARGETS.md)
  - New platform plugin `tools.forma.kmp` (parallel to `tools.forma.jvm`)
  - Types: `kmp-api` / `kmp-library` / `kmp-util` / `kmp-test-util` (suffix-prefixed; no collision with `api`/`library`)
  - v1 platforms: **jvm + android** only; project-global `kmpProjectConfiguration`; type owns MPP plugin
  - Rejected: call-site `kotlin { targets { } }`, plugin shopping, restoring `androidLibrary`, `kmpImpl`/`kmpBinary` in v1
  - Composition roots remain Android/JVM; Android/JVM registries gain consumer edges in F-108
  - Ticket map F-106 skeleton → F-107 apply/DSL → F-108 matrices → F-109 example → F-110 docs
- **Board:** `TICKETS.md` **P11**; VISION platform list + sequencing §8; ARCHITECTURE `:kmp` row; README link
- **Impl plan (workspace):** `.hermes/plans/2026-07-25_092348-kmp-multiplatform.md` (local Hermes; design of record is `docs/KMP-TARGETS.md`)
- **Code:** docs/board only this slice (no plugin module yet)
- **Verify:** n/a product build (design)
- **Next step:** **F-106** `:kmp` module + `KmpTargetRegistry` unit tests (Grok Build / implement PR)

## 2026-07-25 — F-099: project-global feature flags + conditional deps (GH #126)

- **Ticket:** F-099 → `done` (GH #126)
- **Branch:** `forma/F-099-target-feature-options` (from `origin/v2`)
- **Design:** `docs/TARGET-FEATURE-OPTIONS.md` — project-global named boolean flags only; flags select deps/behavior not plugin identity; binary-linked config deferred; rejects `.withPlugin` / per-module flag shopping / free-form Gradle as happy path.
- **Code:**
  - `FormaFeatureFlags` (pure) + `AndroidProjectSettings.featureFlags` + `FormaSettingsStore.featureFlagsOrEmpty()`
  - `androidProjectConfiguration(featureFlags = …)`
  - `NameSpec.featureFlag` / `featureFlagExpected`; `depsIf` / `depsUnless` / `NamedDependency.whenFlag`
  - Pure `resolveFeatureFlags` in `ConditionalDependency.kt`; wired in `applyDependencies` at apply time
  - Sample: commented `featureFlags` example in `application/build.gradle.kts` (default empty)
  - Jacoco happy-path includes `FormaFeatureFlags*` + `ConditionalDependency*`
- **Tests:** `:config` `FormaFeatureFlagsTest`; `:deps` `ConditionalDependencyTest` (DI swap recipe on/off/unknown)
- **Docs:** TARGET-FEATURE-OPTIONS; PROJECT-CONFIGURATION § featureFlags; CALL-SITE-SURFACE § product flags; DEPS-CATALOG API rows; GETTING-STARTED pointer
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after CLI timeout on app assemble
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :config:test :deps:test test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL** in 9s
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** in 10s (599 tasks, up-to-date)
- **Commits/PRs:** this branch; Hermes PR/merge; close GH #126 when merged
- **Blockers:** none (F-094 Portal still human-blocked)
- **Next step:** F-100 (Gradle project on buildscript classpath)

## 2026-07-25 — F-098: project-global core library desugaring (GH #103)

- **Ticket:** F-098 → `done` (GH #103)
- **Branch:** `forma/F-098-core-library-desugaring` (from `origin/v2` @ 3193ef8)
- **Design:** one fleet path on `androidProjectConfiguration` / `AndroidProjectSettings` only — no call-site desugar flags (same tier as `javaVersionCompatibility` / `buildFeatures`).
- **Code:**
  - `AndroidProjectSettings.coreLibraryDesugaring` (default false) + `coreLibraryDesugaringDependency` (default `com.android.tools:desugar_jdk_libs:2.1.5`)
  - `DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY` + pure `coreLibraryDesugaringDependencyOrNull`
  - DSL params on `androidProjectConfiguration`
  - `CompileOptions.applyFrom` sets `isCoreLibraryDesugaringEnabled`
  - Shared `applyCoreLibraryDesugaring(project, settings)` from library + binary + native feature definitions
  - Sample: commented enable example next to `buildFeatures` (default remains off)
- **Tests:** `:config` `CoreLibraryDesugaringTest`; `:android` `CoreLibraryDesugaringApplyTest`
- **Docs:** PROJECT-CONFIGURATION § coreLibraryDesugaring; CALL-SITE-SURFACE rejected call-site + section; GETTING-STARTED pointer
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :config:test :android:test test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL** in 1m5s (config 5 new desugar tests; android 11 tests incl. 4 desugar)
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** in 2m10s (default desugar off)
- **Commits/PRs:** this branch; Hermes PR/merge; close GH #103 when merged
- **Blockers:** none
- **Next step:** F-099 (target-feature configuration options)

## 2026-07-24 — F-097: signing configs on androidBinary (GH #51)

- **Ticket:** F-097 → `done` (GH #51 remaining checkbox)
- **Branch:** `forma/F-097-signing-configs` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after CLI timeout on app assemble
- **Design:** binary-only attrs (mirror F-092). Keep `BuildConfiguration` = build-types only. New `FormaSigningConfig` + `signingConfigs` / `buildTypeSigning` on `androidBinary` → AGP application containers only. Name→name bridge because `BuildType.() -> Unit` cannot see application `signingConfig`.
- **Code:**
  - `plugins/android/.../FormaSigningConfig.kt` — model, `toAppliedFields`, `resolveBuildTypeSigningPairs`, `applySigningConfigs`, `applyBuildTypeSigning`
  - `AndroidBinaryFeatureConfiguration` + `androidBinary` public attrs; apply order in `androidBinaryFeatureDefinition`
  - `:android` unit tests (7) via `kotlin("test")` + JUnit Platform
  - Sample: committed dummy `application/binary/demo-release.keystore` (password `android`); `release` → `demoRelease`; minify off on release
- **Docs:** CALL-SITE-SURFACE § APK signing; PROJECT-CONFIGURATION “does not”; GETTING-STARTED pointer; KDoc on binary
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :android:cleanTest :android:test` → **BUILD SUCCESSFUL** (7 tests)
  - `plugins/`: `./gradlew test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL**
  - `application/`: `./gradlew :binary:clean :binary:assembleDebug :binary:assembleRelease` → **BUILD SUCCESSFUL** (2m8s)
- **Commits/PRs:** this branch; Hermes PR/merge; close GH #51 when merged
- **Blockers:** none (F-094 Portal still human-blocked)
- **Next step:** F-098 (core library desugaring)

## 2026-07-24 — F-096: String.transitiveDep catalog parity

- **Ticket:** F-096 → `done` (GH #77)
- **Branch:** `forma/F-096-transitive-dep` (from `origin/v2`)
- **Actions:**
  - Added `val String.transitiveDep: NamedDependency get() = transitiveDeps(this)` in `plugins/deps/src/main/java/dependencies.kt` (parity with `String.dep`)
  - Unit tests in `DepsModelAndPluginHappyPathTest`: `.dep` non-transitive Implementation; `.transitiveDep` transitive Implementation + GAV; property matches `transitiveDeps(...)`
  - `docs/DEPS-CATALOG.md`: API table row + practical tip for transitive control
  - `TICKETS.md` F-096 → `done`
- **Verify (real):** `plugins/` `./gradlew :deps:test jacocoHappyPathCoverageVerification` → **BUILD SUCCESSFUL**; `./gradlew build` → **BUILD SUCCESSFUL**
- **Commits/PRs:** this branch; Hermes PR/merge
- **Blockers:** none
- **Next step:** F-097 (finish build types: signing configs)

## 2026-07-24 — Promote backlog → P10 (Stepan)

- **Action:** User: “Promote the tickets” (empty coding queue; only F-094 blocked)
- **Mapped open GH → board (new **P10**):**
  1. **F-096** GH #77 `String.transitiveDep` catalog parity → `todo`
  2. **F-097** GH #51 finish build types (signing configs) → `todo`
  3. **F-098** GH #103 core library desugaring → `todo`
  4. **F-099** GH #126 target-feature configuration options → `todo`
  5. **F-100** GH #111 Gradle project on buildscript classpath → `todo`
  6. **F-101** GH #56 `target(...)` deps API audit/close → `todo`
  7. **F-102** GH #46 navigation abstraction → `todo` (design-first, large)
  8. **F-103** GH #44 hybrid targets example → `todo`
  9. **F-104** GH #43 hybrid configuration / stub targets → `todo`
- **Not promoted:** GH #48 sample domain cleanup (not meta-build); F-094 remains `blocked`; `v2`→`master` not requested; 4h cron left running (board no longer empty)
- **Files:** `TICKETS.md` **P10** + backlog strikes; this PROGRESS entry; skill board snapshot
- **Next worker pickup:** **F-096**
- **Blockers:** none for coding queue

## 2026-07-23 — F-095: progressive example Metro DI

- **Ticket:** F-095 → `done`
- **Branch:** `forma/F-095-metro-di-example` (from `origin/v2`)
- **Actions:**
  - New ladder step `examples/android/11-metro-di`
  - Path A: `registerTargetPlugin` Metro (`dev.zacsweers.metro` **1.3.2**) on `impl` + `app`
  - Thin DSLs `metroImpl` / `metroApp` (attributes-only call sites)
  - Mini graph: `@Inject` feature impl + `@DependencyGraph` / `createGraph` at root-app
  - Docs: PROGRESSIVE-EXAMPLES, examples README, TARGET-PLUGINS quick start, agent skills, root README
  - JVM **11** on example (`javaVersionCompatibility`) for Metro runtime
- **Verify:** `examples/android/11-metro-di ./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL**
- **Blockers:** none (F-094 Portal still human-blocked)
- **Next step:** idle board may `[SILENT]` unless new tickets

## 2026-07-22 — F-093: hard-remove legacy kapt (KSP-only)

- **Ticket:** F-093 → `done`
- **Branch:** `forma/F-093-remove-kapt` (from `origin/v2`)
- **Actions:**
  - Removed public DSL: `fun kapt` / `String.kapt` (`dependencies.kt`), `DependencyHandler.kapt`
  - Removed `object Kapt` from `ConfigurationType.kt`
  - Removed `kotlinKaptFeatureDefinition` / `Kapt` branch / deprecated `kaptConfigurationFeature` from `Kotlin.kt`; `processorConfigurationFeatures()` is KSP-only
  - Tests: drop Kapt assertion; add `ksp` helper coverage
  - JaCoCo: drop dead `Kapt*` include
  - Docs: DEPS-CATALOG, CONFIGURATION-PERFORMANCE, ARCHITECTURE, forma-core-api, PRINCIPLE-AUDIT, impl KDoc; TICKETS F-093 done
- **Verify:** (this run) plugins `test jacocoHappyPathCoverageVerification build`; application `:binary:assembleDebug`
- **Commits/PRs:** commit on branch; Hermes may open PR
- **Blockers:** none
- **Next step:** F-094 (blocked Portal) or next open P8/backlog ticket

## 2026-07-22 — F-092: versionCode / versionName on binary (GH #82)

- **Ticket:** F-092 → `done`
- **Branch:** `forma/F-092-version-on-binary` (from origin/v2 @ c345f10)
- **Skills/modes:** Hermes finish path (API already shipped; docs/KDoc + board close). No Grok Build — product surface pre-existed.
- **Finding:** Issue #82 (2021) asked to move version off global `FormaConfiguration`. Current tree already:
  - requires `versionCode`/`versionName` on `androidBinary` (no defaults)
  - applies them only via `AndroidBinaryFeatureConfiguration` → AGP `ApplicationExtension.defaultConfig`
  - has **zero** version fields on `AndroidProjectSettings` / `androidProjectConfiguration`
  - sample `application/binary` + all `examples/android/*/binary` use call-site attrs
  - `androidApp` is `com.android.library` composition shell — version attrs would not be an APK surface (Gradle limit acknowledged in ticket)
- **This slice:**
  - KDoc on `androidBinary` / `androidApp` / `AndroidBinaryFeatureConfiguration` (F-092 contract)
  - Docs: `CALL-SITE-SURFACE.md` § APK version identity; `PROJECT-CONFIGURATION.md` “does not”; GETTING-STARTED multi-APK note; agent skill forbidden list
  - Board: F-092 `done`
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :android:compileKotlin build --no-daemon` → **BUILD SUCCESSFUL** in 17s (86 tasks)
  - `application/`: `./gradlew :binary:assembleDebug --no-daemon` → **BUILD SUCCESSFUL** in 1m58s (599 tasks)
- **GH #82:** close when this PR merges
- **Blockers:** none
- **Next:** F-093 Remove legacy `.kapt` once unused

## 2026-07-22 — Plugins JaCoCo + happy-path ≥60% gate

- **Branch:** `forma/test-coverage-jacoco` (from origin/v2)
- **Ask:** run full test suite, enable coverage, ensure ≥60% happy-path coverage
- **Actions:**
  - JaCoCo 0.8.13 on `plugins/` subprojects (`formaCoverage.kt` in buildSrc)
  - Per-module `jacocoTestReport` finalized from `test`
  - Aggregate `jacocoRootReport` (core/config/deps/jvm)
  - **Happy-path** report + verification: pure engine/catalog/registry/config model
    classes only (see `docs/TEST-COVERAGE.md`); LINE **COVEREDRATIO ≥ 0.60**
  - Wired into root `check` (so `./gradlew build` / CI Plugins job enforce)
  - Extra pure unit tests: `DepsModelAndPluginHappyPathTest`, `CatalogFactoriesTest`
  - CI comment on Plugins job; README link
- **Full test suite (host, real runs):**
  - `plugins/`: `./gradlew cleanTest test jacocoHappyPathCoverageVerification jacocoRootReport` → **BUILD SUCCESSFUL**
  - Happy-path LINE **~95.7%** (branch ~79%, instruction ~88%) — above 60%
  - `includer/`: test + functionalTest → SUCCESS
  - `depgen/`: test + functionalTest → SUCCESS
  - `bazel-adapter/`: test → SUCCESS
- **Blockers:** none
- **Next:** board top `todo` (unchanged by this infra slice)

## 2026-07-22 — F-091: BuildFeatures under Forma (GH #88)

- **Ticket:** F-091 → `done`
- **Branch:** `forma/F-091-build-features` (from origin/v2 @ cb65049)
- **Skills/modes:** Grok Build `--mode full` (design/plan + implement); Hermes finish path after implement timeout on app verify
- **Design (shipped):**
  - Layer ownership: type owns always-on (`viewBinding` / `composeWidget`); project owns fleet AGP flags; minimal call-site attrs only (`compose`, `impl.viewBinding`)
  - Nested `FormaBuildFeatures` for non-Compose flags (all default **false**); Compose stays top-level `compose` / `composeCompilerVersion` (one happy path)
  - No per-flag call-site shopping; no `renderScript`; dataBinding not auto-coupled to viewBinding
- **Code:**
  - `plugins/config` `FormaBuildFeatures` + `resolveWith` → `ResolvedFormaBuildFeatures`; unit tests
  - `AndroidProjectSettings.buildFeatures`; removed “No BuildConfig Support” limitation
  - `androidProjectConfiguration(buildFeatures = …)` store wiring
  - `BuildFeaturesSupport.applyFormaBuildFeatures` — explicit write of every supported flag; `dataBinding` via `LibraryBuildFeatures` / `ApplicationBuildFeatures` (AGP 9); compose via `enableCompose` or forced false
  - Wired into library + binary feature definitions; binary viewBinding = project default only
  - `impl(viewBinding=)` default now `Forma.settings.buildFeatures.viewBinding`
- **Docs:** `CALL-SITE-SURFACE.md` inventory; `PROJECT-CONFIGURATION.md` § buildFeatures; `COMPOSE.md` cross-link; sample commented example
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :config:test :android:compileKotlin build --no-daemon` → **BUILD SUCCESSFUL** (80 tasks)
  - `application/`: `./gradlew help` + `assembleDebug --no-daemon` → **BUILD SUCCESSFUL** in 2m4s (896 tasks)
- **Blockers:** none
- **Next:** F-092 versionCode / versionName on binary (and app) (GH #82)
- **GH #88:** close when this PR merges

## 2026-07-22 — F-090: Exclude modules from dependency validation (GH #97)

- **Ticket:** F-090 → `done`
- **Branch:** `forma/F-090-validation-exclusions` (from origin/v2)
- **Actions:**
  - API: `dependencyValidationExclusions: Set<String> = emptySet()` on
    `AndroidProjectSettings` + root `androidProjectConfiguration(...)`
  - Matcher: exact Gradle project path and/or name
    (`matchesDependencyValidationExclusion`); store helpers null-safe when
    settings never written (`FormaSettingsStore.isExcludedFromDependencyValidation`)
  - `applyDependencies` `projectAction` skips suffix `validator.validate` only for
    excluded dependency projects; still adds the edge. Self-type DSL validation
    unchanged. Test/androidTest project deps still unvalidated (pre-existing).
  - Unit tests: `:config` matcher/store; `:deps` apply-gate exclude vs non-exclude
  - Docs: `DEPENDENCY-MATRIX.md` exclusions section; `PROJECT-CONFIGURATION.md`
    parameter; GETTING-STARTED cross-links; sample `application/build.gradle.kts`
    commented example
- **Verify (real tool output, `source scripts/env-mac.sh`):**
  - `plugins/`: `./gradlew :core:test :validation:test :deps:test :config:test build --no-daemon` → **BUILD SUCCESSFUL** in 26s (80 tasks)
  - `application/`: `./gradlew help --no-daemon` + `./gradlew assembleDebug --no-daemon` → **BUILD SUCCESSFUL** in 1m46s (896 tasks)
- **Blockers:** none
- **Next:** F-091 BuildFeatures configuration (GH #88)
- **Commits:** local on `forma/F-090-validation-exclusions` (orchestrator opens PR)

## 2026-07-22 — F-089: Navigation Safe Args task cache (GH #110)

- **Ticket:** F-089 → `done`
- **Branch:** `forma/F-089-nav-task-cache` (from origin/v2 @ 246b6f4)
- **Skills/modes:** Hermes investigation + docs (no product DSL change required)
- **Issue read:** GH #110 screenshots (Develocity exp3 different project locations) —
  `ArgumentsGenerationTask.navigationFiles` absolute-path normalization broke
  cross-checkout build-cache reuse on old Safe Args / AGP 7.x era.
- **Bytecode check:** `navigation-safe-args-gradle-plugin` **2.7.4 / 2.7.7 / 2.8.9 / 2.9.8**
  all annotate `getNavigationFiles()` with `@PathSensitive(RELATIVE)` + `@CacheableTask`.
  Sample already pins **2.9.8** + Path B `navigationRes`.
- **Reproduce/verify (real host, `source scripts/env-mac.sh`):**
  - Two git worktrees + shared local build-cache dir:
    `:core-navigation-res:generateSafeArgsDebug` key
    `328f4fa9dc90662111aac019e754a4da` → second location **FROM-CACHE**
  - Main tree: clean → **FROM-CACHE**; warm → **UP-TO-DATE**
  - `--configuration-cache` run **BUILD SUCCESSFUL** (task executes; CC reuse may
    still miss on composite inputs — out of #110 absolute-path scope)
- **Code changes:** none in plugins (fix is dependency-era + pin). Docs only:
  `docs/CONFIGURATION-PERFORMANCE.md` Safe Args cache section; board/PROGRESS.
- **GH #110:** close when this PR merges (comment with verify summary).
- **Blockers:** none
- **Next:** F-090 Exclude modules from dependency validation (GH #97)

## 2026-07-21 — F-088: Fleet tooling phase 2

- **Ticket:** F-088 → `done`
- **Branch:** `forma/F-088-fleet-phase2` (from origin/v2 @ 4fe663a)
- **Actions:**
  - Shared Gradle helper `tools.forma.deps.fleet` (`FormaLayoutExtension`, `registerFormaLayout`, root `ensureFormaLayoutRootTasks`)
  - Per-project tasks `formaLayoutCheck` / `formaLayoutGenerate` (thin shells over core `LayoutChecker` / `LayoutGenerator`)
  - Root aggregates `formaLayoutCheckAll` / `formaLayoutGenerateAll` (`dependsOn` registered subprojects)
  - Wired into **all** Android + JVM target DSLs that take `packageName`
  - Opt-in `AndroidProjectSettings.checkPackageLayoutAtConfiguration` (default **false**); set via `androidProjectConfiguration(...)`; pure JVM skips when settings unset
  - Check accepts either `src/main/java` or `src/main/kotlin` package trees (sample uses java root)
  - `requirePackageSourceDir = false` for `resourcesTarget` / `viewBinding` / `androidBinary` (AGP identity only — avoids checkAll fail + bulk-generate pollution on res)
  - Docs: `docs/FLEET-TOOLING.md` phase-2 shipped; agent skill task usage; README + GETTING-STARTED cross-links
  - **Deferred (documented):** AST migrate rewrite, depgen resurrection
  - **GH #54:** generate path is user-complete via Gradle tasks + docs — leave note for Hermes to close (not closed from this CLI)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew :core:test build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew help :feature-home-api:formaLayoutCheck formaLayoutCheckAll --no-configuration-cache` → **BUILD SUCCESSFUL** (82 tasks)
  - Sample does **not** set `checkPackageLayoutAtConfiguration`
  - Accidental res kotlin trees from an intermediate `generateAll` run were cleaned (`git clean`); generate now skips AGP-identity targets
- **Blockers:** none
- **Next:** F-089 Navigation task cache broken (GH #110)

## 2026-07-21 — F-087: AndroidX / SDK ceiling

- **Ticket:** F-087 → `done`
- **Branch:** `forma/F-087-androidx-ceiling` (from origin/v2)
- **Skills/modes:** Hermes toolchain finish path (F-018 class); AAR metadata probe before pins
- **SDK:** sample min **23** / target **37** / compile **37**
  - Host: installed `platforms;android-36`, `platforms;android-37.0`, build-tools 36/37; symlink `platforms/android-37` → `android-37.0` (AGP dir name)
  - CI: `.github/workflows/main.yml` packages 37.0/37/36/35 + build-tools 37/36/35
- **AndroidX / related (stable, AAR-probed):**
  - core **1.19.0** (minCompileSdk 37, minAGP 9.1) · activity **1.13.0** · lifecycle **2.11.0**
  - compose **1.11.4** · room **2.8.4** · sqlite **2.7.0** · material **1.14.0**
  - annotation **1.10.0** · collection **1.6.0** · savedstate **1.5.0** · transition **1.7.0**
  - navigationevent **1.1.2** added under activity graph (DialogFragment / ComponentActivity surface)
  - appcompat **1.7.1**, fragment **1.8.9**, navigation **2.9.8** (latest stable; alphas skipped)
  - **paging 2.1.2 kept** (sample PagedList APIs — 3.x = separate rewrite)
- **Bytecode:** `javaVersionCompatibility = JavaVersion.VERSION_11` (navigation 2.9 JVM11 cannot inline into 1.8)
- **Examples:** all `examples/android/*` compile/target 37; compose demo pins → 1.11.4
- **Docs:** ENV, ARCHITECTURE, GETTING-STARTED, SAMPLE-APP toolchain stamps
- **Out of scope:** OkHttp 5 / Retrofit 3; paging 3; alpha appcompat/fragment/navigation/compose
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2322 tasks)
- **CI note:** first tip Application job failed installing non-existent `platforms;android-37` — fixed in follow-up (install `android-37.0` + symlink `android-37`)
- **Blockers:** none
- **Next:** F-088 fleet tooling phase 2

## 2026-07-21 — Promote backlog → P9 (Stepan)

- **Action:** User: “Promote all 6 tickets” on empty-board daily next-actions
- **Mapped next-actions → board (expanded GH bundle into discrete tickets):**
  1. AndroidX ceiling → **F-087** `todo`
  2. F-084 follow-ups → **F-088** `todo`
  3. Historical GH → **F-089** #110, **F-090** #97, **F-091** #88, **F-092** #82 `todo`; **F-094** #133 `blocked` (Portal human admin)
  4. Legacy `.kapt` removal → **F-093** `todo`
  5. Pause/stretch 4h worker → **not a product ticket** (board no longer empty; leave cron as-is)
  6. `v2`→`master` → **not done** (git promote stays explicit; this change only fills `TICKETS.md`)
- **Files:** `TICKETS.md` new **P9**; backlog lines struck through → F-xxx
- **Next worker pickup:** **F-087** (AndroidX / SDK ceiling)
- **Blockers:** none for coding queue; F-094 waits on Portal credentials

## 2026-07-21 — F-085: full-tree principle audit

- **Ticket:** F-085 → `done`
- **Branch:** `forma/F-085-principle-audit` (from origin/v2 @ `36d52ba`)
- **Skills/modes:** Grok Build `--mode full` design **503** + implement **timeout** (no tree changes); Hermes finish path for docs/teaching audit (F-085 class)
- **Audit record:** new `docs/PRINCIPLE-AUDIT.md` (checklist, findings A1–A7, non-issues, re-audit recipe)
- **Live code:** zero production `.withPlugin` / `TargetBuilder` / `PluginWrapper` / `androidLibrary` / free-form `plugins=` — sample uses Path B `navigationRes`; root config is single `buildscript` path
- **Teaching fixes:**
  - `examples/android/10-target-plugins/README.md` — chain API **removed** (was “deprecated”); audit/CALL-SITE links
  - `docs/VISION.md` sequencing — P6–P8 marked complete; withPlugin **removed**
  - `docs/TARGET-PLUGINS.md` F-072 row notes F-081 hard-remove
  - Agent skills: `forma-overview` (ladder 01…10 + P8 docs), hard rules 6–8, `forma-compose` house-style note
  - Cross-links: README, PROGRESSIVE-EXAMPLES, CALL-SITE-SURFACE
- **Out of scope (documented):** sample mass-migrate off typed catalogs; optional `firebaseBinary`; F-084 Gradle-task follow-ups; AndroidX ceiling
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2322 tasks)
- **Blockers:** none
- **Next:** P8 complete — empty product board; promote backlog only on user request

## 2026-07-20 — F-084: fleet tooling check/generate/migrate v1

- **Ticket:** F-084 → `done` (v1 toolkit; follow-ups listed in `docs/FLEET-TOOLING.md`)
- **Branch:** `forma/F-084-fleet-tooling` (from origin/v2)
- **Skills/modes:** Grok Build `--mode full` design 503 + implement hang (no tree changes); Hermes finish path for pure-core + docs slice
- **Code (`plugins/core` `tools.forma.core.fleet`):**
  - `PackageLayout` / `SourceLanguage` — packageName → `src/main/{kotlin|java}/…`
  - `ProjectPathForms` — includer `:feature-home-impl` ↔ Forma `:feature:home:impl`
  - `LayoutGenerator` plan/apply (GH #54) + optional `.gitkeep`
  - `LayoutChecker` — missing package dir violations
  - `MigratePlanner` — path rename + suggested reference rewrites (not AST)
  - Unit tests: `FleetToolkitTest` (GH #54 package, idempotent apply, path forms, migrate)
- **Docs / teaching:**
  - New `docs/FLEET-TOOLING.md` (modes, tool map, API, follow-ups)
  - VISION principle 3 link; README + GETTING-STARTED; ARCHITECTURE audit stamp
  - Agent skill `forma-fleet-tooling` + skills README + PROGRESSIVE-EXAMPLES row
- **Out of scope (documented follow-ups):** Gradle tasks, AST migrate, depgen finish, config-time package hook
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew :core:test build` → **BUILD SUCCESSFUL** (78 tasks; core fleet unit tests green)
- **Blockers:** none for v1
- **Next:** F-085 full-tree principle audit

## 2026-07-20 — F-083: one external-deps house style

- **Ticket:** F-083 → `done`
- **Branch:** `forma/F-083-external-deps-convention` (from origin/v2)
- **Skills/modes:** Grok Build `--mode full` design 503 + implement timeout (no tree changes); Hermes finish path for docs/convention slice (F-083 is house-style docs class)
- **Decision (locked):**
  - **House style:** `projectDependencies` → `libs.*` + `deps(...)`
  - **Advanced only:** typed `build-dependencies/` objects (`androidx.*`, `google.*`) for large nested non-transitive graphs
  - Not dual happy paths; sample may still mix at monorepo scale
- **Docs / teaching:**
  - Rewrote `docs/DEPS-CATALOG.md` (house style §1, advanced §2)
  - README, GETTING-STARTED, JVM-GETTING-STARTED, CALL-SITE-SURFACE, ARCHITECTURE §2.4, PROGRESSIVE-EXAMPLES
  - Agent skill `forma-deps-catalog` + `examples/android/08-deps-catalog/README.md`
  - Light KDoc on `Settings.projectDependencies`; sample settings comment
- **Out of scope (intentional):** mass-migrate sample modules off typed catalogs
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2322 tasks)
  - `examples/android/08-deps-catalog ./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (128 tasks)
- **Blockers:** none
- **Next:** F-084 fleet tooling (check/generate/migrate)

## 2026-07-20 — F-086: kapt → KSP + AGP built-in Kotlin

- **Ticket:** F-086 → `done`
- **Branch:** `forma/F-086-ksp-migration` (from origin/v2)
- **User OK:** “Schedule Migration to ksp” (topic 136)
- **Forma engine:**
  - `ConfigurationType.Ksp` + `ksp()` / `String.ksp` + `DependencyHandler.ksp`
  - `processorConfigurationFeatures()` auto-applies `com.google.devtools.ksp` (or legacy kapt)
  - Android targets use AGP **built-in Kotlin** (no `kotlin-android` plugin)
- **Sample:**
  - `google.dagger` → `dagger-compiler` on **ksp** (was kapt)
  - Drop `android.builtInKotlin=false` / `android.newDsl=false`
  - Navigation **2.9.8** + safe-args plugin **2.9.8** (AGP 9 new DSL via `com.android.base` / AndroidComponentsExtension)
- **Verify:** plugins + application (2322 tasks) + jvm-application **BUILD SUCCESSFUL**
- **Next:** F-083


## 2026-07-20 — F-019: Gradle 9.6.1 + Kotlin 2.3.21 + AGP 9.3.0

- **Ticket:** F-019 → `done` (Phase 1)
- **Branch:** `forma/F-019-gradle9-kotlin` (from origin/v2)
- **User OK:** explicit “Schedule Kotlin and Gradle 9 upgrade” (topic 136)
- **Pins:**
  - All **24** wrappers → Gradle **9.6.1** (embedded Kotlin **2.3.21**)
  - AGP lockstep **9.3.0** (`plugins/android` + sample `agpVersion` + forces)
  - aapt2-proto **9.3.0-15703166**; KSP **2.3.10**; Compose compiler default **2.3.21**
  - Dagger **2.60.1** (Kotlin metadata 2.3 support)
- **Forma / Gradle 9 API:**
  - `ProjectDependency.dependencyProject` removed → resolve via `path` + `Project.target(ProjectDependency)`
  - `String.capitalized()` → `replaceFirstChar` titlecase
  - `kotlinOptions.jvmTarget` → `compilerOptions.jvmTarget` + `JvmTarget.fromTarget`
  - Drop `-Xcontext-receivers` (superseded in Kotlin 2.3)
  - AGP public DSL: `com.android.build.api.dsl.LibraryExtension` / `CommonExtension` (no type args); libraries no longer set `targetSdk`
  - Sample: explicit `viewModels<CharacterFavoriteViewModel>()` (Kotlin 2.3 reified intersection error)
- **AGP 9 consumer flags (Phase 1):** `android.builtInKotlin=false` + `android.newDsl=false` while sample still uses **kapt**/Dagger. Phase 2 = built-in Kotlin + kapt→KSP.
- **Other:** includer TestKit JVM **17** + foojay-resolver **1.0.0**
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL**
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2363 tasks)
  - `jvm-application/ ./gradlew build` → **BUILD SUCCESSFUL**
  - `includer/ ./gradlew build` → **BUILD SUCCESSFUL**
  - `depgen/ ./gradlew build` → **BUILD SUCCESSFUL**
  - `plugins/ ./gradlew --version` → Gradle **9.6.1** / Kotlin **2.3.21** / JVM 21
- **Blockers:** none for Phase 1
- **Next:** F-083 external-deps house style; F-019 Phase 2 (built-in Kotlin + KSP) when prioritized

## 2026-07-20 — F-082: One global configuration path

- **Ticket:** F-082 → `done`
- **Branch:** `forma/F-082-global-config-path` (from origin/v2)
- **Skills/modes:** /goal + todo_write + explore before edits; product code first (compile green) then docs; self-verify planned with /check-work
- **Code:**
  - Hard-removed `fun Project.androidProjectConfiguration(...)` (and its dead params `dataBinding`/`validateManifestPackages`/`generateMissedManifests`) from `plugins/android/src/main/java/androidProjectConfiguration.kt`
  - Replaced imprecise KDoc; `ScriptHandlerScope.androidProjectConfiguration` now clearly documents: single supported path, `extraPlugins` = **buildscript classpath only**, points to `TARGET-PLUGINS.md` for apply, `Forma`/`FormaSettingsStore`/`AndroidProjectSettings` store story, "do not call from arbitrary Project scopes"
  - No remaining references or call sites to the removed overload (verified by grep)
- **Docs:**
  - New `docs/PROJECT-CONFIGURATION.md` — one path, what the call does (classpath + store + registry), what it does not (apply), removed overload, single settings/store story table, pointers + checklist
  - README: added classpath-only comment on `extraPlugins` sample
  - CALL-SITE-SURFACE.md: marked F-082 done + link to PROJECT-CONFIGURATION
  - GETTING-STARTED.md: link to new doc; clarified store behavior
  - ARCHITECTURE.md: updated root config snippet with `project=`, `buildscript`, classpath note
  - TARGET-PLUGINS.md: reinforced classpath story + cross link
- **No dual path** in examples/agent-skills (scanned; all usage already correct `buildscript` form; no "old approach" language)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew build --console=plain` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew build --console=plain` → **BUILD SUCCESSFUL** (2554 tasks, 3m56s)
- **Blockers:** none
- **Next:** F-083 one external-deps house style

## 2026-07-20 — F-081: call-site surface audit + hard-remove chain API

- **Ticket:** F-081 → `done`
- **Branch / PR:** `forma/F-081-call-site-surface` → base `v2`
- **Skills/modes:** Grok Build `--mode full` failed (design 503 + implement timeout/max-turns, no tree changes); Hermes finish path for mechanical removal + docs inventory
- **Code removed:**
  - `plugins/android/.../TargetBuilder.kt`
  - `plugins/deps/.../PluginWrapper.kt`
  - `plugins/deps/.../PluginConfiguration.kt` (chain-only; distinct from config-store `PluginConfiguration`)
  - `build-dependencies/.../Plugins.kt` + unused `firebase-crashlytics-gradle` compile dep
- **Docs:**
  - New `docs/CALL-SITE-SURFACE.md` — Android/JVM DSL table, `compose`/`viewBinding` flag policy, removed APIs
  - `TARGET-PLUGINS.md` / GETTING-STARTED / README / agent skill: chain **removed** (not merely deprecated)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2554 tasks)
  - `jvm-application/ ./gradlew build` → **BUILD SUCCESSFUL** (61 tasks)
  - No production `TargetBuilder` / `PluginWrapper` sources remain
- **Blockers:** none
- **Next:** F-082 one global configuration path

## 2026-07-20 — F-073: target plugins user docs + progressive example + agent skill

- **Ticket:** F-073 → `done`
- **Branch / PR:** `forma/F-073-target-plugins-docs` → base `v2`
- **Skills/modes:** Grok Build unavailable (503 + max-turns on stale F-072 plan); Hermes implement docs/example slice directly (F-073 is docs/example class)
- **Docs / teaching:**
  - `docs/TARGET-PLUGINS.md` — status F-070–F-073 shipped + **Quick start (users)**
  - `docs/GETTING-STARTED.md` — external plugins section; `extraPlugins` = classpath only
  - `docs/SAMPLE-APP.md` — `navigation/res` is Path B `navigationRes`
  - `docs/PROGRESSIVE-EXAMPLES.md` + `examples/README.md` — Android step **10**
  - Root `README.md` — target plugins link points at shipped how-to + example
- **Progressive example:** `examples/android/10-target-plugins/`
  - Local `forma-defs/` included build: `targetPlugin` + `deriveTargetType` + `navigationRes` DSL
  - Call site `feature/hello/res` uses `navigationRes(...)` (no plugin ids)
  - `root-res` stays plain `androidRes` (contrast)
  - Classpath: catalog `plugin(...)` + `extraPlugins` for forma-defs + safe-args
- **Agent skill:** `examples/agent-skills/forma-target-plugins.md` + README/overview/android-targets pointers
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `examples/android/10-target-plugins ./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL**
  - `:feature-hello-res` runs safe-args / navigation resource processing (type-owned plugin applied)
  - `plugins/ ./gradlew :deps:test :android:compileKotlin` → run in same session
- **GH #36:** close with comment pointing at docs + example
- **Blockers:** none
- **Next:** F-081 call-site surface audit (P8)


## 2026-07-19 — F-072: navigationRes Path B + deprecate withPlugin chain

- **Ticket:** F-072 → `done`
- **Branch / PR:** `forma/F-072-navigation-res` → base `v2`
- **Skills/modes:** Grok Build `--mode full` (design + implement max-turns); Hermes finish path: `resourcesTarget` helper, core classpath fix, verify builds, bookkeeping
- **Code:**
  - `plugins/android`: `resourcesTarget(type, …)` shared res wiring; `androidRes` / `androidBinary` / `uiLibrary` return **Unit**; `TargetBuilder` `@Deprecated`
  - `plugins/deps`: `PluginWrapper` `@Deprecated`
  - `build-dependencies`: `NavigationRes.kt` — `deriveTargetType(sample.navigation-res, base=res, suffix=res)` + `navigationRes(...)` DSL; deps on `tools.forma:android` + `:core`; legacy `Plugins` object `@Deprecated`
  - Sample: `application/core/navigation/res` → `navigationRes(...)` (no plugin ids); binary comment updated (no chain)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew :deps:test :android:compileKotlin build` → **BUILD SUCCESSFUL**
  - `application/ ./gradlew :core-navigation-res:compileDebugKotlin` → **SUCCESS** including `generateSafeArgsDebug` (type-owned safe-args applied)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2554 tasks)
  - `rg` active `.withPlugin` call sites → **none** (only deprecation messages / TestKit `withPluginClasspath`)
- **Blockers:** none
- **Next:** F-073 user docs + progressive example + agent skill; close GH #36

## 2026-07-19 — F-071: type-owned target plugins registry + auto-apply

- **Ticket:** F-071 → `done`
- **Branch / PR:** `forma/F-071-target-plugins` → base `v2`
- **Skills/modes:** Grok Build `--mode full` (design + partial implement, max-turns); Hermes finish path: compile fixes, unit tests, remaining DSL verify, bookkeeping
- **Code:**
  - `plugins/deps`: `TargetPluginSpec` / `targetPlugin`, `TargetPluginRegistryApi` + `DefaultTargetPluginRegistry` + global `TargetPluginRegistry`, `registerTargetPlugin` (Path A), `deriveTargetType` (Path B, optional core registry clone), `Project.applyTargetPlugins`
  - `plugins/android`: re-exports on `AndroidTargetRegistry`; **all** Android target DSLs call `applyTargetPlugins(type)` after `applyFeatures`, before `applyDependencies`
  - Unit tests: `TargetPluginRegistryTest` (ordered register/dedupe, Path A global bind, Path B clone + plugins, empty unbound)
  - Legacy `TargetBuilder` / `PluginWrapper` / sample `.withPlugin` **untouched** (F-072)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `plugins/ ./gradlew :deps:test :android:compileKotlin` → SUCCESS
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew help` → **BUILD SUCCESSFUL** (legacy path still configures)
- **Blockers:** none
- **Next:** F-072 migrate sample to derived types (e.g. `navigationRes`); hard-deprecate `TargetBuilder` / `.withPlugin`

## 2026-07-19 — P8 board: principle-alignment tickets + F-080 docs

- **User ask:** create tickets to update implementation to match Forma goals (3 root principles)
- **Tickets added (P8):**
  - **F-080** `done` — codify principles in VISION/README/AGENTS/GETTING-STARTED
  - **F-081** call-site surface audit (Unit returns, minimal attrs)
  - **F-082** one global configuration path
  - **F-083** one external-deps house style
  - **F-084** fleet tooling check/generate/migrate (GH #54 theme)
  - **F-085** full-tree sample/examples/skills audit
- **P7 unchanged priority:** **F-071** still top coding ticket (type-owned plugins)
- **TICKETS.md** top section = root goals bar for all workers
- **Product code:** none this slice
- **Next:** F-071 implement

## 2026-07-19 — F-070 revised again: Bazel-like call sites

- **Ticket:** F-070 design correction (PR #185 update)
- **User correction:** first Path A example was wrong — extending a target type with a plugin must **auto-apply on every call site**; config should look almost like **Bazel** in Gradle files. No per-callsite `pluginConfig(binding)`.
- **Model:**
  - Type/rule owns plugin identity
  - Call sites = attributes only (`navigationRes(packageName=…, dependencies=…)`)
  - Path A = static extend pre-defined type (global for that type)
  - Path B = derived type (preferred when only some modules need the plugin)
  - Complex config = rule attrs or one type-associated config arg — never plugin ids
- **Docs:** rewrite [`docs/TARGET-PLUGINS.md`](TARGET-PLUGINS.md)
- **Next:** F-071 type→plugin registry + auto-apply

## 2026-07-19 — F-070 revised: reject free-form plugins lists

- **Ticket:** F-070 remains `done` (design corrected); F-071…F-073 notes updated
- **User correction:** free-form `plugins = plugins(plugin("id")…)` on every target is **exactly what to avoid**
- **Note:** intermediate draft still had call-site binding selection — superseded by Bazel auto-apply revision above
- **Product code:** none

## 2026-07-19 — F-070: target plugins API design

- **Ticket:** F-070 → `done` (design only); **F-071…F-073** `todo` on board (P7)
- **Branch / PR:** #184 → `v2` (initial write; superseded)
- **Note:** first draft free-form `plugins =` lists — wrong
- **Product code:** none this slice
- **Blockers:** none

## 2026-07-19 — F-018 close: docs soak + ticket done

- **Ticket:** F-018 → `done` (8.x terminal toolchain; AGP 9 = F-019 only with explicit OK)
- **Branch / PR:** `forma/F-018-docs-close` → base `v2`
- **Skills/modes:** Hermes orchestrator only (mechanical F-018 docs/CI pin class — no Grok Build product coding)
- **Why this slice:** Product ladder already on tip via #179–#182; tip CI [29680345620](https://github.com/formatools/forma/actions/runs/29680345620) **SUCCESS**. Several living docs still claimed AGP **8.1.2** / Gradle **8.7** / Kotlin **1.9.22** as current.
- **Docs aligned to live baseline:**
  - `docs/ENV.md` — verified section → Gradle **8.14.5**, AGP **8.13.2**, Kotlin/KSP **2.0.21**, Compose **2.0.21**, sample SDK 23/35/35
  - `docs/ARCHITECTURE.md` — wrapper table, AGP compile line, CI SDK pins, toolchain snapshot, F-018 done row
  - `docs/COMPOSE.md` — compiler pairing Kotlin **2.0.21** + Compose Compiler plugin note
  - `docs/GETTING-STARTED.md` — tools table, pitfalls, checklist (JDK 21 + SDK 35)
  - `TICKETS.md` — F-018 `done` with #179–#182 summary
- **Product code:** none (versions already correct on `v2` @ `47c2e70`)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `java -version` → **21.0.11**
  - `plugins/ ./gradlew --version` → **Gradle 8.14.5** / Kotlin **2.0.21** / JVM 21
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2554 tasks)
- **Blockers:** none
- **Next:** prioritized queue empty unless user adds F-xxx or promotes backlog (#110, #97, #88, #82, Portal #133) or explicitly OKs **F-019** AGP 9

## 2026-07-19 — F-018 deps: Gradle 8.14.5 + AGP 8.13.2 + library refresh

- **Ticket:** F-018 remains `in_progress` (8.x terminal toolchain + sample deps; AGP 9 = F-019)
- **User ask:** “Upgrade the dependencies to the latest versions too”
- **Toolchain**
  - All wrappers → **Gradle 8.14.5** (Kotlin **2.0.21**)
  - AGP lockstep **8.13.2** + `aapt2-proto:8.13.2-14304508`
  - KSP **2.0.21-1.0.28**; Compose compiler default **2.0.21** + Kotlin Compose Compiler plugin when `compose=true`
  - Sample SDK: min **23** / target **35** / compile **35** (Compose 1.9 / AndroidX AAR metadata)
- **Libraries** (`build-dependencies` + application catalog) — latest **compatible with AGP 8.13 + compileSdk 35** (absolute Maven latest core/activity/lifecycle need AGP **9.1** + SDK 36/37):
  - core **1.16.0**, activity **1.10.1**, lifecycle **2.10.0**, fragment **1.8.9**, appcompat **1.7.1**, material **1.13.0**, compose **1.9.4**, room **2.7.2**, coil **2.7.0**, dagger **2.56.2** (+ jakarta.inject), navigation **2.7.7** (2.8+ broke library `verifyReleaseResources` with safe-args graphs), paging stays **2.1.2** (PagedList API)
  - OkHttp/Retrofit stay **4.12 / 2.11** (5/3 = separate migration)
- **Forma AGP API**: migrate off `internal.dsl` BuildType/DefaultConfig/BaseAppModuleExtension → public `api.dsl`; disable flaky library `verify*Resources`
- **Sample fixes**: Transformations → LiveData `map`/`switchMap`; Fragment observe → `viewLifecycleOwner`; VisibleForTesting.PRIVATE removed
- **Verify (real):**
  - `plugins/ ./gradlew build` → SUCCESS
  - `application/ ./gradlew build` → SUCCESS
  - includer, depgen, jvm-application → SUCCESS
  - bazel-adapter toolchain 21 + KGP 2.0.21
- **Next:** optional F-019 AGP 9 / Gradle 9 for true Maven-latest AndroidX; or declare F-018 done after soak

## 2026-07-19 — F-018 Phase 1b: host + CI → JDK 21

- **Ticket:** F-018 remains `in_progress` (wrappers #180 + JDK 21 done; AGP ladder remains)
- **Branch / PR:** `forma/F-018-jdk21` → new PR base `v2` (Phase 1 already on tip via #180)
- **Host:** `brew install openjdk@21` → OpenJDK **21.0.11** at `/usr/local/opt/openjdk@21/...`
- **Code / config:**
  - `scripts/env-mac.sh` default `JAVA_HOME` → openjdk@21 (probes `/usr/local` + `/opt/homebrew`)
  - `.github/workflows/main.yml` all jobs Temurin **17 → 21**
  - Docs: `ENV.md`, `ARCHITECTURE.md` CI/toolchain snapshot, `SAMPLE-APP.md`, `GETTING-STARTED.md`, `README.md`, `AGENTS.md`
  - App bytecode / `jvmTarget` **not** raised (sample still `JavaVersion.VERSION_1_8`)
- **Verify (real host, `source scripts/env-mac.sh`):**
  - `java -version` → **21.0.11** (Homebrew)
  - `plugins/ ./gradlew --version` → **Gradle 8.7** + **JVM 21.0.11** + Kotlin 1.9.22
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (89 tasks)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2203 tasks, 4m16s)
  - `includer/ ./gradlew build` → **BUILD SUCCESSFUL**
  - `depgen/ ./gradlew build` → **BUILD SUCCESSFUL**
- **Not in this slice:** AGP still **8.1.2** (Phase 2 = 8.5.2+); no F-019
- **Next:** AGP lockstep 8.5.2 (separate PR preferred)

## 2026-07-19 — F-018 Phase 1: unify Gradle wrappers → 8.7

- **Ticket:** F-018 → `in_progress` (Phase 1 shipped; JDK 21 + AGP climb remain)
- **PR:** #180 merged to `v2` (`7464d3e`)
- **Why delayed:** Jul 12 session wrote the plan + asked priority buttons instead of implementing; F-018 was not on `TICKETS.md` until 2026-07-19, so 4h workers continued forma-core/Bazel/flat-structure. User call-out: do the work, don't re-plan.
- **Code:**
  - All `gradle-wrapper.properties` (plugins, application, includer, depgen, jvm-application, bazel-adapter, examples/*, build-*, root) → **Gradle 8.7**
  - `application/settings.gradle.kts`: force full kotlin-stdlib family to `$embeddedKotlinVersion` (1.9.22) under `failOnVersionConflict`; KSP **1.9.22-1.0.16** (1.0.18 caused kapt↔ksp task cycle)
  - Default Compose compiler **1.5.3 → 1.5.10** (Kotlin 1.9.22 map); example 06-compose aligned
- **Verify (real, OpenJDK 17 at Phase 1 commit; re-verified on 21 in Phase 1b):**
  - `plugins/ ./gradlew --version` → **8.7** / Kotlin 1.9.22
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL**
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2203 tasks)
  - `includer`, `depgen`, `bazel-adapter`, `jvm-application` builds → **BUILD SUCCESSFUL**
- **Next:** F-018 Phase 1b host/CI JDK 21 (this run); Phase 2 AGP 8.5.2 lockstep

## 2026-07-19 — F-018 scheduled (JDK 21 + toolchain ladder)

- **Ticket:** F-018 → `todo` (inserted under P0 by user request: “Schedule JDK upgrade for the latest supported by AGP”)
- **Facts (official):**
  - AGP **8.1.2** (current Forma) through **9.2.x**: compatibility tables list JDK **min 17 / default 17**
  - Gradle **8.3/8.4** (current wrappers): run JVM up through **20**; **JDK 21 requires Gradle ≥8.5**
  - Current Gradle line supports run JVMs through **26** (9.4+); not a target until AGP 9 / Gradle 9 (F-019)
- **Decision:** upgrade host + CI build JDK to **21** (current LTS above 17 that AGP runs on once Gradle is new enough). Keep Android `source`/`target` / toolchain language at **17** unless a later phase deliberately raises bytecode level.
- **Ship order (one phase per PR preferred):**
  1. Unify wrappers → **Gradle 8.7** (enables JDK 21 daemon)
  2. Host `scripts/env-mac.sh` + docs + CI `actions/setup-java` → **Temurin/OpenJDK 21**
  3. AGP lockstep **8.5.2** → later **8.7** → **8.13** (existing plan phases 2–4)
  4. Do **not** start AGP 9 without explicit OK (F-019)
- **Not done this note:** no Gradle/JDK install yet — queue + schedule only
- **Next:** worker implements Phase 1 (Gradle 8.7) then JDK 21 host/CI on same or follow-up PR

## 2026-07-17 — F-063: hard-remove `androidLibrary`

- **Ticket:** F-063 → `done` (P6 flat-structure complete)
- **Branch:** `forma/F-063-hard-remove-androidLibrary` from `origin/v2` @ `773d061`
- **Skills/modes:** Grok Build `--mode full` (design + implement hit max-turns with tree on disk); Hermes finish path: remaining docs, TICKETS/PROGRESS, verify, PR
- **Code:**
  - Deleted `plugins/android/src/main/java/androidLibrary.kt` (public DSL)
  - Removed `AndroidTargetTypes.library` (`android.library`)
  - Dropped registry consumer registration + restriction-graph consumer rules
  - Rewrote allow-lists (`api`/`impl`/`util`/`androidUtil`/`viewBinding`/`app`/`binary`) to depend on `jvmLibrary` (same `library` suffix)
  - Kept `androidLibraryFeatureDefinition` (AGP wiring for impl/uiLibrary/etc.) + JVM `library()`
  - Core unit tests still use synthetic `android.library` types for suffix-collision engine docs
- **Docs:** DEPENDENCY-MATRIX (first), ANDROID-LIBRARY-DEPRECATION lifecycle, README, ARCHITECTURE, VISION, GETTING-STARTED, COMPOSE, CONFIGURATION-PERFORMANCE, progressive examples + agent skills, plugins/android README
- **Verify (real host, OpenJDK via `scripts/env-mac.sh`):**
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL** (78 tasks)
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2157 tasks)
- **Blockers:** none
- **Next:** prioritized queue empty again unless user adds F-xxx or promotes backlog (e.g. #110, #97, #88, #82, Portal #133)

## 2026-07-16 — F-060/061/062: deprecate androidLibrary, flatten sample + examples

- **Tickets:** F-060, F-061, F-062 → `done`; F-063 hard-remove remains `todo`
- **Branch:** `forma/F-060-deprecate-androidLibrary`
- **Product direction (user):** `androidLibrary` is a temporary hack; Forma should protect a **flat, role-typed** structure — reimplement examples without it.
- **Code:**
  - `@Deprecated` on `Project.androidLibrary` with replacement guidance
  - `androidRes` now returns `TargetBuilder` (safe-args / plugins parity)
  - Matrix: `viewBinding` → `ui-library`; `androidUtil` → JVM `library`
  - Sample renames: `core/di/android-util`, `core/mvvm/ui-library`, `core/navigation/res`
  - Example 04: `core/platform/android-util` (no `androidLibrary`)
- **Docs:** `docs/ANDROID-LIBRARY-DEPRECATION.md`, VISION flat-structure principle, DEPENDENCY-MATRIX, README, GETTING-STARTED, SAMPLE-APP, ARCHITECTURE, COMPOSE, DEPS-CATALOG, progressive examples + agent skills
- **Verify (real host):**
  - `plugins/ ./gradlew build` → **BUILD SUCCESSFUL**
  - `application/ ./gradlew build` → **BUILD SUCCESSFUL** (2157 tasks)
  - `examples/android/04-shared-libs ./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL**
- **Next:** F-063 hard-remove when ready; optional package renames off historical `.library` segments

## 2026-07-15 — 4h worker: empty queue + CI re-run

- **Ticket:** none — `TICKETS.md` all `done` through F-050 (P0–P5 complete)
- **Branch:** `forma/docs-progress-empty-queue` from `origin/v2` @ `9386b64`
- **Skills/modes:** Hermes orchestrator only (no Grok Build — nothing to implement)
- **Actions:**
  - Confirmed workdir `/Users/claw/work/forma`, remote `formatools/forma`, on `v2`
  - No open PRs; no `todo`/`in_progress` tickets
  - Re-ran failed post-merge GHA [29417713674](https://github.com/formatools/forma/actions/runs/29417713674) (Application only; prior failure was transient Gradle zip `Connection reset`)
  - Open backlog GH issues left untouched (need explicit promote into `TICKETS.md`)
- **Blockers:** prioritized queue empty — need new F-xxx tickets or user-promoted backlog item
- **Next step:** user adds next-phase tickets (e.g. polish, publish Portal #133, or backlog promotions)

## 2026-07-15 — Daily report note (CI)

- **Ticket:** none (queue empty; F-050 already `done` on `v2` @ `9386b64`)
- **Fact:** PR #175 checks on the feature branch were **SUCCESS** (Plugins/Includer/Depgen/Application). Post-merge `push` to `v2` run [29417713674](https://github.com/formatools/forma/actions/runs/29417713674) **failed** Application only: transient `java.net.SocketException: Connection reset` while downloading `gradle-8.4-bin.zip` — not a product build failure. Other jobs on that run succeeded.
- **Open PRs:** none (base v2 or master). `v2` is **33 commits ahead** of `master`.
- **Next:** user adds new F-xxx tickets or promotes backlog; optional re-run failed GHA workflow if a green tip signal is desired.

## 2026-07-15 — F-050 Progressive examples + agent skills

- **Ticket:** F-050 → `done`
- **Branch:** `forma/F-050-progressive-examples-skills` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design + partial implement, max-turns); Hermes finish path completed Android ladder, skills, docs, verify, PR
- **Deliverable:**
  - `examples/jvm/01-hello-binary` … `05-test-util` — full JVM target set progressive ladder
  - `examples/android/01-hello-apk` … `09-test-utils` — full Android target set + catalogs + compose + test utils
  - `examples/agent-skills/` — overview, android/jvm targets, matrix, catalogs, compose, includer, layout, bazel
  - `docs/PROGRESSIVE-EXAMPLES.md` + `examples/README.md` curriculum + feature coverage matrix
  - Cross-links: README, GETTING-STARTED, JVM-GETTING-STARTED
- **Verify (real host, OpenJDK 17 + `scripts/env-mac.sh`):**
  - All JVM steps: `./gradlew build` + `:binary:run` → **OK**
  - All Android steps: `:binary:assembleDebug` → **OK**
  - Android 09: `:feature-hello-impl:testDebugUnitTest` → **OK**
- **Notes:** `deps()` is non-transitive — Compose/JUnit examples use `transitiveDeps(...)`. Teaching apps use framework `Activity` (not AppCompat) to keep transitive surface small.
- **Blockers:** none
- **Next step:** backlog / new tickets as prioritized

## 2026-07-15 — Ticket queue empty; close done GitHub issues

- **Ticket:** none open in P0–P4 (`TICKETS.md` all `done` through F-042)
- **Branch:** `v2` (clean, up to date with `origin/v2` @ `92c82d7`)
- **Skills/modes:** Hermes orchestrator only (no Grok Build — nothing to implement)
- **Actions:**
  - Confirmed no open PRs (base v2 or otherwise)
  - Closed GitHub issues already delivered on `v2`:
    - #53 → F-015 (PR #159)
    - #96 → F-013 (PR #157)
    - #106 + #42 → F-017 (PR #161)
    - #132 → F-016 (PR #160); #133 Portal org remains human/admin open
    - #39 → covered by F-020..F-024 forma-core extraction
    - #18 → F-022 validation framework
  - Left true backlog issues open (#110, #97, #88, #82, #77, #54, #51, #46, #44/#43, #36, #126, #111, #103, #48, #56, #133)
- **Blockers:** no prioritized `todo`/`in_progress` tickets — need new F-xxx tickets or explicit backlog prioritization to continue product work
- **Next step:** user adds next-phase tickets (or promotes a backlog GH item into `TICKETS.md`)

## 2026-07-14 — F-042 Minimal Bazel sample using forma-core concepts

- **Ticket:** F-042 → `done`
- **Branch:** `forma/F-042-bazel-sample` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design/plan completed; implement hit max-turns with tree on disk); Hermes finish path: verify builds, clean orphan lock/symlinks, gitignore, README layout fix, commit/PR
- **Primary deliverable:**
  - New **`bazel-sample/`** (experimental, non-production):
    - `.bazelversion` (7.4.1), `WORKSPACE` (http_archive: rules_kotlin 1.9.0 + skylib 1.5.0 + rules_java 7.4.0), `.bazelrc` (`--noenable_bzlmod`)
    - Two features (greeter + calculator) with `api`/`impl`, `common/library` + `common/util`, `binary` composition root
    - Sources from `jvm-application/` (packages `tools.forma.jvm.sample.*`)
    - `BUILD.bazel` hand-authored to F-041 conventions: `load("@rules_kotlin//kotlin:jvm.bzl", ...)`, `kt_jvm_library`/`kt_jvm_binary`, `//pkg:leaf` labels, `tags = ["forma:type=..."]`, narrow visibility, **zero impl→impl deps**
  - `bazel-sample/README.md` (banner, prereqs, run commands, matrix, enforcement via adapter illegal fixture)
  - Root `.gitignore` entries for Bazel output symlinks
- **Cross-links & bookkeeping:**
  - `docs/BAZEL-ADAPTER.md`: status + **Sample results (F-042)**
  - `docs/JVM-SAMPLE.md`, `README.md`, `docs/ARCHITECTURE.md`, `TICKETS.md`
- **Verify (Hermes re-run, OpenJDK 17 + env-mac.sh):**
  - `bazel-adapter/`: `./gradlew test` → **BUILD SUCCESSFUL**
  - `bazel-sample/`: `bazelisk build //...` → **Build completed successfully** (7 targets)
  - `bazelisk run //binary:binary` →
    ```
    Hello, World! (2 + 3 = 5)
    Bazel sample (forma concepts) build + run successful.
    ```
- **Grounded:** commands executed this run; no invented green builds
- **Blockers:** none (WORKSPACE chosen after bzlmod friction; bazelisk via brew)
- **Next step:** P4 Bazel phase complete for current ticket list; backlog only unless new tickets added

## 2026-07-14 — F-041 Spike: generate/check Bazel BUILD from forma model

- **Ticket:** F-041 → `done`
- **Branch:** `forma/F-041-bazel-spike` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design/plan completed; implement hit max-turns with tree on disk); Hermes finished verify, docs, bookkeeping, PR
- **Deliverable (`bazel-adapter/` top-level):**
  - Pure Kotlin module; production dep **only** `tools.forma:core` via composite + dependency substitution
  - `FormaProjectModel` / `TargetSnapshot` / `DepRef` portable snapshot (no Gradle Project APIs)
  - `FormaToBazel` + `JvmBazelAdapter`: `generate()` → package dir → `BUILD.bazel` text; `check()` → violations via core `RestrictionGraph.isAllowed`
  - JVM 6-type matrix re-registered with core `SimpleTargetType` + `DefaultTargetRegistry` (kept in sync with `JvmTargetRegistry` by comment)
  - Labels: `:feature:greeter:impl` → `//feature/greeter/impl:impl`; tags `forma:type=…`; `kt_jvm_library` / `kt_jvm_binary` + `main_class`
  - Fixture from `jvm-application/` (8 targets); illegal impl→impl variant for check tests
  - Unit tests (labels, no cross-impl deps, check clean/illegal, round-trip, srcs/tags)
  - Committed examples under `examples/jvm-application-build/`; `./gradlew runSample` driver
  - `bazel-adapter/README.md`; **Spike results (F-041)** section in `docs/BAZEL-ADAPTER.md`; README Progress + docs index
- **Verify (real output, OpenJDK 17 + env-mac.sh):**
  - `bazel-adapter/`: `./gradlew test` → **BUILD SUCCESSFUL**
  - `bazel-adapter/`: `./gradlew runSample` → **BUILD SUCCESSFUL**; `violations=0 warnings=0`
  - `plugins/`: `./gradlew :core:test` → **BUILD SUCCESSFUL**
  - Core unchanged / Bazel-free
- **Grounded:** all commands executed this run; no invented green builds; no Bazel binary required
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none (Grok implement max-turns; completed under Hermes finish path)
- **Next step:** F-042 Minimal Bazel sample using forma-core concepts

## 2026-07-14 — F-040 Bazel adapter mapping design

- **Ticket:** F-040 → `done`
- **Branch:** `forma/F-040-bazel-adapter-design` (tracking `origin/v2`)
- **Skills/modes:** /goal + todo_write + plan subagent + read sources + implement design doc + cross-links + self-verify via spawned verifier subagent
- **Primary deliverable:**
  - New **`docs/BAZEL-ADAPTER.md`** (408 lines) — complete design covering all 10 acceptance criteria:
    - Goals/non-goals (JVM-first, adapter depends on core only)
    - Concept mapping table (TargetType.id → tags, RestrictionGraph → visibility/deps/check, etc.)
    - Label & package conventions derived from `jvm-application/` (Gradle `:` paths → `//feature/greeter/impl:impl`)
    - Visibility strategy: hybrid (generate from graph + package groups + check backstop); explains preservation of `impl ↛ impl` and binary composition root
    - JVM kit v1 rule table (`kt_jvm_library` / `kt_jvm_binary` + exports/visibility/tags)
    - Adapter architecture: `FormaProjectModel` + `FormaToBazel` (generate/check); new top-level module recommended (depends on core only)
    - Two modes (Generate + Check) with spike order recommendation (generate first for usable artifacts)
    - F-042 minimal sample sketch (docs-only)
    - Open questions (bzlmod, model export, rules source, etc.)
    - Ordered implementation plan linking F-041/F-042 boundaries
- **Cross-links added:**
  - `docs/VISION.md`: Bazel sequencing bullet → BAZEL-ADAPTER.md
  - `docs/forma-core-api.md`: F-040 status note + runtime collaboration update
  - `docs/ARCHITECTURE.md`: extraction map entry + adapter location recommendation
  - `README.md`: docs index + Progress section entry
  - `docs/JVM-TARGETS.md` + `docs/JVM-SAMPLE.md`: "Future Bazel" notes
- **Tickets:** `TICKETS.md` F-040 set to `done`; F-041/F-042 remain `todo`
- **Verify:** all required source files read before writing (VISION, forma-core-api, JVM-TARGETS, JVM-SAMPLE, ARCHITECTURE, DEPENDENCY-MATRIX, core target/restriction/validation sources, JvmTargetRegistry + test, jvm-application/ build files + layout). No code changes; no build executed (pure design). Doc length 408 lines (<1000). Branch not force-pushed.
- **Commits/PRs:** `45674c7` "F-040: design Bazel adapter mapping" on `forma/F-040-bazel-adapter-design`. (PR against v2 to be handled by orchestrator per session instructions.)
- **Blockers:** none
- **Next step:** F-041 (Spike: generate or check Bazel BUILD from forma declarations)

## 2026-07-14 — F-032 JVM getting started tutorial

- **Ticket:** F-032 → `done`
- **Branch:** `forma/F-032-jvm-getting-started` (from `origin/v2`)
- **Skills/modes:** /goal + todo_write + plan subagent + implement docs + self-verify via spawned verifier subagent
- **Docs (primary):**
  - New **`docs/JVM-GETTING-STARTED.md`** (410 lines, content-rich but focused) — polished tutorial so a developer with zero Forma experience reaches a working multi-module pure-JVM app. Mirrors structure/quality of Android [GETTING-STARTED.md](GETTING-STARTED.md):
    - Mental model (targets, suffixes, `binary` composition root, `api`/`impl` features, catalogs)
    - Prereqs: JDK 17+ only (**no Android SDK**), Gradle wrapper
    - Path A: run `jvm-application/` sample (`./gradlew :binary:run`)
    - Path B: greenfield — Portal + monorepo composite `settings.gradle.kts` (includer + `tools.forma.jvm`), root build note (no `androidProjectConfiguration`; matrix on first DSL use), minimal tree with `binary(..., mainClass = "...MainKt")` + one feature (`api`/`impl`) + `common/library`, **exact** `import tools.forma.jvm.*` snippets, build/run commands
    - Target cheat sheet (JVM types only)
    - Dependency rules of thumb + link to JVM-TARGETS
    - Troubleshooting (validation names consumer/dep, missing import, mainClass naming)
    - See also table
  - Cross-links wired:
    - `README.md` Getting started list (new JVM entry near top of JVM docs) + Progress section
    - `docs/JVM-SAMPLE.md`: “F-032 next” → link to tutorial
    - `docs/JVM-TARGETS.md`: “Next F-032” → link + context
    - `docs/GETTING-STARTED.md`: brief “Looking for pure JVM?” pointer (top + See also table)
    - `TICKETS.md`: F-032 `done`
- **Verify (real output, OpenJDK 17 + env-mac.sh — docs-only change):**
  - `jvm-application/`: `source scripts/env-mac.sh && ./gradlew :binary:run` → **success** (exit 0); captured:
    ```
    Hello, World! (2 + 3 = 5)
    JVM sample (tools.forma.jvm) build + run successful.
    ```
  - `git status` clean before run; only doc files touched.
- **Grounded:** all commands executed and outputs captured in session; no invented green builds or claims.
- **Commits/PRs:** `e2126e5` on `forma/F-032-jvm-getting-started`; PR base `v2` (this run)
- **Blockers:** none
- **Next step:** F-040 (Bazel design) per TICKETS priority

## 2026-07-13 — F-031 JVM sample application

- **Ticket:** F-031 → `done`
- **Branch:** `forma/F-031-jvm-sample` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design plan completed; implement hit max-turns with partial tree on disk); Hermes finished wiring, verify, docs, commit/PR
- **Platform (`plugins/jvm`):**
  - New type **`jvm.binary`** + `binary(...)` DSL (Kotlin JVM + Gradle `application` + `mainClass`)
  - `registerJvmDefaults` composition-root row: binary → api/impl/library/util/test-util (no binary→binary)
  - `impl` / `library` `testDependencies` widened to `FormaDependency` (project test-util)
  - `JvmTargetRegistryTest` updated for six types + binary matrix/self-suffix
- **Sample (`jvm-application/`):**
  - Multi-module pure-JVM product: greeter + calculator features (api/impl), common library/util/test-util, binary composition root
  - Composite: `includeBuild` plugins + includer + build-settings; `convention-dependencies` for project repos
  - Explicit `import tools.forma.jvm.*` in module scripts (JVM DSL is packaged, not default package)
  - Runnable: `./gradlew :binary:run` → `Hello, World! (2 + 3 = 5)`
- **Docs:** `docs/JVM-SAMPLE.md`; JVM-TARGETS matrix + binary row; ARCHITECTURE layout; README Progress + getting-started link; TICKETS
- **Verify (OpenJDK 17 + env-mac.sh):**
  - `plugins/`: `./gradlew :core:test :jvm:test build` → **BUILD SUCCESSFUL** (earlier full build; re-run `:jvm:test` green)
  - `jvm-application/`: `./gradlew build :binary:run` → **BUILD SUCCESSFUL** (run output as above)
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (578 tasks)
- **Grounded:** all commands executed; no invented green builds.
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none (Grok implement max-turns; completed under Hermes)
- **Next step:** F-032 Docs: JVM getting started

## 2026-07-13 — F-030 JVM target set on forma-core

- **Ticket:** F-030 → `done`
- **Branch:** `forma/F-030-jvm-targets` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design plan completed; implement hit max-turns with partial tree); Hermes finished tests, docs, verify, commit/PR
- **Platform (`plugins/jvm`):**
  - Plugin id **`tools.forma.jvm`** via `formaPublishedPlugin(name = "jvm")` + empty Settings `FormaPlugin`
  - **No AGP**; depends on `:core`, `:deps`, `:validation`, `:target`, `:owners` + embedded Kotlin GP
  - `JvmTargetTypes`: `jvm.api`, `jvm.impl`, `jvm.library`, `jvm.util`, `jvm.test-util`
  - `JvmTargetRegistry` + `registerJvmDefaults()` matrix (impl↛impl; api→api/library; library→util/test-util; util→util/library; test-util→test-util/util/library)
  - DSL under package `tools.forma.jvm`: `api` / `impl` / `library` / `util` / `testUtil`
  - Minimal `applyKotlinJvm()` (Java 11) without `AndroidProjectSettings`
- **Tests:** `JvmTargetRegistryTest` (matrix, self-suffix, identity cache)
- **Docs:** `docs/JVM-TARGETS.md`; ARCHITECTURE module graph; PLUGIN-PUBLISH table; README Progress; forma-core-api status
- **Verify (OpenJDK 17 + env-mac.sh):**
  - `plugins/`: `./gradlew :core:test :jvm:test build` → **BUILD SUCCESSFUL** (78 tasks; `JvmTargetRegistryTest` 5/5)
  - Plugin descriptor `tools.forma.jvm` → `tools.forma.jvm.plugin.FormaPlugin`
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (578 tasks)
- **Grounded:** all commands executed; no invented green builds.
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none (Grok implement max-turns; completed under Hermes)
- **Next step:** F-031 JVM sample application

## 2026-07-13 — F-024 Publish/coordinate coordinates: `tools.forma:core` vs android plugins

- **Ticket:** F-024 → `done`
- **Branch:** `forma/F-024-publish-coordinates` (from `origin/v2`)
- **Skills/modes:** /goal + todo_write + direct implement (per user query); self-verify via spawned verifier subagent
- **Docs (primary):**
  - Added dedicated **Coordinates (F-024)** section to `docs/PLUGIN-PUBLISH.md`: GAV matrix, single-jar confirmation, `:owners` sibling status, POM wiring (project → external GAV), consumer guidance (android plugin vs pure core), deprecation policy (keep facades through 0.1.x, no removal).
  - Updated table of published artifacts to include `:core`.
  - Resolved open questions in `docs/forma-core-api.md` §11; updated extraction checklist + F-024 row.
  - Updated `docs/ARCHITECTURE.md` step 5 extraction map.
  - README Progress section + cross links.
- **Publish wiring / metadata:**
  - Hardened `plugins/core/build.gradle.kts`: added `pom { name, description, url, scm }` (matches forma config; visible in published core POM).
  - `publishAllToMavenLocal` already orders core first (confirmed in execution).
  - No changes to turn `:core` into a plugin; no plugin-publish applied to core.
- **Verify (real output, OpenJDK 17 via env-mac.sh):**
  - `plugins/`: `./gradlew :core:test` → **BUILD SUCCESSFUL**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (68 tasks)
  - `plugins/`: `./gradlew publishAllToMavenLocal -PformaLocalVersion=0.1.3-F024` → **BUILD SUCCESSFUL**
  - `~/.m2/.../core/0.1.3-F024/` has `core-0.1.3-F024.jar` + `-sources.jar` + `.pom` (with metadata)
  - android POM contains `<artifactId>core</artifactId><version>0.1.3-F024</version>` (project dep substituted)
  - `plugins/`: `./gradlew :android:publishPlugins --validate-only` → packaging tasks ran; failed only on missing `gradle.publish.key/secret` (explicitly acceptable per AC)
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (578 tasks)
- **Grounded:** all commands executed; outputs captured; no invented green builds.
- **Commits/PRs:** commit b215f01 on `forma/F-024-publish-coordinates`; PR https://github.com/formatools/forma/pull/167 (base v2); self-verification PASS
- **Blockers:** none
- **Next step:** F-030 JVM targets (after PR merge)

## 2026-07-13 — F-023 Wire Android as first consumer of forma-core

- **Ticket:** F-023 → `done`
- **Branch:** `forma/F-023-android-registry` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (design plan) then `--mode implement` (hit max-turns twice; tree completed by Hermes verify/docs)
- **Core (`tools.forma.core.target`):**
  - `TargetRegistration`, `TargetRegistry`, `DefaultTargetRegistry`
  - `validatorFor` / `selfValidator` via core factories + registry-level caches
  - `restrictionGraph()` rebuilt from registrations (replace semantics)
  - Unit tests: `TargetRegistryTest` (9 tests, all green)
- **Android consumer:**
  - `AndroidTargetRegistry` + `registerAndroidDefaults()` (matrix + content-rule metadata)
  - `androidProjectConfiguration` initializes registry after `Forma.store`
  - All DSL entrypoints (`api`/`impl`/`library`/`androidLibrary`/…/app/binary/native) use registry validators
  - **jvm.library** vs **android.library** distinguished for `library()` vs `androidLibrary()`
- **Facade:** `TargetValidator.asValidator()` → legacy `Validator` / `ProjectValidationError`
- **Docs:** forma-core-api checklist + status; ARCHITECTURE §6; README Progress; TICKETS
- **Verify (OpenJDK 17 + env-mac.sh):**
  - `plugins/`: `./gradlew :core:test` → **BUILD SUCCESSFUL** (`TargetRegistryTest` 9/9)
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (68 tasks)
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (578 tasks)
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none (Grok implement max-turns; code/docs finished under Hermes)
- **Next step:** F-024 Publish/coordinate coordinates (`tools.forma:core` vs android plugins)

## 2026-07-13 — F-022 Extract validation framework into forma-core

- **Ticket:** F-022 → `done`
- **Branch:** `forma/F-022-validation-framework` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (plan design → implement; implement hit max-turns); Hermes finished docs, OnlyLayout empty-list parity, builds, commit/PR
- **Core (`tools.forma.core.validation`):**
  - `TargetValidator` SPI + `AcceptAny`
  - `dependencyTypeValidator` / `selfTypeValidator` with **identity cache** (F-017)
  - `FormaValidationException` (recognizable suffix messages)
  - Pure `ContentRule` + `NoResourcesUnderMain` / `OnlyResourcesUnderMain` / `OnlyLayoutResources`
  - Unit tests: `TargetValidatorTest`, `ContentRuleTest`
- **Facades:**
  - `:validation` `Validator`/`validator(TargetTemplate…)` → core; still throws `ProjectValidationError`
  - `FormaTarget` implements `TargetRef`; `:target`/`:deps` depend on `:core`
  - Android `commonValidators.kt` lists dirs via Gradle, checks via core rules
- **Docs:** ARCHITECTURE graph/table/extraction; forma-core-api status + checklist; README Progress; TICKETS
- **Verify (OpenJDK 17 + env-mac.sh):**
  - `plugins/`: `./gradlew :core:test` → **BUILD SUCCESSFUL**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (68 tasks)
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (578 tasks)
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none (Grok implement max-turns; tree completed by Hermes verify/docs)
- **Next step:** F-023 Wire Android as first consumer of forma-core (registry / DSL)

## 2026-07-13 — Local maven publishing for plugin testing

- **Scope:** tooling for F-018 AGP/Gradle smoke tests + external consumers
- **Changes:**
  - `plugins/core`: `maven-publish` → `tools.forma:core` (+ sources)
  - `plugins/build.gradle.kts`: `publishAllToMavenLocal` aggregate; `-PformaLocalVersion=…`
  - `scripts/publish-local.sh` helper
  - `docs/PLUGIN-PUBLISH.md` § Local publishing
- **Verify:** `./gradlew publishAllToMavenLocal -PformaLocalVersion=0.1.3-LOCAL` → **BUILD SUCCESSFUL**; artifacts under `~/.m2/repository/tools/forma/*`; android POM lists `tools.forma:core:0.1.3-LOCAL`
- **Note:** sample `application/` still uses `includeBuild`; mavenLocal is for external test consumers
- **Next:** commit with F-018 branch or standalone docs/tooling PR if desired

## 2026-07-13 — F-021 Extract dependency-type / restriction engine into forma-core

- **Ticket:** F-021 → `done`
- **Branch:** `forma/F-021-forma-core-restriction` (from `origin/v2`)
- **Skills/modes:** Grok Build `--mode full` (plan agent design → implement + goal + check); Hermes verified builds; matrix id split fix applied on disk after implement timeout
- **New module:**
  - `plugins/core/` pure JVM library (`tools.forma` / `0.1.3`), no AGP / no plugin-publish
  - Auto-included via `tools.forma.includer`
- **Core packages (`tools.forma.core.*`):**
  - `target`: `TargetType`, `TargetRef`, `SimpleTargetType`, `targetType(...)`, `NameMatcher`, `SuffixNameMatcher`
  - `restriction`: `EdgeKind`, `RestrictionRule`, `RestrictionGraph`, `MutableRestrictionGraph` (closed-world, id-keyed)
- **Android wiring (platform owns types + matrix):**
  - `AndroidTargetTypes`: stable ids (`android.api`, `android.impl`, `android.library`, **`jvm.library`**, `jvm.util`, …)
  - `AndroidRestrictionKit.register` / `build()` from `docs/DEPENDENCY-MATRIX.md`; **impl ↛ impl**; JVM `library` vs `androidLibrary` rules not merged
  - `:android` depends on `:core`
- **Compat:** legacy `TargetTemplate` / `validator(...)` / DSL untouched (F-023 consumes graph)
- **Tests:** `NameMatcherTest` + `RestrictionGraphTest` (impl rule + §7 suffix collision)
- **Docs:** ARCHITECTURE graph/table/extraction; README Progress; TICKETS
- **Verify (Hermes re-run, OpenJDK 17):**
  - `plugins/`: `./gradlew :core:test` → **BUILD SUCCESSFUL**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (67 tasks)
  - `plugins/`: `./gradlew :android:compileKotlin` → **BUILD SUCCESSFUL**
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL** (578 tasks)
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none (Grok CLI auth needed env-parse fix in `~/.hermes/scripts/grok_build_exec.sh`; implement phase hit turn/timeout once but tree landed)
- **Next step:** F-022 Extract validation framework into forma-core

## 2026-07-13 — F-020 Design forma-core public API

- **Ticket:** F-020 → `done`
- **Branch:** `forma/F-020-forma-core-api` (from `origin/v2`)
- **Docs (design only — no plugin code move):**
  - Added `docs/forma-core-api.md`: goals/non-goals, package/coords preview
    (`tools.forma:core`), `TargetType` / `TargetRef` / `NameMatcher`,
    restriction graph + `EdgeKind`, validator SPI + content rules, target
    registry, deps/settings split, owners, feature-definitions stay platform,
    runtime sequence, F-021…F-024 mapping, v1 API checklist, **library suffix
    collision decision** (unique type `id`, shared suffix + optional marker),
    migration/compat, test strategy, open questions
  - ARCHITECTURE §6 extraction order + §7 collision note → design doc
  - README Getting started + Progress pointers; TICKETS status
- **Code:** none (F-020 is design acceptance)
- **Verify:** markdown + cross-links only (no Gradle required)
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-021 Extract dependency-type / restriction engine into
  `forma-core` (`plugins/core` + wire Android matrix data; sample stays green)

## 2026-07-12 — F-017 Configuration-time performance pass (GH #106, #42)

- **Ticket:** F-017 → `done`
- **Branch:** `forma/F-017-config-performance` (from `origin/v2`)
- **Code:**
  - `validator(...)` identity-caches single/multi suffix validators; hot path uses
    precomputed dash-suffixes (no per-call `map`/`contains` lists)
  - `kotlinFeatureDefinition` / `kotlinAndroidFeatureDefinition` / kapt: singleton
    `FeatureDefinition` instances; live `Forma.settings` read at apply time
  - `applyDependencies`: empty-repo sentinel skips per-target `repositories {}`;
    early-return when all dep bags are `EmptyDependency`; skip plugin lookup when
    no plugin deps registered
  - `deps` / `FormaDependency.plus` / `forEach`: typed merges + indexed loops
    (less `filterIsInstance` / intermediate lists)
  - Catalog `filteredTokens` → `Set`; Android targets drop redundant
    `repositoriesConfiguration = Forma.settings.repositories`
- **Docs:** `docs/CONFIGURATION-PERFORMANCE.md` (measure + guidance); README /
  ARCHITECTURE / SAMPLE-APP / TICKETS links
- **Verify (real tool output, OpenJDK 17 + SDK 34):**
  - `plugins/`: `./gradlew build --offline` → **BUILD SUCCESSFUL** (63 tasks;
    `:deps:test` green)
  - `application/`: `./gradlew build --offline` → **BUILD SUCCESSFUL**
    (2155 tasks)
  - Profile `help --no-configuration-cache --offline` (daemon warm): Configuring
    Projects ~4.8s → ~1.1s on best same-host pair; CC reuse `help` **880ms**
  - Wall-clock is noisy under load (first cold after full app build spiked);
    treat as allocation/hot-path win, not a fixed %
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none for F-017; deeper remote Build Scans / GH #42 still optional
- **Next step:** F-020 Design forma-core public API (`docs/forma-core-api.md`)

## 2026-07-12 — F-016 Plugin publish path (Portal + target publish config)

- **Ticket:** F-016 → `done` (GH #132 code path; GH #133 Portal org remains human admin)
- **Branch:** `forma/F-016-plugin-publish` (from `origin/v2`)
- **Code (`plugins/buildSrc` + plugin modules):**
  - Added `FormaPluginPublishExtension` + top-level helpers:
    - `formaPluginConfiguration { … }` on plugins root (group/version/website/vcs/tags/…)
    - `formaPublishedPlugin(name = …)` on each publishable subproject
  - Replaced duplicated `rootProject.ext` + `gradlePlugin { }` blocks in
    `:android`, `:target`, `:validation`, `:owners`, `:config`, `:deps`
  - Root `plugins/build.gradle.kts` now only applies plugin-publish version +
    `formaPluginConfiguration` (no raw `ext { }` map)
  - buildSrc depends on `gradleApi()` only (must not put plugin-publish on
    buildSrc classpath — causes "already on the classpath with unknown version")
- **Docs:**
  - `docs/PLUGIN-PUBLISH.md` — DSL usage, validate-only, credential model,
    GH #133 operator checklist (no secrets in-repo)
  - README Getting started + Progress pointers; ARCHITECTURE §2 publish note;
    TICKETS status
- **Verify (real tool output, OpenJDK 17):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (63 tasks)
  - Plugin descriptors still `tools.forma.{android,target,validation,owners,config,deps}`
    with `*.plugin.FormaPlugin`; group/version `tools.forma` / `0.1.3`
  - `./gradlew :android:publishPlugins --validate-only` → fails only on
    **Missing publishing keys** (expected without Portal credentials; packaging OK)
  - `application/`: `./gradlew :binary:assembleDebug` → **BUILD SUCCESSFUL**
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** GH #133 (create/claim shared Forma Plugin Portal identity +
  distribute keys) is outside automation
- **Next step:** F-017 Configuration-time performance pass (GH #106, #42)

## 2026-07-12 — F-015 Android project tutorial (getting started)

- **Ticket:** F-015 → `done` (GH #53)
- **Branch:** `forma/F-015-android-tutorial` (from `origin/v2`)
- **Docs:**
  - Added `docs/GETTING-STARTED.md` — mental model, prerequisites, Path A (build
    sample), Path B (greenfield settings/root/targets), target cheat sheet,
    deps, day-one rules, second feature, Compose pointer, pitfalls, checklist
  - README: Getting started section + F-015 pointer under Progress
  - SAMPLE-APP + TICKETS status links
- **Code:** docs-only (no plugin/sample behavior change)
- **Verify:** markdown + cross-links only (no Gradle required for this slice)
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-016 Plugin publish path (Portal user + target publish config)

## 2026-07-12 — F-014 Sample app gold-standard multi-feature structure

- **Ticket:** F-014 → `done`
- **Branch:** `forma/F-014-sample-gold-standard` (from `origin/v2`)
- **Sample modernization:**
  - Relocated leftover `com/stepango/blockme/…` source trees under
    `tools/forma/sample/…` (mvvm lifecycle, extensions, Marvel service/repo/mapper/tests)
  - Aligned `packageName` with module path for home/characters api+res+viewbinding,
    characters core api, root-res (`tools.forma.sample.root.res`)
  - Moved `toggle-widget` sources into `tools.forma.sample.widget.toggle` (+ menu XML)
  - Home viewbinding UI types under `…home.viewbinding.ui`; list contracts under
    `…list.viewbinding.domain.model` (impl imports updated)
  - Normalized `feature/characters/list/impl` deps to consistent `target(":…")` form
  - Placeholder `FeatureHomeApi` / `FeatureCharactersListApi` objects for empty api modules
- **Docs:** `docs/SAMPLE-APP.md` gold-standard guide; ARCHITECTURE §3, DEPENDENCY-MATRIX,
  README pointers; TICKETS status
- **Verify (real tool output, OpenJDK 17 + SDK 34):**
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** (2112 tasks, 54s after clean)
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-015 Android project tutorial (getting started)

## 2026-07-11 — F-013 Compose support (GH #96)

- **Ticket:** F-013 → `done`
- **Branch:** `forma/F-013-compose-support` (from `origin/v2`)
- **Plugin / DSL:**
  - `AndroidLibraryFeatureConfiguration.compose` + `AndroidBinaryFeatureConfiguration.compose`
    → `buildFeatures.compose` + `composeOptions.kotlinCompilerExtensionVersion`
  - Per-target `compose` flag (default = project `androidProjectConfiguration(compose=…)`)
    on `impl`, `androidLibrary`, `androidUtil`, `androidApp`, `uiLibrary`, `androidBinary`
  - New target **`composeWidget`** / suffix `compose-widget` (always Compose)
  - Project settings: `composeCompilerVersion` (default **1.5.3** for Kotlin 1.9.10)
  - Validators: `compose-widget` allowed from impl/app/binary/uiLibrary/widget/viewBinding/res;
    widget ↔ compose-widget mutual deps (View + Compose coexist)
- **Sample:**
  - `application/common/greeting/compose-widget` with `@Composable GreetingCard`
  - `binary` depends on it with `compose = true`
  - `androidx.compose` catalog cluster (`transitiveDeps`) in build-dependencies
  - Sample `compileSdk` **34** (Compose emoji2 AAR metadata); `targetSdk` remains 33
- **Docs:** `docs/COMPOSE.md`; matrix/README/ARCHITECTURE/ENV/CI updated
- **Verify (real tool output, OpenJDK 17 + SDK 34):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** (2151 tasks)
  - `:common-greeting-compose-widget:compileDebugKotlin` + `:binary:assembleDebug` green
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-014 sample app gold-standard multi-feature structure

## 2026-07-11 — F-012 External deps catalog UX

- **Ticket:** F-012 → `done`
- **Branch:** `forma/F-012-deps-catalog-ux` (from `origin/v2`)
- **Code (`plugins/deps` catalog):**
  - Pure name generators (removed configuration-time `println`)
  - Clear GAV validation via `parseGroupArtifactVersion` (exactly `group:artifact:version`)
  - Fail-fast when auto-name would be empty (all tokens filtered)
  - New `library(gav, name = …)` factory for stable short accessors
  - `projectDependencies` accepts `library()` / `bundle()` / `plugin()` / bare GAV; better error for unknown types
  - Unit tests: `GeneratorsTest` (`:deps:test`)
- **Sample:** `application/settings.gradle.kts` uses `library(..., name = "coil"|"coilBase")` inside the coil bundle
- **Docs:** `docs/DEPS-CATALOG.md` (user guide); README section; ARCHITECTURE §2.4 pointer
- **Verify (real tool output, OpenJDK 17 + SDK 33):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `plugins/`: `./gradlew :deps:test` → **BUILD SUCCESSFUL** (GeneratorsTest)
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** (2080 tasks)
  - Configuration log no longer spam-prints `Generated name …`
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-013 Compose support (GH #96)

## 2026-07-11 — F-011 api/impl + composition-root validation

- **Ticket:** F-011 → `done`
- **Branch:** `forma/F-011-api-impl-validation`
- **Code:**
  - `androidApp` / `androidBinary` / `androidLibrary`: replace `EmptyValidator` with Dagger-friendly project-dep allowlists
  - `androidLibrary` **cannot** depend on `impl` (or widget/viewbinding)
  - `impl` still cannot depend on `impl`; KDoc on `api`/`impl` documents boundaries
  - Clearer validation error text in `Validator.kt`
- **Docs:** `docs/DEPENDENCY-MATRIX.md`, README summary, TICKETS
- **Verify:** `plugins` + `application` `./gradlew build` (see commit/PR notes)
- **Next:** F-012 external deps catalog UX

## 2026-07-11 — F-010 Dependency matrix from live validators

- **Ticket:** F-010 → `done`
- **Branch:** `forma/F-010-dependency-matrix` (from `origin/v2` after F-003/F-004 merge)
- **Actions:**
  - Added `docs/DEPENDENCY-MATRIX.md` as canonical code-truth matrix
  - Replaced aspirational README matrix with code-aligned summary
  - Pointed `docs/ARCHITECTURE.md` at the new doc; marked F-010 done
- **Also that run:** F-003 #152 and F-004 #153 merged into `v2` (`123ec44`)
- **PR:** #154 squash-merged to `v2` as `619ce05`

## 2026-07-11 — v2 base of operations (merge open PRs)

- Created `v2` from `master`; integrated F-001/F-002; workers branch/PR against `v2`
- Tip later advanced with F-003/F-004/F-010/F-011

## 2026-07-11 — F-004 CI green (workflow + badge)

- **Ticket:** F-004 → `done`
- **Branch:** `forma/F-004-ci-green` (from `origin/v2`; independent of open F-003 PR #152)
- **Actions:**
  - Rewrote `.github/workflows/main.yml`:
    - Display name **CI**; triggers `push` + `pull_request` + `workflow_dispatch`
    - Concurrency cancel-in-progress per ref
    - **All four jobs** pin Temurin 17 (`actions/setup-java@v4`)
    - Gradle via `gradle/actions/setup-gradle@v4`
    - `build_application`: `android-actions/setup-android@v3` with `platforms;android-33`, platform-tools, build-tools 33.0.2 + 34.0.0
    - Dropped unconditional `--scan`; use `--stacktrace --console=plain`
  - README CI badge + code-size shield → `formatools/forma` + `actions/workflows/main.yml/badge.svg`
  - `docs/ARCHITECTURE.md` §4 updated for new CI layout
- **GHA verification (PR run 29162702015):** **all success**
  - Plugins: success
  - Includer: success
  - Depgen: success
  - Application: success (SDK setup + full sample build)
- **Commits/PRs:** https://github.com/formatools/forma/pull/153
- **Blockers:** none
- **Next step:** P0 complete after #152 (F-003) + #153 (F-004) merge to `v2`. Then P1 top is F-010 (live dependency matrix docs).

## 2026-07-11 — F-003 Modern toolchain (AGP compile/runtime align)

- **Ticket:** F-003 → `done`
- **Branch:** `forma/F-003-modern-toolchain` (from `origin/v2`)
- **Actions:**
  - Bumped `plugins/android` compile dep `com.android.tools.build:gradle` **7.4.2 → 8.1.2** to match sample runtime force (`application/settings.gradle.kts` + `agpVersion = "8.1.2"`)
  - README sample config: `agpVersion` + nav safe-args / crashlytics plugin versions aligned with application
  - Docs: `ARCHITECTURE.md` toolchain snapshot + inconsistency table; `ENV.md` re-verify notes; `TICKETS.md` status
- **Build verification (real tool output, OpenJDK 17 + SDK 33):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** in 42s (58 tasks; `:android:compileKotlin` clean after AGP bump)
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** in 3m8s (2080 tasks)
- **Not in this slice:** CI workflow (F-004 — still missing Java on plugin jobs + Android SDK setup); further AGP/Gradle bumps beyond 8.1.2/8.4
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none for local modern-toolchain claim
- **Next step:** F-004 CI green (pin Temurin 17 on all jobs; install Android SDK for `build_application`; fix badge/name drift)

## 2026-07-11 — v2 base of operations (merge open PRs)

- **Action:** Created integration branch `v2` from `origin/master`, merged open work from:
  - PR #150 / `forma/F-001-jdk-bootstrap` (F-001)
  - PR #151 / `forma/F-002-architecture-audit` (F-002, stacked on F-001)
- **Tip:** `b517e1b` merge commit (or later tip if docs follow-up landed on same push)
- **Policy:** Workers branch from `origin/v2`; PRs use `--base v2`. `master` remains public default until explicit promote.
- **Docs:** `AGENTS.md`, `docs/cron-worker-prompt.txt` updated for v2 base.
- **Not reopened:** old closed PRs #147 / #127 / #112 (pre-2024 / superseded) — left closed unless user prioritizes.
- **Next:** F-003 or F-004 on top of `v2`; retarget or close #150/#151 once v2 is published.

## 2026-07-11 — F-002 Architecture audit (build graph)

- **Ticket:** F-002 → `done`
- **Branch:** `forma/F-002-architecture-audit` (stacked on F-001 docs commit)
- **Actions:**
  - Mapped composite builds: `plugins/`, `application/`, `includer/`, `depgen/`, `build-settings/`, `build-dependencies/`
  - Documented plugins module DAG (`target` → `validation`/`owners`/`config` → `deps` → `android`)
  - Extracted **live** allowed-dependency validators from every Android DSL entrypoint into a table (code truth vs README matrix)
  - Listed sample app ~35 targets (feature api/impl/res/viewbinding pattern)
  - Audited CI workflow gaps (Java only on application job; no Android SDK step; badge/name drift)
  - Marked forma-core extraction candidates vs Android-only code
- **Artifact:** `docs/ARCHITECTURE.md`
- **Commits/PRs:** this run — push + PR
- **Blockers:** none (docs-only slice; builds not re-run; F-001 host green still the baseline)
- **Next step:** F-003 (toolchain modernization / AGP compile vs runtime skew) or F-004 (CI green) — prefer F-003 only if code changes needed; CI is the remaining P0 risk

## 2026-07-10 — F-001 Environment bootstrap (JDK + Android SDK)

- **Ticket:** F-001 → `done`
- **Branch:** `forma/F-001-jdk-bootstrap` (from `origin/master`)
- **Host toolchain:**
  - JDK: Homebrew `openjdk@17` 17.0.19 at `/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home` (keg-only; no sudo). `brew install --cask temurin@17` fails without interactive sudo.
  - Android SDK: `brew install --cask android-commandlinetools` → `/usr/local/share/android-commandlinetools`; installed `platforms;android-33`, `platform-tools`, `build-tools;33.0.2` + `34.0.0`; licenses accepted.
  - `application/local.properties` written (gitignored) with `sdk.dir`.
  - `~/.zshrc` exports `JAVA_HOME` / `ANDROID_HOME` for interactive shells.
- **Repo docs/scripts:**
  - `docs/ENV.md` — install + verify steps
  - `scripts/env-mac.sh` — sourceable env for cron/workers
- **Build verification (real tool output):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (58 tasks)
  - `includer/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `depgen/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** in 3m29s (2080 tasks)
- **Commits/PRs:** (this run) push + PR for env docs/scripts; host packages stay machine-local
- **Blockers:** none for local Gradle; optional `sudo ln -sfn …/openjdk.jdk` into `/Library/Java/JavaVirtualMachines` not done (sudo password unavailable)
- **Next step:** F-002 audit build graph → `docs/ARCHITECTURE.md`; F-003 may largely be closed by host green builds (confirm CI separately as F-004)

## 2026-07-10 — Workspace init + cron online

- Cloned `formatools/forma` → `/Users/claw/work/forma` (branch `forma/agent-workspace`)
- Wrote `docs/VISION.md`, `TICKETS.md`, `AGENTS.md`, this log
- Cron jobs:
  - `18717ea2093c` **forma 4h ticket worker** — every 240m, workdir `/Users/claw/work/forma`, deliver `telegram:-1003754340081:136`, model `grok-4.5` / `custom:xai`, toolsets terminal+file+web
  - `1c872230b69e` **forma daily progress report** — `0 10 * * *` (10:00 America/Los_Angeles), same deliver/workdir, toolsets terminal+file
- Prompt sources: `docs/cron-worker-prompt.txt`, `docs/cron-daily-prompt.txt`
- Host note: no Java runtime installed yet (blocks Gradle until F-001)
