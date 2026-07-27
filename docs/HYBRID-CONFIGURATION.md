# Hybrid configuration — impl ↔ stub-impl swap (F-104 / GH #43)

**Status:** shipped (design + working spike)  
**Depends on:** F-099 `FormaFeatureFlags`, F-101 `target(":…")`, F-103 `stub-impl/` layout  
**Layout teaching:** [`examples/android/15-hybrid-targets`](../examples/android/15-hybrid-targets)

## Problem

Composition roots must depend on **api + one of impl / stub-impl** (never both).
Before F-104, example 15 documented a **manual comment swap** of `target(":…:impl")`
↔ `target(":…:stub-impl")`. That does not scale: every root repeats the dual list,
and IDE/local perf toggles become per-module Gradle `if` hacks.

GH [#43](https://github.com/formatools/forma/issues/43) asks for stub targets and a
deps API so **IDE sync** can substitute stubs via **one project-global flag**.

## Product axioms (non-negotiable)

1. **Configure once** — flag on `androidProjectConfiguration`; call sites stay minimal.
2. **One global way** — single flag + pair helper; no dual happy path of free-form
   `if (project.hasProperty)` as the product API.
3. **Explicit structure** — physical `stub-impl/` layout from F-103; no hidden magic folders.
4. **Closed matrix** — stubs remain type `impl`; no `androidLibrary`, no `impl`→`impl`,
   composition only at roots. **No matrix change** for this ticket.
5. **Reuse F-099** — same `FormaFeatureFlags` / `resolveFeatureFlags` path; do not invent
   a second flag system.

## Chosen model

### A. Canonical flag + property

| | |
|--|--|
| **Flag name** | `useFeatureStubs` ([`USE_FEATURE_STUBS_FLAG`](../plugins/deps/src/main/java/tools.forma/deps/core/ConditionalDependency.kt)) |
| **Gradle property** | `forma.useFeatureStubs` ([`USE_FEATURE_STUBS_PROPERTY`](../plugins/deps/src/main/java/tools.forma/deps/core/ConditionalDependency.kt)) |
| **Default** | **false** / unset → production **impl** |
| **IDE / local** | `-Pforma.useFeatureStubs=true` or `forma.useFeatureStubs=true` in `gradle.properties` |

```kotlin
// root build.gradle.kts
androidProjectConfiguration(
    project = rootProject,
    // ...
    featureFlags = tools.forma.config.FormaFeatureFlags(
        tools.forma.deps.core.USE_FEATURE_STUBS_FLAG to
            providers.gradleProperty(tools.forma.deps.core.USE_FEATURE_STUBS_PROPERTY)
                .map { it.toBoolean() }
                .orElse(false)
                .get(),
    ),
)
```

Declare the flag **once** at the root. Call sites never read the property themselves.

### B. Target-level flag metadata

`TargetSpec` carries the same optional gate as `NameSpec`:

- `featureFlag: String?`
- `featureFlagExpected: Boolean` (default `true`)
- `config: ConfigurationType` (default `Implementation`; `CompileOnly` / `RuntimeOnly` preserved)

Pure `resolveFeatureFlags(flags)` filters:

| Kind | Filtered? |
|------|-----------|
| `NameSpec` | yes (F-099) |
| `TargetSpec` | yes (F-104) |
| files / platforms | no (unchanged) |

Applied in `applyDependencies` (and KMP apply path) at **apply** time — not when the
DSL helper is constructed.

### C. Public DSL

| Helper | Role |
|--------|------|
| `TargetDependency.whenFlag(flag, enabled)` | Tag all target specs |
| `depsIf(flag, …TargetDependency\|FormaTarget)` | Include targets when flag matches |
| `depsUnless(flag, …)` | Include targets when flag is off / unknown |
| **`featureImplementation(impl, stub, flag = useFeatureStubs)`** | **Product API** — one pair, no dual manual lists |

```kotlin
dependencies = deps(
    deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:world:api"),
    ),
    featureImplementation(
        impl = target(":feature:hello:impl"),
        stub = target(":feature:hello:stub-impl"),
    ),
    featureImplementation(
        impl = target(":feature:world:impl"),
        stub = target(":feature:world:stub-impl"),
    ),
)
```

### D. Resolution semantics (simple swap — shipped)

| `useFeatureStubs` | Resolved edges |
|-------------------|----------------|
| `false` / unset | `implementation(impl)` only |
| `true` | `implementation(stub)` only |

Gated-out targets are **not** on the resolved dependency graph. They must still be
**included** projects in `settings` (includer discovers `stub-impl/` siblings). With
configuration-on-demand, unused siblings may not configure; example 15 sets
`org.gradle.configureondemand=false` so stubs still validate when unused.

### E. Optional dual-config pattern (not default)

GH #43 mentioned `compileOnly` + `runtimeOnly` / `implementation`. That pattern is
**supported by the model** (`TargetSpec.config` + flag gates) but **not** the default
product recipe:

- Simple swap avoids duplicate class bindings and is enough for IDE/local classpath shrink.
- Dual-config (e.g. `compileOnly(impl)` + `implementation(stub)`) can be composed manually
  with `whenFlag` + non-default configs if a fleet needs it — unit-tested that configs
  are preserved. Do not teach two equal happy paths.

### F. Composition-root source / FQNs

A classpath swap only changes runtime behavior when the root does not hardcode types
that exist on **only one** side. Teaching options:

1. **Same FQN replacement (example 15)** — stub-impl provides the same package + class
   names as production impl with lighter bodies (`DefaultHelloMessage` → `"HelloStub"`).
   Root always imports production FQNs; flag selects which module supplies the bytecode.
2. **DI / SPI** — bind `api` types from the selected impl module (Dagger modules, ServiceLoader).

Example 15 uses (1). Distinct `…stub` packages with different class names require a
source or binding change when swapping — fine for demos, worse for a one-flag toggle.

## Physical layout (F-103, unchanged)

```
feature/<name>/{api,impl,stub-impl}/
```

| Role | Directory | Type | `target()` path |
|------|-----------|------|-----------------|
| api | `api/` | `api` | `:feature:name:api` |
| production | `impl/` | `impl` | `:feature:name:impl` |
| stub | `stub-impl/` | `impl` | `:feature:name:stub-impl` |

Bare `stub/` fails the `impl` suffix rule — keep `stub-impl/`.

## IDE recipe

1. Root declares `useFeatureStubs` from `-Pforma.useFeatureStubs` (default false).
2. Roots use `featureImplementation(impl, stub)` only — no comment blocks.
3. Local / IDE sync: add to `gradle.properties` or Run configuration:
   ```
   forma.useFeatureStubs=true
   ```
4. CI / release builds: leave unset or `false`.
5. Verify:
   ```bash
   ./gradlew :binary:assembleDebug                          # Hello World
   ./gradlew :binary:assembleDebug -Pforma.useFeatureStubs=true  # HelloStub WorldStub
   ```

## Rejected alternatives

| Idea | Why rejected |
|------|----------------|
| Per-module `if (project.hasProperty("…"))` as happy path | Dual path; not fleet-scalable; violates one global way |
| New forever target type / bare `-stub` suffix | F-103 chose `stub-impl` + type `impl`; matrix churn without need |
| Restoring `androidLibrary` | Flat role-typed graph axiom |
| Second flag system beside `FormaFeatureFlags` | F-099 is the single map |
| Equal dual APIs (comment swap **and** flag API) | Migrate example 15 fully to the flag API |
| Changing gold `application/` layout in F-104 | Optional later; spike stays in example 15 |
| Default dual-config compileOnly+implementation | Risk of duplicate bindings; simple swap first |
| Free-form `.withPlugin` / plugin id lists gated by stubs | Type-owned plugins only |

## API checklist

- [x] Canonical flag `useFeatureStubs` + property `forma.useFeatureStubs`
- [x] `TargetSpec` feature-flag metadata + `ConfigurationType` preserved
- [x] `resolveFeatureFlags` filters targets (`TargetDependency` + `MixedDependency`)
- [x] `whenFlag` / `depsIf` / `depsUnless` for targets
- [x] `featureImplementation(impl, stub)`
- [x] Pure unit tests in `:deps`
- [x] Example 15 on the flag API
- [x] This design doc + cross-links

## Cross references

- [`TARGET-FEATURE-OPTIONS.md`](TARGET-FEATURE-OPTIONS.md) — `FormaFeatureFlags` (F-099)
- [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) — `featureFlags` on root config
- [`DEPS-CATALOG.md`](DEPS-CATALOG.md) — deps DSL table
- [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) — call-site vs project ownership
- [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) — unchanged edges
- [`PROGRESSIVE-EXAMPLES.md`](PROGRESSIVE-EXAMPLES.md) — ladder step 15
- Example: [`examples/android/15-hybrid-targets`](../examples/android/15-hybrid-targets)
- Skill: [`examples/agent-skills/forma-project-layout.md`](../examples/agent-skills/forma-project-layout.md)
