# Forma prioritized tickets

Status legend: `todo` | `in_progress` | `blocked` | `done`

Update this file when picking or finishing work. Cron workers must pick the **highest priority open ticket** that is not blocked.

## P0 — Bootstrap (Android product baseline)

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-001 | done | Environment bootstrap: JDK + Android SDK tooling on worker host | OpenJDK 17 + cmdline-tools; see `docs/ENV.md`, `scripts/env-mac.sh`. plugins/app/includer/depgen build green on host |
| F-002 | done | Audit build graph: plugins, sample app, CI workflows | Map modules → forma-core candidates; capture in `docs/ARCHITECTURE.md` |
| F-003 | done | Get plugins + sample `application/` building on modern toolchain | Plugins compile AGP 8.1.2 matches sample; Gradle 8.3 (plugins) / 8.4 (app); host builds green |
| F-004 | done | CI green on GitHub Actions for plugins + application | Temurin 17 all jobs + Android SDK 33 for app; PR #153 GHA green |
| F-018 | in_progress | JDK 21 + Gradle/AGP staged modernization | **2026-07-19 deps PR:** Gradle **8.14.5**, AGP **8.13.2**, Kotlin **2.0.21**, KSP **2.0.21-1.0.28**, Compose **1.9.4**/compiler **2.0.21**, sample libs modernized (AGP-8.13/SDK-35 ceiling). JDK 21 host/CI already. AGP 9 = F-019. |

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

## Backlog (lower priority / historical GitHub)

Keep for reference; do not start unless higher tickets done or user prioritizes:

- GH #110 Navigation task cache broken
- GH #97 Excluded from dependency validation
- GH #88 BuildFeatures support
- GH #82 Version code/name in binary
- GH #77 transitiveDeps extension
- GH #54 Generate target structure from minimal config
- GH #51 Support build types
- GH #46 New navigation system
- GH #44/#43 Hybrid targets/config examples
- GH #36 Docs for external plugins
- GH #126 Target features configuration options
- GH #111 Gradle project as buildscript classpath
- GH #103 Java 8+ API on Android API ≤26

## How workers update this file

1. Set ticket to `in_progress` when starting.
2. On partial progress: leave `in_progress`, append note under ticket in `docs/PROGRESS.md`.
3. On finish: set `done`, link PR/commit in PROGRESS.
4. Never reorder priority without an explicit user request; append new tickets at end of the right phase.
