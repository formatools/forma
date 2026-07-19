---
name: forma-compose
description: Compose flags and composeWidget target.
---

# Compose (agent skill)

Doc: `docs/COMPOSE.md`. Example: `examples/android/06-compose`.

```kotlin
// root
androidProjectConfiguration(..., compose = true, composeCompilerVersion = "2.0.21")

impl(..., compose = true, dependencies = transitiveDeps("androidx.compose.ui:ui:…", /* … */))
composeWidget(packageName = "…", dependencies = transitiveDeps(/* compose libs */))
androidBinary(..., compose = true, ...)
```

- Forma enables `buildFeatures.compose` + compiler extension version
- **You** still add Compose Maven artifacts
- Align compiler version with Kotlin (sample: 2.0.21 ↔ 2.0.21)
- **`deps("gav")` is non-transitive** — use `transitiveDeps(...)` for Compose (same as `androidx.compose` in `build-dependencies/`)
