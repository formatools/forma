# Forma agent skills

Load these when scaffolding or reviewing Forma targets. They encode DSL, suffixes,
matrix rules, and progressive example pointers.

| Skill | When to load |
|-------|----------------|
| [forma-overview](forma-overview.md) | Any Forma work; entry map |
| [forma-android-targets](forma-android-targets.md) | Creating/editing Android targets |
| [forma-jvm-targets](forma-jvm-targets.md) | Pure JVM targets |
| [forma-kmp-targets](forma-kmp-targets.md) | KMP shared libraries (`kmpLibrary`, platforms once) |
| [forma-dependency-matrix](forma-dependency-matrix.md) | Dependency / visibility errors |
| [forma-deps-catalog](forma-deps-catalog.md) | External deps / catalogs |
| [forma-target-plugins](forma-target-plugins.md) | External plugins on types (Path A/B); never `.withPlugin` |
| [forma-compose](forma-compose.md) | Jetpack Compose |
| [forma-includer-settings](forma-includer-settings.md) | settings.gradle / includer / composite |
| [forma-project-layout](forma-project-layout.md) | Feature folders, packages, composition roots |
| [forma-fleet-tooling](forma-fleet-tooling.md) | check/generate/migrate layout + path forms (F-084) |
| [forma-bazel](forma-bazel.md) | Bazel adapter / sample |

**Hard rules agents must never violate**

1. `impl` must not depend on another `impl`
2. Composition roots list feature `api` **and** `impl` explicitly
3. Suffix of project name must match DSL (`…/impl` → `impl { }`)
4. `packageName` must match source root path
5. Do not weaken validators to “make it build”
6. No `.withPlugin` / free-form plugin id lists / restored `androidLibrary`
7. One global way per concern — house-style deps = `projectDependencies` → `libs.*`; type owns plugins
8. Call sites = attributes only (`docs/CALL-SITE-SURFACE.md`); see `docs/PRINCIPLE-AUDIT.md`
