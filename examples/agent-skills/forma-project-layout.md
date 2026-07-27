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

Optional fleet layout for easier multi-module navigation (GH #44 / F-103) plus
project-global stub swap (GH #43 / F-104):

```
feature/<name>/{api,impl,stub-impl}/
```

- Same Forma types as the ladder (`api`, `impl`). A **stub** is a real `impl` module
  in directory `stub-impl/` (name must end with `-impl` for the type suffix rule).
- Gradle projects: `:feature-<name>-api`, `:feature-<name>-impl`, `:feature-<name>-stub-impl`.
  Forma paths: `target(":feature:<name>:api|impl|stub-impl")`.
- Composition roots: `featureImplementation(impl = target("…:impl"), stub = target("…:stub-impl"))`.
  Flag **`useFeatureStubs`** (property `forma.useFeatureStubs`, default false) selects
  impl vs stub-impl — never both. Prefer same FQNs on stub and impl so roots need no
  source swap (simple classpath replacement).
- Declare the flag once on `androidProjectConfiguration(featureFlags = …)`.
  Do **not** use per-module `if (project.hasProperty)` as the happy path.
- No `androidLibrary`. No `impl` → `impl`. Matrix unchanged.
- Teaching example: [`examples/android/15-hybrid-targets`](../android/15-hybrid-targets).
- Design: [`docs/HYBRID-CONFIGURATION.md`](../../docs/HYBRID-CONFIGURATION.md).
