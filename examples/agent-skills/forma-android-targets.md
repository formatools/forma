---
name: forma-android-targets
description: All Android Forma DSL targets, suffixes, content rules, and example steps.
---

# Android targets (agent skill)

Plugin: `tools.forma.android`. Register happens on first DSL use via `AndroidTargetRegistry`.

| DSL | Suffix | Content rules | Progressive step |
|-----|--------|---------------|------------------|
| `androidBinary` | `binary` | no `res/`; composition root | 01+ |
| `androidApp` | `app` | no `res/`; composition root | 01+ |
| `api` | `api` | no `res/` | 02+ |
| `impl` | `impl` | Android feature impl | 02+ |
| `androidRes` | `res` | **only** `res/` under src/main | 01, 03 |
| `navigationRes` (Path B sample/example) | `res` (typical) | same as res + type-owned safe-args | 10 |
| `viewBinding` | `viewbinding` | **only** `layout*` under res | 03 |
| `library` | `library` | pure JVM library | 04 |
| `util` | `util` | no res/ | 04 |
| `androidUtil` | `android-util` | no res/ | 04, **13** (JNI façade) |
| `widget` | `widget` | Custom View | 05 |
| `uiLibrary` | `ui-library` | shared UI blocks | 05 |
| `composeWidget` | `compose-widget` | always Compose | 06 |
| `testUtil` | `test-util` | no res/ | 09 (unit test usage) |
| `androidTestUtil` | `android-test-util` | Android test helpers | 09 (`androidTestDependencies`) |
| `androidNative` | `native` | no res/; **leaf** (no first-party deps) | **13** |

## Minimal skeletons

```kotlin
// versionCode/versionName required on every binary (F-092) — not project-global
androidBinary(packageName = "…", versionCode = 1, versionName = "0.1.0", dependencies = deps(target(":root-app")))
// androidApp = library composition shell — no versionCode/versionName
androidApp(packageName = "…", dependencies = deps("androidx.appcompat:appcompat:1.6.1") + deps(target(":root-res")))
androidRes(packageName = "…")
api(packageName = "…")
impl(
    packageName = "…",
    dependencies = deps(target(":feature:x:api")),
    // FormaDependency: named GAVs and/or first-party test helpers
    testDependencies = transitiveDeps("junit:junit:4.13.2"),
    androidTestDependencies = deps(target(":common:android-test-util")) + transitiveDeps(
        "androidx.test.ext:junit:1.2.1",
    ),
)
viewBinding(packageName = "…", dependencies = deps(target(":feature:x:res")))
widget(packageName = "…")
composeWidget(packageName = "…", dependencies = deps(/* compose GAVs */))
// androidLibrary(…) // REMOVED F-063 — use androidUtil / uiLibrary / androidRes / …
androidUtil(packageName = "…", dependencies = deps(target(":common:hello:native"))) // JNI façade
util(packageName = "…")
library(packageName = "…")
testUtil(packageName = "…")
androidTestUtil(packageName = "…")
androidNative(
    packageName = "…",
    buildSystem = tools.forma.android.config.CMake(path = file("src/main/cpp/CMakeLists.txt")),
    abi = setOf(tools.forma.android.config.NdkAbi.ARM8, tools.forma.android.config.NdkAbi.X86_64),
)
```

## First-party library coverage (usage examples)

Every Android **first-party library-like** role has a progressive step with real call-sites + source usage:

| Role | Example | Usage proof |
|------|---------|-------------|
| `library` / `util` / `androidUtil` | 04 | feature code calls helpers |
| `uiLibrary` / `widget` | 05 | Activity hosts `BannerView` |
| `composeWidget` | 06 | Activity `setContent { GreetingCard }` |
| `testUtil` | 09 | `AdderTest` uses `assertPositive` |
| `androidTestUtil` | 09 | `AdderAndroidTest` uses `AndroidChecks` |
| `androidNative` | 13 | Activity shows JNI string via `androidUtil` |

## Forbidden

- `.withPlugin` / free-form plugin id lists — use type-owned plugins (see forma-target-plugins)
- `impl` → `impl`
- `api` shipping `res/`
- removed `androidLibrary` (use role-specific targets; F-063)
- Wrong suffix for DSL
- Relying on transitive feature wiring instead of listing api+impl on roots
- Putting `versionCode`/`versionName` on `androidApp` or `androidProjectConfiguration` — only on `androidBinary`
- `impl` → `native` (no matrix edge) — put JNI on `androidUtil`
