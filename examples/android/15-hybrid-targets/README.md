# 15 — hybrid targets + stub swap (flat api / impl / stub-impl)

**Goal:** Teach a **hybrid / flat co-located feature layout** where `api`, `impl`, and
a **stub** sibling live under one feature folder, plus the **F-104** project-global
flag that swaps `impl` → `stub-impl` at composition roots (GH
[#44](https://github.com/formatools/forma/issues/44),
[#43](https://github.com/formatools/forma/issues/43)).

Design: [`docs/HYBRID-CONFIGURATION.md`](../../../docs/HYBRID-CONFIGURATION.md).

## Features introduced

- Co-located `feature/<name>/{api,impl,stub-impl}/` (same mental model as 02/07, flatter nav)
- Real **stub** modules that replace production impl FQNs with lightweight stand-ins
- Multi-feature composition (hello + world) at the root — same rules as 07
- **`featureImplementation(impl, stub)`** + `useFeatureStubs` flag (no comment hacks)

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
      stub-impl/          # impl(...) lightweight stand-in (same api + same FQNs)
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

The **role** is still “stub”; the **type** remains `impl`. No new target type.

### Same FQNs (simple swap)

Stubs ship the **same package + class names** as production (`DefaultHelloMessage`,
etc.) with different `text()` bodies. Composition roots always import production
FQNs; the flag selects which module is on the classpath.

### api / impl / stub mental model

| Role | Target type | Depends on | Purpose |
|------|-------------|------------|---------|
| `api` | `api` | (none / shared libs) | Public contracts |
| `impl` | `impl` | `api` of same feature | Production implementation |
| `stub` | `impl` (dir `stub-impl`) | `api` of same feature | Lightweight stand-in for IDE/sync/demo |

Rules that still hold:

- **No `androidLibrary`** — roles stay typed
- **`impl` ↛ `impl`** — stubs do not depend on production impls (or each other)
- Composition **only at roots** (`androidBinary` / `androidApp`)
- Roots depend on **api + one of impl/stub-impl** — never both (duplicate bindings)

## Automated stub swap (F-104)

Root `androidProjectConfiguration` declares one flag from a Gradle property:

```kotlin
featureFlags = FormaFeatureFlags(
    USE_FEATURE_STUBS_FLAG to
        providers.gradleProperty(USE_FEATURE_STUBS_PROPERTY)
            .map { it.toBoolean() }
            .orElse(false)
            .get(),
)
```

Roots use the pair helper (no comment blocks):

```kotlin
featureImplementation(
    impl = target(":feature:hello:impl"),
    stub = target(":feature:hello:stub-impl"),
)
```

| Command | Flag | UI text |
|---------|------|---------|
| `./gradlew :binary:assembleDebug` | off (default) | `Hello World` |
| `./gradlew :binary:assembleDebug -Pforma.useFeatureStubs=true` | on | `HelloStub WorldStub` |

This example sets `org.gradle.configureondemand=false` so stub-impl siblings are
always configured and validated even when unused by the default root graph.
Stubs remain **included** projects either way; gated-out targets are simply not
on the resolved dependency graph.

## Build

```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties

# Production impl (default)
./gradlew :binary:assembleDebug

# Stub mode (IDE / local perf demo)
./gradlew :binary:assembleDebug -Pforma.useFeatureStubs=true

# Optional: prove stubs compile on their own
./gradlew :feature-hello-stub-impl:compileDebugKotlin :feature-world-stub-impl:compileDebugKotlin
```

Expect **BUILD SUCCESSFUL** for both assemble variants.

## Principles

1. Bazel-like types — call sites are attributes only (`api` / `impl` / roots)
2. One global way — single `useFeatureStubs` flag + `featureImplementation`; no per-module `if`
3. Explicit structure — role folders declare boundaries; matrix unchanged

## Non-goals

- Restoring `androidLibrary` or a dedicated `stub` target type/suffix
- Changing gold `application/` sample layout
- Dual-config `compileOnly(impl)+implementation(stub)` as the default recipe
  (model supports it; simple swap is the shipped happy path — see design doc)

## Related

- Step 02 — minimal api+impl
- Step 07 — multi-feature composition
- [`docs/HYBRID-CONFIGURATION.md`](../../../docs/HYBRID-CONFIGURATION.md) — F-104 design
- Agent skill: `examples/agent-skills/forma-project-layout.md`
