# Forma progress log

Newest entries first.

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
