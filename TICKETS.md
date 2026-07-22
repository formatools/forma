# Forma prioritized tickets

Status legend: `todo` | `in_progress` | `blocked` | `done`

Update this file when picking or finishing work. Cron workers must pick the **highest priority open ticket** that is not blocked.

## Root goals (implementation bar)

Every open ticket below must move the product toward:

1. **Bazel-like rules** — configure once on the type; call sites = minimal static attrs; type behavior auto-applies.
2. **One global way** — single supported approach per concern, project-wide (scale to large orgs).
3. **Explicit structure + tooling** — declare boundaries openly; invest in check/generate/migrate so fleet changes stay reliable.

Canonical write-up: `docs/VISION.md` § Root principles. Plugins design: `docs/TARGET-PLUGINS.md`.

## P0 — Bootstrap (Android product baseline)

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-001 | done | Environment bootstrap: JDK + Android SDK tooling on worker host | OpenJDK 17 + cmdline-tools; see `docs/ENV.md`, `scripts/env-mac.sh`. plugins/app/includer/depgen build green on host |
| F-002 | done | Audit build graph: plugins, sample app, CI workflows | Map modules → forma-core candidates; capture in `docs/ARCHITECTURE.md` |
| F-003 | done | Get plugins + sample `application/` building on modern toolchain | Plugins compile AGP 8.1.2 matches sample; Gradle 8.3 (plugins) / 8.4 (app); host builds green |
| F-004 | done | CI green on GitHub Actions for plugins + application | Temurin 17 all jobs + Android SDK 33 for app; PR #153 GHA green |
| F-018 | done | JDK 21 + Gradle/AGP staged modernization | **Shipped on `v2`:** Gradle **8.14.5**, AGP **8.13.2**, Kotlin **2.0.21**, KSP **2.0.21-1.0.28**, Compose **1.9.4**/compiler **2.0.21**, JDK **21** host/CI, sample SDK min23/target35/compile35 + deps at AGP-8.13 ceiling. PRs **#179–#182**. Superseded by **F-019** for 9.x. |
| F-019 | done | Gradle 9 + Kotlin 2.3 + AGP 9 toolchain | Phase 1 done (9.6.1 / 2.3.21 / AGP 9.3.0). **F-086** = kapt→KSP + built-in Kotlin. AndroidX ceiling → **F-087**. |
| F-086 | done | Migrate kapt → KSP + AGP built-in Kotlin | First-class `Ksp` + `String.ksp`; `processorConfigurationFeatures()`; sample Dagger on **ksp**; drop F-019 kapt bridge flags; legacy `.kapt` kept until **F-093**. |

## P1 — Android working product

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-010 | done | Document strict dependency matrix from live code (not only README) | `docs/DEPENDENCY-MATRIX.md` + README summary aligned to validators |
| F-011 | done | Tighten validation for `api` / `impl` (Dagger2-friendly boundaries) | app/binary/androidLibrary no longer EmptyValidator; impl still no→impl; docs matrix updated |
| F-012 | done | External deps catalog UX + tooling polish | `library()` + pure generators + GAV validation + unit tests + `docs/DEPS-CATALOG.md` |
| F-013 | done | Compose support for Android library/ui targets | GH #96; `compose` flags + `composeWidget`; `docs/COMPOSE.md` |
| F-014 | done | Sample app: gold-standard multi-feature structure | packageName + source-root alignment; `docs/SAMPLE-APP.md`; home/characters pattern |
| F-015 | done | Android project tutorial (getting started) | GH #53; `docs/GETTING-STARTED.md` + README entry |
| F-016 | done | Plugin publish path (Portal user + target publish config) | GH #132 done (`formaPluginConfiguration`/`formaPublishedPlugin`); GH #133 Portal org is human admin — see `docs/PLUGIN-PUBLISH.md` |
| F-017 | done | Configuration-time performance pass | GH #106, #42; validator/feature caches + lean deps; `docs/CONFIGURATION-PERFORMANCE.md` |

## P2 — forma-core extraction

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-020 | done | Design forma-core public API (types, restrictions, validation, target registry) | `docs/forma-core-api.md` — types, restriction graph, validator SPI, registry, coords, library-suffix decision |
| F-021 | done | Extract dependency-type / restriction engine into `forma-core` | `plugins/core` + TargetType/NameMatcher/RestrictionGraph + AndroidTargetTypes + AndroidRestrictionKit (distinct jvm.library vs android.library); facades preserved. Related GH #39 |
| F-022 | done | Extract validation framework into `forma-core` | `plugins/core` TargetValidator + ContentRule; `:validation` facade; Android helpers call pure rules |
| F-023 | done | Wire Android implementation as first consumer of forma-core | TargetRegistry + AndroidTargetRegistry; DSL uses registry validators; sample green |
| F-024 | done | Publish/coordinate coordinates: `tools.forma:core` vs android plugins | Coordinates, docs, mavenLocal + validate, sample green |

## P3 — JVM applications

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-030 | done | JVM target set on forma-core (`library`, `api`, `impl`, `utils`, tests) | `plugins/jvm` + `tools.forma.jvm`; `docs/JVM-TARGETS.md` |
| F-031 | done | JVM sample application | `jvm-application/` + `binary` DSL; docs/JVM-SAMPLE.md |
| F-032 | done | Docs: JVM getting started | `docs/JVM-GETTING-STARTED.md` + cross-links; see PROGRESS |

## P4 — Bazel

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-040 | done | Design Bazel adapter mapping (targets ↔ rules, visibility ↔ deps) | `docs/BAZEL-ADAPTER.md` + cross-links; commit 45674c7 |
| F-041 | done | Spike: generate or check Bazel BUILD from forma declarations | `bazel-adapter/` generate+check via core RestrictionGraph; examples + tests green |
| F-042 | done | Minimal Bazel sample using forma-core concepts | `bazel-sample/` + real `bazelisk build`/`run` + docs; see PROGRESS |

## P5 — Progressive examples + agent skills

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-050 | done | Progressive examples + agent skills for all supported features | `examples/{jvm,android}/*` + `examples/agent-skills/` + `docs/PROGRESSIVE-EXAMPLES.md`; JVM build+run and Android assembleDebug verified |

## P6 — Flat structure (deprecate generic androidLibrary)

Forma’s job is to **keep the graph flat and role-typed**. `androidLibrary` was a
temporary generic AGP-library escape hatch (shared suffix with JVM `library`) that
encourages dumping mixed concerns into one bucket. Prefer specific targets.

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-060 | done | Deprecate `androidLibrary` + document flat-structure policy | `@Deprecated` on DSL; VISION + `docs/ANDROID-LIBRARY-DEPRECATION.md`; matrix/README |
| F-061 | done | Migrate `application/` sample off `androidLibrary` | di→`androidUtil`, mvvm→`uiLibrary`, navigation→`androidRes`; path renames |
| F-062 | done | Reimplement progressive examples / skills without `androidLibrary` | `examples/android/04` + agent skills + README curriculum |
| F-063 | done | Hard-remove `androidLibrary` target (after consumers migrated) | DSL + `android.library` type/registry/restrictions removed; JVM `library` + AGP feature helper kept; matrix/docs updated |

## P7 — Target plugins (type-owned, auto-apply)

Close the biggest call-site / multi-way gap: chain `withPlugin`. Design:
`docs/TARGET-PLUGINS.md`. Serves root goals **1** (Bazel call sites) and **2**
(one way: type owns plugin). GH #36 → F-073.

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-070 | done | Design type-owned plugins API | Bazel north star; type owns plugin; auto-apply; reject free-form lists + call-site binding re-selection |
| F-071 | done | **Implement** type→plugin registry + auto-apply; `targetPlugin` / `deriveTargetType` | `TargetPluginSpec` + registry + Path A/B; `applyTargetPlugins` on all Android DSLs; unit tests; plugins build green; legacy shims kept for F-072 |
| F-072 | done | Migrate sample to derived types (e.g. `navigationRes`); hard-deprecate chain | Path B `navigationRes` in build-dependencies; `resourcesTarget` helper; Unit returns on androidRes/binary/uiLibrary; `TargetBuilder`/`PluginWrapper`/`Plugins` `@Deprecated`; zero active `.withPlugin`; application build green |
| F-073 | done | User docs + progressive example + agent skill; close GH #36 | `docs/TARGET-PLUGINS.md` quick start; `examples/android/10-target-plugins` Path B; agent skill `forma-target-plugins`; GH #36 closable |

## P8 — Principle alignment (implementation matches goals)

Close remaining gaps where code/docs still allow **multiple ways**, **fat call
sites**, or **missing fleet tooling**. **P7 + P8 (F-070–F-085) done**; **F-019 Phase 1 done**.

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-080 | done | Codify root principles in product docs | `VISION.md` § Root principles + README + `AGENTS.md` + GETTING-STARTED; board P8 added |
| F-081 | done | Call-site surface audit: `Unit` returns, no builder chains, minimal attrs | Hard-removed `TargetBuilder` / `PluginWrapper` / sample `Plugins` + chain-only `PluginConfiguration`; all Android/JVM DSLs already `Unit`; `docs/CALL-SITE-SURFACE.md` flag inventory (`compose` → project-global default, `viewBinding` on impl + dedicated type) |
| F-082 | done | One global configuration path | Hard-removed `Project.androidProjectConfiguration`; `ScriptHandlerScope` form is the single API; `extraPlugins` documented classpath-only; new `PROJECT-CONFIGURATION.md` + cross-links; builds green |
| F-083 | done | One project-global external-deps convention | **House style = `projectDependencies` → `libs.*`**. Typed `build-dependencies/` catalogs = advanced/sample-scale only (not dual happy path). `DEPS-CATALOG.md` + README/GETTING-STARTED/agent skill/08 README |
| F-084 | done | Fleet tooling for explicit graphs (check / generate / migrate) | **v1 in `tools.forma.core.fleet`:** `LayoutChecker` / `LayoutGenerator` / `MigratePlanner` / path forms; `docs/FLEET-TOOLING.md` + agent skill; GH **#54** package→dir covered. Phase 2 follow-ups → **F-088** |
| F-085 | done | Full-tree principle audit: sample + examples + agent skills | `docs/PRINCIPLE-AUDIT.md`; teaching stragglers fixed (removed-not-deprecated chain wording); zero live chain/`androidLibrary`/dual-path happy paths |

## P9 — Promoted backlog (2026-07-21 Stepan)

Promoted from daily next-actions / historical GH. Workers pick top `todo` in order.
**Not promoted as product tickets:** pause/stretch 4h worker (ops); `v2`→`master` (explicit git promote only).

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-087 | done | AndroidX / SDK ceiling after AGP 9 | compile/target **37**; core **1.19.0**, activity **1.13.0**, lifecycle **2.11.0**, compose **1.11.4**, room **2.8.4**, material **1.14.0**; JVM **11**; navigationevent on activity graph; paging **2.1.2** kept. Host+CI platform 37. |
| F-088 | done | Fleet tooling phase 2 (F-084 follow-ups) | Gradle `formaLayoutCheck`/`formaLayoutGenerate` + root `*All` via `tools.forma.deps.fleet.registerFormaLayout` on all packageName DSLs; opt-in `checkPackageLayoutAtConfiguration` (default false); res/viewBinding/binary skip source-dir requirement; docs/skill updated. GH **#54** generate path user-complete (Hermes may close). AST migrate / depgen still deferred. |
| F-089 | done | Navigation task cache broken | GH **#110** — upstream Safe Args **2.9.8** already `@PathSensitive(RELATIVE)` on `navigationFiles`; two-location build-cache hit verified on sample `navigationRes`; regression note in `docs/CONFIGURATION-PERFORMANCE.md`. |
| F-090 | done | Exclude modules from dependency validation | GH **#97** — `dependencyValidationExclusions` on root `androidProjectConfiguration` / `AndroidProjectSettings`; `applyDependencies` skips project-dep suffix validation for exact path/name matches only. Self-type validation unchanged. Unit tests in `:config` + `:deps`; matrix + PROJECT-CONFIGURATION docs. |
| F-091 | done | BuildFeatures under Forma | GH **#88** — `FormaBuildFeatures` project-global (defaults **off**); central apply on library+binary; type-owned viewBinding/composeWidget + minimal `impl`/`compose` attrs; docs CALL-SITE + PROJECT-CONFIGURATION. |
| F-092 | done | versionCode / versionName on binary (and app) | GH **#82** — **done:** required `versionCode`/`versionName` on `androidBinary` only (wired to AGP `defaultConfig`); **not** on `AndroidProjectSettings` / `androidProjectConfiguration`; `androidApp` stays library shell (no version attrs — Gradle app-module limit). Sample + examples already use call-site API; docs/KDoc locked. |
| F-093 | todo | Remove legacy `.kapt` once unused | After F-086 sample is KSP-only: hard-remove or hard-deprecate `String.kapt` / `kapt()` / `Kapt` configuration path if no in-repo consumers; keep only if a documented external-compat shim is still required. Grep tree + tests + DEPS docs. |
| F-094 | blocked | Gradle Plugin Portal Forma org/user | GH **#133** — human/admin: create shared Portal user/org and credentials for team publish. Worker cannot finish without Stepan Portal access. Track only; see `docs/PLUGIN-PUBLISH.md`. |

## Backlog (lower priority / historical GitHub)

Keep for reference; do not start unless higher tickets done or user prioritizes:

- ~~GH #110 Navigation task cache broken~~ → **F-089**
- ~~GH #97 Excluded from dependency validation~~ → **F-090**
- ~~GH #88 BuildFeatures support~~ → **F-091**
- ~~GH #82 Version code/name in binary~~ → **F-092**
- ~~GH #133 Portal Forma user~~ → **F-094** (blocked on human admin)
- ~~F-019 AndroidX ceiling~~ → **F-087**
- ~~Legacy `.kapt` removal~~ → **F-093**
- ~~F-084 Gradle-task follow-ups~~ → **F-088**
- GH #77 transitiveDeps extension
- GH #54 Generate target structure from minimal config → **theme under F-084 / F-088**
- GH #51 Support build types
- GH #46 New navigation system
- GH #44/#43 Hybrid targets/config examples
- GH #36 Docs for external plugins → **P7 / F-070–F-073**
- GH #126 Target features configuration options → consider under **F-081** / **F-091**
- GH #111 Gradle project as buildscript classpath
- GH #103 Java 8+ API on Android API ≤26

## How workers update this file

1. Set ticket to `in_progress` when starting.
2. On partial progress: leave `in_progress`, append note under ticket in `docs/PROGRESS.md`.
3. On finish: set `done`, link PR/commit in PROGRESS.
4. Never reorder priority without an explicit user request; append new tickets at end of the right phase.
