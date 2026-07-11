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

## P1 — Android working product

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-010 | todo | Document strict dependency matrix from live code (not only README) | Source of truth from validators/targets |
| F-011 | todo | Tighten validation for `api` / `impl` (Dagger2-friendly boundaries) | Align with current types; related GH #56, #18 |
| F-012 | todo | External deps catalog UX + tooling polish | `plugins/deps` catalog generators |
| F-013 | todo | Compose support for Android library/ui targets | GH #96 |
| F-014 | todo | Sample app: gold-standard multi-feature structure | Home/characters already present; modernize |
| F-015 | todo | Android project tutorial (getting started) | GH #53 |
| F-016 | todo | Plugin publish path (Portal user + target publish config) | GH #132, #133 |
| F-017 | todo | Configuration-time performance pass | GH #106, #42 |

## P2 — forma-core extraction

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-020 | todo | Design forma-core public API (types, restrictions, validation, target registry) | Write `docs/forma-core-api.md` first |
| F-021 | todo | Extract dependency-type / restriction engine into `forma-core` | Related GH #39, closed #34 |
| F-022 | todo | Extract validation framework into `forma-core` | Keep Android validators as plugins |
| F-023 | todo | Wire Android implementation as first consumer of forma-core | Sample still builds |
| F-024 | todo | Publish/coordinate coordinates: `tools.forma:core` vs android plugins | |

## P3 — JVM applications

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-030 | todo | JVM target set on forma-core (`library`, `api`, `impl`, `utils`, tests) | |
| F-031 | todo | JVM sample application | |
| F-032 | todo | Docs: JVM getting started | |

## P4 — Bazel

| ID | Status | Title | Notes |
|----|--------|-------|-------|
| F-040 | todo | Design Bazel adapter mapping (targets ↔ rules, visibility ↔ deps) | |
| F-041 | todo | Spike: generate or check Bazel BUILD from forma declarations | |
| F-042 | todo | Minimal Bazel sample using forma-core concepts | |

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
