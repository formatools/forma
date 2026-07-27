# 15 — hybrid targets (flat api / impl / stub co-location)

**Goal:** Teach a **hybrid / flat co-located feature layout** where `api`, `impl`, and
a **stub** sibling live under one feature folder. Easy multi-module navigation
(GH [#44](https://github.com/formatools/forma/issues/44)) without restoring
`androidLibrary`, without engine changes, and without F-104 IDE swap wiring.

## Features introduced

- Co-located `feature/<name>/{api,impl,stub-impl}/` (same mental model as 02/07, flatter nav)
- Real **stub** modules that implement the same `api` with lightweight stand-ins
- Multi-feature composition (hello + world) at the root — same rules as 07
- Manual `impl` → `stub-impl` dependency swap as a **preview of F-104** (not automated)

## Layout

```
15-hybrid-targets/
  binary/                 # androidBinary composition root
  root-app/               # androidApp composition root
  root-res/
  feature/
    hello/
      api/                # api(...) contracts
      impl/               # impl(...) production
      stub-impl/          # impl(...) lightweight stand-in (same api)
    world/
      api/
      impl/
      stub-impl/
```

### Why `stub-impl` (not bare `stub/`)?

Forma target types are **suffix-typed**: an `impl { }` module’s Gradle project name
must end with `-impl` (self-type / matrix checks). Includer derives the name from
the directory path, so the role folder is `stub-impl/`:

| Role (mental model) | Directory | Gradle project | `target()` path |
|---------------------|-----------|----------------|-----------------|
| api | `feature/hello/api` | `:feature-hello-api` | `:feature:hello:api` |
| impl (production) | `feature/hello/impl` | `:feature-hello-impl` | `:feature:hello:impl` |
| stub (stand-in) | `feature/hello/stub-impl` | `:feature-hello-stub-impl` | `:feature:hello:stub-impl` |

The **role** is still “stub”; the **type** remains `impl` with `packageName` `…stub`.
No new target type and no engine change — layout convention only. F-104 may later
add project-global swap tooling; it does not need a bare `-stub` suffix for this
teaching example.

Includer discovers each role directory (`arbitraryBuildScriptNames = true`).

### Why this eases IDE navigation

Deep ladders scatter related roles across distant trees. Co-location keeps **one
folder per feature** with role-named children — open `feature/hello/` and see
api + impl + stub together. Optional browsing symlinks (e.g. top-level
`hello-api` → `feature/hello/api`) are fine if they help humans, but **includer
must still discover real `projectDir`s** and `target(":feature:…")` remains the
supported path. This example does **not** require symlinks.

### api / impl / stub mental model

| Role | Target type | Depends on | Purpose |
|------|-------------|------------|---------|
| `api` | `api` | (none / shared libs) | Public contracts |
| `impl` | `impl` | `api` of same feature | Production implementation |
| `stub` | `impl` (dir `stub-impl`, package `…stub`) | `api` of same feature | Lightweight stand-in for IDE/sync/demo |

Rules that still hold:

- **No `androidLibrary`** — roles stay typed
- **`impl` ↛ `impl`** — stubs do not depend on production impls (or each other)
- Composition **only at roots** (`androidBinary` / `androidApp`)
- Roots depend on **api + one of impl/stub-impl** — never both (duplicate bindings)

Default wiring uses **impl**. Stubs compile as real modules so you can prove the
layout; they are not on the production classpath unless you swap deps.
This example sets `org.gradle.configureondemand=false` so stub-impl siblings are
always configured and validated even when unused by the default root graph.

## Manual stub swap (teaser → F-104)

1. In `root-app/build.gradle.kts` and `binary/build.gradle.kts`, replace
   `target(":feature:hello:impl")` with `target(":feature:hello:stub-impl")`
   (same for `world`).
2. In `HelloActivity`, import `StubHelloMessage` / `StubWorldMessage` instead of
   the `Default*` classes.
3. `./gradlew :binary:assembleDebug` — UI text becomes `HelloStub WorldStub`.

**F-104** will automate impl→stub via a **project-global flag** (IDE sync /
compileOnly+runtimeOnly style). This step only teaches the **physical layout**
and keeps stubs wireable by hand.

## Build

```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
# Optional: prove stubs compile without swapping roots
./gradlew :feature-hello-stub-impl:compileDebugKotlin :feature-world-stub-impl:compileDebugKotlin
```

Expect **BUILD SUCCESSFUL**. Default APK shows combined hello + world messages
from **impl** (`Hello World`).

## Principles

1. Bazel-like types — call sites are attributes only (`api` / `impl` / roots)
2. One global way — co-located `{api,impl,stub-impl}` is a **layout convention**, not a
   second target DSL
3. Explicit structure — role folders declare boundaries; matrix unchanged

## Non-goals

- F-104 IDE configuration substitution APIs
- Forma engine / dependency-matrix changes (including a dedicated `stub` type/suffix)
- Restoring `androidLibrary`
- Changing gold `application/` sample layout

## Related

- Step 02 — minimal api+impl
- Step 07 — multi-feature composition
- F-104 — automated stub swap for IDE sync
- Agent skill: `examples/agent-skills/forma-project-layout.md`
