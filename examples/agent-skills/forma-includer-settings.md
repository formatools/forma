---
name: forma-includer-settings
description: settings.gradle.kts, includer, composite includeBuild patterns.
---

# Includer & settings (agent skill)

## Composite monorepo (examples / samples)

```kotlin
pluginManagement {
  repositories { google(); gradlePluginPortal(); mavenCentral() }
  includeBuild("../../../plugins")   // depth depends on location
  includeBuild("../../../includer")
}
plugins {
  id("tools.forma.includer")
  id("tools.forma.android") // or tools.forma.jvm
}
includer { arbitraryBuildScriptNames = true }
```

## Rules

- Drop `build.gradle.kts` in a folder → project discovered (no manual `include`)
- Nested `settings.gradle.kts` skips that tree
- Path `feature/home/impl` → Gradle `:feature-home-impl`; Forma `target(":feature:home:impl")`
- Android root needs `androidProjectConfiguration` in root `build.gradle.kts`
