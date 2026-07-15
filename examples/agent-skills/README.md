# Forma agent skills

Load these when scaffolding or reviewing Forma targets. They encode DSL, suffixes,
matrix rules, and progressive example pointers.

| Skill | When to load |
|-------|----------------|
| [forma-overview](forma-overview.md) | Any Forma work; entry map |
| [forma-android-targets](forma-android-targets.md) | Creating/editing Android targets |
| [forma-jvm-targets](forma-jvm-targets.md) | Pure JVM targets |
| [forma-dependency-matrix](forma-dependency-matrix.md) | Dependency / visibility errors |
| [forma-deps-catalog](forma-deps-catalog.md) | External deps / catalogs |
| [forma-compose](forma-compose.md) | Jetpack Compose |
| [forma-includer-settings](forma-includer-settings.md) | settings.gradle / includer / composite |
| [forma-project-layout](forma-project-layout.md) | Feature folders, packages, composition roots |
| [forma-bazel](forma-bazel.md) | Bazel adapter / sample |

**Hard rules agents must never violate**

1. `impl` must not depend on another `impl`
2. Composition roots list feature `api` **and** `impl` explicitly
3. Suffix of project name must match DSL (`…/impl` → `impl { }`)
4. `packageName` must match source root path
5. Do not weaken validators to “make it build”
