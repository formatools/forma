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
| `tools.forma.android` | `plugins/android` | `application/` | `examples/android/01`…`09` |
| `tools.forma.jvm` | `plugins/jvm` | `jvm-application/` | `examples/jvm/01`…`05` |
| `tools.forma:core` | `plugins/core` | — | restriction engine |
| Bazel adapter | `bazel-adapter/` | `bazel-sample/` | skill `forma-bazel` |

## Learning order for agents

1. This overview + `forma-includer-settings` + `forma-project-layout`
2. Platform targets (`forma-android-targets` or `forma-jvm-targets`)
3. `forma-dependency-matrix` before multi-module wiring
4. `forma-deps-catalog` / `forma-compose` as needed
5. Walk progressive examples in order; copy the **smallest** step that has the feature

## Docs of record

- `docs/DEPENDENCY-MATRIX.md` — live validator truth (Android)
- `docs/JVM-TARGETS.md` — JVM matrix
- `docs/GETTING-STARTED.md` / `docs/JVM-GETTING-STARTED.md`
- `docs/PROGRESSIVE-EXAMPLES.md`
- `docs/COMPOSE.md`, `docs/DEPS-CATALOG.md`
