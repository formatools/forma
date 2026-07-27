# Target-feature configuration options (F-099 / GH #126)

**Status:** design + thin API shipped. Project-global named boolean flags +
conditional dependency helpers (named deps F-099; **target** deps + stub swap
**F-104** — see [`HYBRID-CONFIGURATION.md`](HYBRID-CONFIGURATION.md)).
Binary-linked configuration remains **deferred**.

## Problem

Product teams need **one** way to toggle behavior across many modules without
reintroducing fat call sites or plugin shopping. Classic examples:

| Need | Example |
|------|---------|
| DI strategy | Annotation-processing / KSP Dagger in release vs [dagger-reflect](https://github.com/JakeWharton/dagger-reflect) in debug/dev |
| Optional stacks | Crash reporting, analytics, offline mode — different dep sets |
| Build-variant product features | Feature modules must know flags **while they configure**, not only at the final APK |

Flags must be readable when **feature modules** apply dependencies. Waiting until
`androidBinary` configuration is often **too late**.

## Why not (rejected)

| Approach | Why rejected |
|----------|----------------|
| Per-module `.withPlugin` / free-form plugin id lists | Violates type-owned plugins ([TARGET-PLUGINS.md](TARGET-PLUGINS.md)); dual happy path; does not scale |
| Binary-only flag maps on every `androidBinary` | Too late for `impl` / feature graph configuration; encourages per-binary dual paths |
| Per-`impl` Boolean for every product flag | Fat call sites; drifts from Bazel-like “configure once on the type/project” |
| Free-form Gradle `if (project.hasProperty)` as happy path | Second path beside Forma; untyped; unreviewable at fleet scale |
| Flags select **plugin identity** ad hoc | Plugin identity stays type-owned (F-070); flags select **deps/behavior** only |
| Restoring generic `androidLibrary` buckets | Orthogonal and already removed (F-063) |

## Chosen model: project-global `FormaFeatureFlags`

**Named boolean dimensions declared once** at root configuration:

```kotlin
// root build.gradle.kts
buildscript {
    androidProjectConfiguration(
        project = rootProject,
        // ...
        featureFlags = tools.forma.config.FormaFeatureFlags(
            "daggerReflect" to true, // dev machine / CI job toggle
        ),
    )
}
```

| Piece | Location |
|-------|----------|
| `FormaFeatureFlags` | `plugins/config` — pure immutable map |
| Storage | `AndroidProjectSettings.featureFlags` |
| Entry API | `androidProjectConfiguration(featureFlags = …)` |
| Read | `Forma.settings.featureFlags["name"]` / `FormaSettingsStore.featureFlagsOrEmpty()` |

### Semantics

| API | Unknown name | Declared `false` | Declared `true` |
|-----|--------------|------------------|-----------------|
| `flags["name"]` / `isEnabled` | **false** | false | true |
| `require("name")` | **throws** | false | true |
| `contains("name")` | false | true | true |

**Default empty flags:** every name is off. Missing declaration is not an error for
`get` / conditional deps — it is a deliberate safe default so gated “on” deps stay out
until someone opts in at the root.

## Conditional dependencies

Helpers live next to `deps(...)` (root `dependencies.kt`). They **tag** named specs;
they do **not** read flags at construction time.

```kotlin
dependencies = deps(
    "com.google.dagger:dagger:2.48".dep,
    depsIf("daggerReflect", "com.jakewharton.dagger:dagger-reflect:0.3.0".dep),
    depsUnless("daggerReflect", "com.google.dagger:dagger:2.48".dep), // if not already always-on
    depsUnless("daggerReflect", "com.google.dagger:dagger-compiler:2.48".ksp),
)
```

| Helper | Include when |
|--------|----------------|
| `depsIf(flag, …deps)` | flag **on** (expected `true`) |
| `depsIf(flag, …deps, enabled = false)` | flag equals `enabled` (named param after vararg) |
| `depsUnless(flag, …deps)` | flag **off** (or unknown) |
| `named.whenFlag(flag, enabled)` | same, extension on `NamedDependency` |

### Resolution timing

1. Call site builds `NamedDependency` graphs; gated specs carry `featureFlag` +
   `featureFlagExpected` on `NameSpec`.
2. `applyDependencies` reads `FormaSettingsStore.featureFlagsOrEmpty()`.
3. Pure `resolveFeatureFlags(flags)` drops non-matching name specs (unit-tested).
4. Remaining specs apply as today (including KSP / catalog plugin side effects).

This keeps the model **pure + JaCoCo-friendly** and correct under Gradle configuration
order: helpers may run before or after settings are stored; **apply** always sees the
store.

### Composition

Existing `deps(vararg NamedDependency)` still works — it flattens `NameSpec` lists and
preserves flag metadata. Unconditional deps stay unconditional (`featureFlag == null`).

## Relationship to `FormaBuildFeatures` (F-091)

| | `FormaBuildFeatures` | `FormaFeatureFlags` |
|--|----------------------|---------------------|
| Concern | AGP `BuildFeatures` (aidl, buildConfig, …) | **Product** toggles (DI mode, optional stacks) |
| Shape | Fixed typed Booleans | Open named map |
| Applied by | Android library/binary feature wiring | Conditional deps (+ future feature reads) |
| Compose | **Not** here — top-level `compose` | N/A |

Do not nest product flags inside `FormaBuildFeatures`. Do not put AGP flags into
`FormaFeatureFlags`.

## Relationship to type-owned plugins (F-070)

- **Type owns plugin identity** — `targetPlugin` / `deriveTargetType` / registry auto-apply.
- **Flags select deps and behavior**, not “which plugin id string to shop at the call site.”
- If a product toggle must change plugins, prefer a **derived target type** or a single
  documented type-level binding — not `if (flag) withPlugin(...)`.

## Worked example: Dagger KSP vs dagger-reflect

**Intent:** one DI graph API; swap runtime/processor wiring with a single root flag.

```kotlin
// root
featureFlags = FormaFeatureFlags("daggerReflect" to providers.gradleProperty("forma.daggerReflect")
    .map { it.toBoolean() }
    .orElse(false)
    .get()) // or a hard-coded true on a dev branch
```

```kotlin
// feature impl (same call site in all modules)
impl(
    packageName = "com.example.feature.home.impl",
    dependencies = deps(
        deps(target(":feature:home:api")),
        "com.google.dagger:dagger:2.48".dep,
        depsIf("daggerReflect", "com.jakewharton.dagger:dagger-reflect:0.3.0".dep),
        depsUnless("daggerReflect", "com.google.dagger:dagger-compiler:2.48".ksp),
    ),
)
```

| `daggerReflect` | Runtime | Processor |
|-----------------|---------|-----------|
| `true` | dagger + dagger-reflect | *(no KSP compiler)* |
| `false` / unset | dagger | dagger-compiler via KSP |

**Not in this slice:** actually wiring dagger-reflect into the gold sample, or a full
binary↔configuration product. The recipe above is the supported pattern.

## Future (explicitly deferred)

| Item | Notes |
|------|--------|
| Binary-linked configuration (1:1 with binaries) | May return as a later ticket; must still feed flags **early** enough for feature modules (e.g. root still declares, binary selects profile) |
| Non-boolean dimensions | Start with booleans; enums/multi only if a ticket demands |
| Flag-gated **target plugins** | Prefer derived types; do not reopen free-form plugin lists |

## Shipped follow-on: hybrid stub swap (F-104)

Target-level flag gating + `featureImplementation(impl, stub)` for IDE/local
`impl` → `stub-impl` substitution. Canonical flag `useFeatureStubs` /
property `forma.useFeatureStubs`. **Design + API:** [`HYBRID-CONFIGURATION.md`](HYBRID-CONFIGURATION.md).

## API checklist

- [x] `FormaFeatureFlags` on `AndroidProjectSettings`
- [x] `androidProjectConfiguration(featureFlags = …)`
- [x] `depsIf` / `depsUnless` / `NamedDependency.whenFlag`
- [x] Apply-time `resolveFeatureFlags` in `applyDependencies`
- [x] Pure unit tests (`:config`, `:deps`)
- [x] Docs: this file + PROJECT-CONFIGURATION + CALL-SITE-SURFACE + DEPS-CATALOG
- [x] F-104: target flag gating + `featureImplementation` — [`HYBRID-CONFIGURATION.md`](HYBRID-CONFIGURATION.md)

## Cross references

- [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) — `featureFlags` section
- [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) — conditional deps ≠ plugin shopping
- [`DEPS-CATALOG.md`](DEPS-CATALOG.md) — API table
- [`HYBRID-CONFIGURATION.md`](HYBRID-CONFIGURATION.md) — F-104 stub swap
- [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md) — type-owned plugins
- [`VISION.md`](VISION.md) — root principles
