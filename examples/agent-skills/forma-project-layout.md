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
