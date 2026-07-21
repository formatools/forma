---
name: forma-fleet-tooling
description: Check/generate/migrate package layout and path forms (F-084/F-088 fleet toolkit).
---

# Fleet tooling (agent skill)

Canonical design: [`docs/FLEET-TOOLING.md`](../../docs/FLEET-TOOLING.md).  
- Pure APIs: `tools.forma.core.fleet` in `plugins/core`
- Gradle shells: `tools.forma.deps.fleet` (`registerFormaLayout` from every `packageName` DSL)

## When to use

- Scaffolding a new target after writing `api` / `impl` / … with `packageName`
- CI or pre-commit layout check for `packageName` ↔ source dir
- Planning a module path rename (includer path + Gradle + `target()` refs)
- Bulk generate for the whole includer graph after adding many modules

## Rules

1. **One way:** use `PackageLayout` / `LayoutGenerator` / `LayoutChecker` / `MigratePlanner` — do not invent alternate path math. Gradle tasks are thin shells over the same APIs.
2. `packageName` must match `src/main/kotlin|java/<segments>`.
3. Includer: `feature/home/impl` → `:feature-home-impl`; Forma refs use `:feature:home:impl`.
4. Migrate planner is **plan-only** (string suggestions). Review before bulk replace; not AST-safe (deferred).
5. Still obey matrix / `impl` ↛ `impl` / no `androidLibrary` / type-owned plugins.
6. Do **not** turn on `checkPackageLayoutAtConfiguration` on the sample by default; use explicit generate/check tasks.
7. `androidRes` / `viewBinding` / `androidBinary` register layout metadata but **skip** check/generate (packageName is AGP identity only).

## Gradle tasks (preferred for day-to-day)

Per project (registered when the target DSL runs):

```bash
./gradlew :feature-home-api:formaLayoutCheck
./gradlew :feature-home-api:formaLayoutGenerate
# optional .gitkeep under empty package dir:
./gradlew :feature-home-api:formaLayoutGenerate -Pforma.layout.createPlaceholder=true
```

Whole graph (root aggregates):

```bash
./gradlew formaLayoutCheckAll
./gradlew formaLayoutGenerateAll
```

Opt-in configuration-time fail (Android only, default **false**):

```kotlin
androidProjectConfiguration(
    project = rootProject,
    // ...
    checkPackageLayoutAtConfiguration = true, // CI/strict only
)
```

## Core API snippets (scripts / offline)

```kotlin
import tools.forma.core.fleet.*

// GH #54 generate
LayoutGenerator.apply(
    GenerateLayoutRequest(
        moduleDir = Path.of("feature/character/list/api"),
        packageName = "com.stepango.blockme.character.list.api",
        createPlaceholder = true,
    ),
)

// check
val violations = LayoutChecker.checkPackageSourceDir(moduleDir, packageName)
check(violations.isEmpty()) { violations.joinToString { it.message } }

// rename plan
val plan = MigratePlanner.planRename(
    PathRenamePlan("feature/home/impl", "feature/dashboard/impl"),
)
// plan.filesystemMoves, plan.referenceRewrites
```

## Related skills

- [forma-includer-settings](forma-includer-settings.md) — discovery / include
- [forma-project-layout](forma-project-layout.md) — feature folders
- [forma-android-targets](forma-android-targets.md) / [forma-jvm-targets](forma-jvm-targets.md)
