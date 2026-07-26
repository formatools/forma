---
name: forma-overview
description: Entry map for Forma progressive examples and platform plugins.
---

# Forma overview (agent skill)

## What Forma is

Meta build system (Gradle plugins) that replaces ad-hoc AGP wiring with **typed targets**,
shared configuration, and a **dependency restriction graph**.

## Platforms

| Plugin id | Module | Sample | Progressive ladder |
|-----------|--------|--------|--------------------|
| `tools.forma.android` | `plugins/android` | `application/` | `examples/android/01`…`11` |
| `tools.forma.jvm` | `plugins/jvm` | `jvm-application/` | `examples/jvm/01`…`05` |
| `tools.forma.kmp` | `plugins/kmp` | — | `examples/kmp/01` |
| `tools.forma:core` | `plugins/core` | — | restriction engine + fleet toolkit |
| Bazel adapter | `bazel-adapter/` | `bazel-sample/` | skill `forma-bazel` |

## Learning order for agents

1. This overview + `forma-includer-settings` + `forma-project-layout`
2. Platform targets (`forma-android-targets`, `forma-jvm-targets`, and/or `forma-kmp-targets`)
3. `forma-dependency-matrix` before multi-module wiring
4. `forma-deps-catalog` (house style) / `forma-compose` as needed
5. `forma-target-plugins` before any third-party Gradle plugin
6. `forma-fleet-tooling` when scaffolding/renaming packages at scale
7. Walk progressive examples in order; copy the **smallest** step that has the feature

## Docs of record

- `docs/VISION.md` — root principles
- `docs/DEPENDENCY-MATRIX.md` — live validator truth (Android + JVM + KMP edges)
- `docs/JVM-TARGETS.md` — JVM matrix
- `docs/KMP-TARGETS.md` — KMP design + matrix
- `docs/GETTING-STARTED.md` / `docs/JVM-GETTING-STARTED.md` / `docs/KMP-GETTING-STARTED.md`
- `docs/PROGRESSIVE-EXAMPLES.md`
- `docs/CALL-SITE-SURFACE.md` — Unit DSLs, flags, removed chain API
- `docs/PROJECT-CONFIGURATION.md` — single `androidProjectConfiguration` path
- `docs/DEPS-CATALOG.md` — house style `projectDependencies`
- `docs/TARGET-PLUGINS.md` — type-owned plugins (Path A/B)
- `docs/FLEET-TOOLING.md` — check/generate/migrate
- `docs/COMPOSE.md`
- `docs/PRINCIPLE-AUDIT.md` — full-tree teaching audit (F-085)

**External plugins:** type-owned (Path A/B) — skill `forma-target-plugins`, examples `android/10-target-plugins` (safe-args) and `android/11-metro-di` (Metro DI). Never `.withPlugin`.
**Navigation ports:** keep Jetpack/Safe Args out of feature `impl` — example `android/12-navigation-ports` + `docs/NAVIGATION-ABSTRACTION.md` (F-112; sample migration F-111).
