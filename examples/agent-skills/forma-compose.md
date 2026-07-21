---
name: forma-compose
description: Compose flags and composeWidget target.
---

# Compose (agent skill)

Doc: `docs/COMPOSE.md`. Example: `examples/android/06-compose`.

```kotlin
// root
androidProjectConfiguration(..., compose = true, composeCompilerVersion = "2.3.21")

impl(..., compose = true, dependencies = transitiveDeps("androidx.compose.ui:ui:…", /* … */))
composeWidget(packageName = "…", dependencies = transitiveDeps(/* compose libs */))
androidBinary(..., compose = true, ...)
```

- Forma enables `buildFeatures.compose` + compiler extension version
- **You** still add Compose Maven artifacts
- Align compiler version with Kotlin (sample: 2.3.21 ↔ 2.3.21)
- **`deps("gav")` is non-transitive** — use `transitiveDeps(...)` for Compose
- Tiny ladder steps may inline GAV strings; real apps use house-style
  `projectDependencies` → `libs.*` ([DEPS-CATALOG.md](../../docs/DEPS-CATALOG.md)).
  Prefer `composeWidget` when the module is Compose-only UI (type owns Compose on).
