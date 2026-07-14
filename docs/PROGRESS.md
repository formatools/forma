# Forma progress log

Newest entries first.

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
