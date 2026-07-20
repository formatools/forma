---
name: forma-deps-catalog
description: House-style projectDependencies catalogs — library, bundle, plugin, bare GAV.
---

# Deps catalog (agent skill)

**House style (one global way):** `projectDependencies` in `settings.gradle.kts` →
`libs.*` + `deps(...)`. Do not teach typed `build-dependencies/` objects as an
equal default.

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

External GAV strings also work in `deps("group:artifact:version")` for tiny
examples **before** step 08. Prefer catalogs in real apps.

## Advanced (not default)

Hand-written typed catalogs (`androidx.*`, `google.*` under sample
`build-dependencies/`) are for large nested non-transitive graphs. See
DEPS-CATALOG §2. New projects and agent scaffolds: **catalog only**.

## Transitivity

`deps("g:a:v")` / bare catalog entries are **non-transitive**. Use
`transitiveDeps(...)` when Maven transitively is required (Compose UI,
JUnit/hamcrest, AppCompat clusters).
