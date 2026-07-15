---
name: forma-deps-catalog
description: projectDependencies catalogs — library, bundle, plugin, bare GAV.
---

# Deps catalog (agent skill)

Doc: `docs/DEPS-CATALOG.md`. Example: `examples/android/08-deps-catalog`.

```kotlin
// settings.gradle.kts
projectDependencies(
  "libs",
  "com.jakewharton.timber:timber:5.0.1",
  library("androidx.appcompat:appcompat:1.6.1", name = "appcompat"),
  bundle(name = "uiBasics", "com.google.android.material:material:1.10.0"),
  plugin("androidx.navigation:navigation-safe-args-gradle-plugin", "2.7.4"),
)

// module
dependencies = deps(libs.appcompat, libs.jakewhartonTimber, libs.bundles.uiBasics)
```

External GAV strings also work in `deps("group:artifact:version")` for tiny examples.
Prefer catalogs in real apps.

## Transitivity

`deps("g:a:v")` is **non-transitive**. Use `transitiveDeps(...)` when Maven transitively is required (Compose UI, JUnit/hamcrest, AppCompat clusters).
