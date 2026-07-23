# 11 — metro-di (Android)

**Goal:** Wire [Metro](https://zacsweers.github.io/metro/latest/) (compile-time Kotlin DI)
through Forma’s **type-owned plugins** model. Call sites stay Bazel-flat.

## Features introduced
- **Type-owned Metro**: `dev.zacsweers.metro` registered once for `impl` + `app` kinds
  (Path A via `registerTargetPlugin`; see `forma-defs/MetroTargets.kt`)
- Thin **`metroImpl` / `metroApp`** DSLs — call sites stay attributes-only and load the
  registration class before `impl` / `androidApp` run
- `extraPlugins` / catalog `plugin(...)` as **classpath only** (not per-module apply)
- Mini DI graph: `@Inject` feature impl + `@DependencyGraph` at composition root
- Contrast with plain `api` / `androidRes` / `androidBinary` (no Metro plugin)

## Mental model

| Layer | Owns |
|-------|------|
| Type registration (`forma-defs` / once) | Metro plugin id on `impl` + `app` |
| Call site (`metroImpl` / `metroApp`) | Attributes only (`packageName`, `dependencies`) |
| Metro annotations (Kotlin sources) | Graph shape (`@Inject`, `@DependencyGraph`, `@Provides`) |

```kotlin
// Registration once (forma-defs) — not repeated per module
private val metroCompiler = targetPlugin(id = "dev.zacsweers.metro")
registerTargetPlugin(AndroidTargetTypes.impl, metroCompiler)
registerTargetPlugin(AndroidTargetTypes.app, metroCompiler)

fun Project.metroImpl(packageName: String, dependencies: FormaDependency = emptyDependency()) {
    // ensure registration loaded, then standard impl wiring
    impl(packageName = packageName, dependencies = dependencies)
}

// Call site — no plugin ids
metroImpl(
    packageName = "…",
    dependencies = deps(target(":feature:hello:api")),
)
```

**Path B note:** res-like plugins use `deriveTargetType` + `resourcesTarget(type=…)`
(example 10). There is not yet a public `implTarget(type=…)` helper, so this step uses
**Path A on `impl`/`app`** plus named DSLs. Same product rule: call sites never pass
plugin ids.

**When Path A is right for DI:** you want every feature impl and composition-root app
in the project to compile with Metro (typical for a Metro-first app). If only some
modules need Metro, wait for / add an `implTarget(type=)` Path B helper — do not
reintroduce `.withPlugin`.

## Forbidden
- `.withPlugin("dev.zacsweers.metro")` / free-form plugin lists on targets
- Re-selecting Metro at each call site
- Putting `@DependencyGraph` behind an `impl`→`impl` edge (composition roots only)

## Metro notes
- Metro is a **Kotlin compiler plugin** (not KSP). Forma still uses KSP for Dagger/Room
  in the gold-standard `application/` sample — this ladder step is the Metro path.
- Runtime artifacts are pulled by the Metro Gradle plugin defaults.
- Example uses **JVM 11** (`javaVersionCompatibility = VERSION_11`) — Metro 1.x runtime
  is not 1.8-inline-safe.
- Upstream: [Metro docs](https://zacsweers.github.io/metro/latest/) · version **1.3.2**

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
# Prove type-owned Metro applied (includer flattens path → project name):
./gradlew :feature-hello-impl:tasks --all | grep -i metro
./gradlew :root-app:tasks --all | grep -i metro
```

## Docs
- Design + API: [`docs/TARGET-PLUGINS.md`](../../../docs/TARGET-PLUGINS.md)
- Ladder index: [`docs/PROGRESSIVE-EXAMPLES.md`](../../../docs/PROGRESSIVE-EXAMPLES.md)
- Sample Dagger (KSP) gold standard: `application/` + [`docs/SAMPLE-APP.md`](../../../docs/SAMPLE-APP.md)

## Previous
10 taught Path B with Navigation Safe Args (`navigationRes`). This step reuses the
same type-owned plugin pattern for a **compiler plugin DI** framework.
