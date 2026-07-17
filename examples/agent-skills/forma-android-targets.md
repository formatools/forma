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
| `viewBinding` | `viewbinding` | **only** `layout*` under res | 03 |
| `library` | `library` | pure JVM library | 04 |
| `util` | `util` | no res/ | 04 |
| `androidUtil` | `android-util` | no res/ | 04 |
| `widget` | `widget` | Custom View | 05 |
| `uiLibrary` | `ui-library` | shared UI blocks | 05 |
| `composeWidget` | `compose-widget` | always Compose | 06 |
| `testUtil` | `test-util` | no res/ | 09 |
| `androidTestUtil` | `android-test-util` | Android test helpers | 09 |
| `androidNative` | `native` | no res/; **no project-dep validation yet** | docs only |

## Minimal skeletons

```kotlin
androidBinary(packageName = "…", versionCode = 1, versionName = "0.1.0", dependencies = deps(target(":root-app")))
androidApp(packageName = "…", dependencies = deps("androidx.appcompat:appcompat:1.6.1") + deps(target(":root-res")))
androidRes(packageName = "…")
api(packageName = "…")
impl(packageName = "…", dependencies = deps(target(":feature:x:api")))
viewBinding(packageName = "…", dependencies = deps(target(":feature:x:res")))
widget(packageName = "…")
composeWidget(packageName = "…", dependencies = deps(/* compose GAVs */))
// androidLibrary(…) // REMOVED F-063 — use androidUtil / uiLibrary / androidRes / …
androidUtil(packageName = "…")
util(packageName = "…")
library(packageName = "…")
testUtil(packageName = "…")
androidTestUtil(packageName = "…")
// androidNative(packageName = "…") // NDK; validators incomplete
```

## Forbidden

- `impl` → `impl`
- `api` shipping `res/`
- removed `androidLibrary` (use role-specific targets; F-063)
- Wrong suffix for DSL
- Relying on transitive feature wiring instead of listing api+impl on roots
