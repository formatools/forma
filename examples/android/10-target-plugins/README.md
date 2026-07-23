# 10 — target-plugins (Android)

**Goal:** Type-owned external Gradle plugins (F-070–F-073). Call sites stay Bazel-flat.

## Features introduced
- **Path B** derived type: `navigationRes` via `targetPlugin` + `deriveTargetType` (see `forma-defs/`)
- Auto-apply of `androidx.navigation.safeargs.kotlin` on every `navigationRes(...)` call site
- `extraPlugins` / catalog `plugin(...)` as **classpath only** (not per-module apply)
- Contrast with plain `androidRes` (no external plugin) on `root-res`

## Mental model

| Layer | Owns |
|-------|------|
| Type registration (`forma-defs` / once) | Plugin identity |
| Call site (`feature/hello/res/build.gradle.kts`) | Attributes only (`packageName`, `dependencies`) |

```kotlin
// Registration once (forma-defs) — not repeated per module
val navigationSafeArgs = targetPlugin(id = "androidx.navigation.safeargs.kotlin")
val ExampleNavigationResType = deriveTargetType(
    id = "example.navigation-res",
    base = AndroidTargetTypes.res,
    nameSuffix = "res",
    plugins = listOf(navigationSafeArgs),
)
fun Project.navigationRes(...) = resourcesTarget(type = ExampleNavigationResType, ...)

// Call site — no plugin ids
navigationRes(
    packageName = "…",
    dependencies = deps("androidx.navigation:navigation-fragment-ktx:2.7.7", …),
)
```

**Path A** (not used here): `registerTargetPlugin(AndroidTargetTypes.res, safeArgs)` would attach
safe-args to *every* `androidRes(...)`. Prefer Path B when only some res modules need the plugin.

## Forbidden
- `.withPlugin(...)` / `.withPlugins(...)` / `TargetBuilder` chains (**removed** F-081)
- Free-form `plugins = plugins(plugin("id"))` on targets
- Re-selecting plugin bindings at each call site

## Build
```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
# Prove type-owned safe-args applied:
./gradlew :feature:hello:res:tasks --all | grep -i safeargs
```

## Docs
- Design + API: [`docs/TARGET-PLUGINS.md`](../../../docs/TARGET-PLUGINS.md)
- Call-site contract: [`docs/CALL-SITE-SURFACE.md`](../../../docs/CALL-SITE-SURFACE.md)
- Agent skill: [`examples/agent-skills/forma-target-plugins.md`](../../agent-skills/forma-target-plugins.md)
- Gold-standard sample: `application/core/navigation/res` + `build-dependencies/.../NavigationRes.kt`
- Full-tree audit: [`docs/PRINCIPLE-AUDIT.md`](../../../docs/PRINCIPLE-AUDIT.md) (F-085)

## Next
11 wires the same Path B pattern for [Metro](https://zacsweers.github.io/metro/latest/) DI
(`examples/android/11-metro-di`). See [`docs/PROGRESSIVE-EXAMPLES.md`](../../../docs/PROGRESSIVE-EXAMPLES.md).
