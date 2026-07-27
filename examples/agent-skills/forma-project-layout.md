---
name: forma-project-layout
description: Feature folder conventions, packages, composition roots.
---

# Project layout (agent skill)

## Feature slice

```
feature/<name>/{api,impl,res,viewbinding}/
```

## Composition roots

- Android: `binary/` (`androidBinary`) + optional `root-app/` (`androidApp`)
- JVM: `binary/` with `mainClass`

Roots **must** depend on each feature’s `api` and `impl` explicitly.

## Packages

`packageName` ↔ `src/main/java|kotlin/<package-as-dirs>/`

Examples gold standards: `docs/SAMPLE-APP.md`, `docs/JVM-SAMPLE.md`.
Teaching ladders: `examples/**`.


## Hybrid co-located roles (api + impl + stub)

Optional fleet layout for easier multi-module navigation (GH #44 / F-103):

```
feature/<name>/{api,impl,stub-impl}/
```

- Same Forma types as the ladder (`api`, `impl`). A **stub** is a real `impl` module
  in directory `stub-impl/` (name must end with `-impl` for the type suffix rule)
  with `packageName` `…stub` and a lightweight stand-in of the same `api`.
- Gradle projects: `:feature-<name>-api`, `:feature-<name>-impl`, `:feature-<name>-stub-impl`.
  Forma paths: `target(":feature:<name>:api|impl|stub-impl")`.
- Composition roots depend on **api + one of impl/stub-impl** (default: **impl**).
  Do not depend on both. Manual swap is documented; automated IDE swap is **F-104**.
- No `androidLibrary`. No `impl` → `impl`. No engine dual path — layout convention only.
- Teaching example: [`examples/android/15-hybrid-targets`](../android/15-hybrid-targets).
