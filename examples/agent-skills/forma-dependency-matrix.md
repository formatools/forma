---
name: forma-dependency-matrix
description: Project-dep allowlists and how to fix validator failures.
---

# Dependency matrix (agent skill)

**Source of truth:** `docs/DEPENDENCY-MATRIX.md` (Android), `docs/JVM-TARGETS.md` (JVM).

## Dagger-friendly rules

| Consumer | May depend on (project suffixes) |
|----------|-----------------------------------|
| `api` | `api`, `library` |
| `impl` | api, utils, library, ui-library, res, viewbinding, widget, compose-widget — **not** other `impl` |
| `androidApp` / `androidBinary` | api, impl, shared UI/libs (composition roots) |
| `androidUtil` | android-util, test-util, res, **library** (JVM) |
| `uiLibrary` | widget, compose-widget, util, android-util, res |

## Fixing failures

1. Read error: consumer suffix, illegal dep, allowed set
2. Prefer re-slice (move types to `api` / shared lib) over disabling validation
3. Composition roots must list feature entrypoints explicitly

## Progressive demos of illegal vs legal

- Legal multi-feature: `examples/android/07-multi-feature`, `examples/jvm/04-multi-feature`
- Shared libs without impl edges: `examples/android/04-shared-libs`
