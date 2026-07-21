# Fleet tooling — check / generate / migrate (F-084 + F-088)

**Status:** v1 pure APIs in `tools.forma:core` (`tools.forma.core.fleet`) **and** phase-2 Gradle shells (F-088).  
Related: principle **3** in [`VISION.md`](VISION.md), GH **#54**, includer, bazel-adapter generate/check.

Forma keeps structure **explicit**. At thousands of modules that only works if
**tooling** can verify and reshape the graph without hand-editing every path.

## Problem

- Renames, type migrations, and new targets touch filesystem layout, Gradle
  project names (includer), Forma `target(":…")` refs, and `packageName` source
  roots together.
- Runtime validators (`ContentRule`, dependency matrix) catch some mistakes
  **after** configuration — too late for bulk refactors and greenfield scaffolding.
- GH #54: from a minimal declaration such as

  ```kotlin
  api(
      packageName = "com.stepango.blockme.character.list.api",
      owner = Teams.core,
  )
  ```

  the package directory `src/main/kotlin/com/stepango/blockme/character/list/api`
  should be **generatable**, not hand-created.

## One global way: three modes

| Mode | Job | Entry |
|------|-----|-------|
| **check** | Fail when layout does not match declared `packageName` | Core `LayoutChecker` · Gradle `formaLayoutCheck` / `formaLayoutCheckAll` |
| **generate** | Create missing `src/main/{java\|kotlin}/…` trees from `packageName` | Core `LayoutGenerator` · Gradle `formaLayoutGenerate` / `formaLayoutGenerateAll` |
| **migrate** | Plan module path renames + suggested reference rewrites (not AST rewrite) | Core `MigratePlanner` (plan-only; AST rewrite deferred) |

Do **not** invent a second parallel layout convention per team. Extend these
helpers (or thin Gradle tasks that call them), not ad-hoc scripts with different
path rules.

## Tool map

| Tool | Role today | Notes |
|------|------------|--------|
| **includer** (`includer/`) | **Discover** modules: any dir with `build.gradle(.kts)` is included; nested `settings` skipped | Naming: `feature/home/impl` → Gradle `:feature-home-impl` |
| **forma-core validators** | **Runtime check** of content rules + restriction graph | Configuration-time; not generate |
| **`tools.forma.core.fleet`** | **Offline** check / generate / migrate-plan | Pure Kotlin, unit-tested, no Gradle APIs |
| **`tools.forma.deps.fleet`** (F-088) | **Gradle shells** + layout metadata on every `packageName` DSL | Thin tasks over core; shared Android + JVM |
| **bazel-adapter** | Bazel **generate** + **check** from `FormaProjectModel` | F-041; JVM-first; see [`BAZEL-ADAPTER.md`](BAZEL-ADAPTER.md) |
| **depgen** (`depgen/`) | Intended transitive external-deps generation | **Stub** plugin + unfinished task; not on sample critical path; **deferred** (not resurrected in F-088) |

## Path conventions (source of truth)

| Form | Example | Producer |
|------|---------|----------|
| Filesystem relative dir | `feature/home/impl` | Repo layout + includer walk |
| Gradle project path | `:feature-home-impl` | Includer (`/` → `-`) |
| Forma target ref | `:feature:home:impl` | `target(":feature:home:impl")` |
| Package source dir | `src/main/kotlin/com.foo.bar` | `packageName` attribute |

Helpers: `ProjectPathForms`, `PackageLayout`.

**Caveat:** reversing `:feature-home-impl` → `feature/home/impl` is **ambiguous**
when a path segment itself contains `-`. Prefer the filesystem relative path as
the canonical key for migrate plans.

## API (forma-core)

Package: `tools.forma.core.fleet`

```kotlin
// Package → directories
PackageLayout.segments("com.foo.bar")
PackageLayout.sourceDir("com.foo.bar") // src/main/kotlin/com/foo/bar
PackageLayout.sourceDir("com.foo.bar", SourceLanguage.JAVA)

// Path forms (includer-aligned)
ProjectPathForms.gradleProjectPath("feature/home/impl") // :feature-home-impl
ProjectPathForms.formaTargetPath("feature/home/impl")   // :feature:home:impl

// Generate (GH #54)
LayoutGenerator.apply(
    GenerateLayoutRequest(
        moduleDir = modulePath,
        packageName = "com.stepango.blockme.character.list.api",
        language = SourceLanguage.KOTLIN,
        createPlaceholder = true, // optional .gitkeep
    ),
)

// Check
LayoutChecker.checkPackageSourceDir(modulePath, "com.foo.bar") // empty = OK

// Migrate plan (no IO)
MigratePlanner.planRename(
    PathRenamePlan("feature/home/impl", "feature/dashboard/impl"),
)
// → filesystemMoves + referenceRewrites (:gradle, :forma, target(), project())
```

Logic **stays in core**. Gradle tasks are thin shells only.

## Gradle tasks (F-088 phase 2)

Every Android and JVM target DSL that takes `packageName` calls
`Project.registerFormaLayout(packageName)` (`tools.forma.deps.fleet`):

1. Stores typed metadata on the project: extension `formaLayout` /
   `FormaLayoutExtension` (`packageName` + `SourceLanguage`, default KOTLIN).
2. Registers per-project tasks:
   - **`formaLayoutCheck`** — for code targets, package dir must exist under **either**
     `src/main/kotlin/…` or `src/main/java/…` (sample Android trees use the java root
     for Kotlin sources). Fails if missing under both. **Skipped** (success) for
     AGP-identity-only targets: `androidRes` / `resourcesTarget`, `viewBinding`,
     `androidBinary` (`requirePackageSourceDir = false`).
   - **`formaLayoutGenerate`** — `LayoutGenerator.apply(...)` for code targets; logs
     created dirs. Prefers an existing conventional root (`java` vs `kotlin`);
     otherwise default language KOTLIN. Optional placeholder via project property
     **`forma.layout.createPlaceholder=true`** (default **false**). **Skipped** for
     AGP-identity-only targets (same set as check skip) so bulk generate does not
     drop empty kotlin trees into res/binary modules.
3. Idempotent if the helper is invoked twice on the same project.

Root aggregates (registered from `androidProjectConfiguration` and/or first layout registration):

| Task | Behavior |
|------|----------|
| **`formaLayoutCheckAll`** | `dependsOn` every subproject `formaLayoutCheck` that registered metadata |
| **`formaLayoutGenerateAll`** | `dependsOn` every subproject `formaLayoutGenerate` that registered metadata |

### Optional configuration-time check

On `androidProjectConfiguration` / `AndroidProjectSettings`:

```kotlin
androidProjectConfiguration(
    project = rootProject,
    // ...
    checkPackageLayoutAtConfiguration = false, // default — leave off for sample green
)
```

When **`true`**, after a target registers layout metadata, if the package source
dir is missing → `logger.error` + `GradleException` at **configuration** time.

- **Opt-in** CI/dev strictness only.
- Happy path for scaffolding remains **`formaLayoutGenerate`** / **`formaLayoutGenerateAll`**.
- Pure JVM projects without Android settings skip the config-time hook (no second configuration path).

### Verify commands

```bash
source scripts/env-mac.sh
cd plugins && ./gradlew :core:test build

cd application
./gradlew help
./gradlew :feature-home-api:formaLayoutCheck
./gradlew formaLayoutCheckAll
# scaffold missing trees (explicit; does not run on normal build):
./gradlew :some-module:formaLayoutGenerate
./gradlew formaLayoutGenerateAll
# optional placeholder files:
./gradlew formaLayoutGenerateAll -Pforma.layout.createPlaceholder=true
```

Do **not** enable `checkPackageLayoutAtConfiguration` on the sample by default.

## What ships vs deferred

**Shipped (F-084 v1)**

- Design (this doc) + agent skill
- Pure `check` / `generate` / `migrate-plan` + path forms in `plugins/core`
- Unit tests including GH #54 package example

**Shipped (F-088 phase 2)**

- Gradle `formaLayoutCheck` / `formaLayoutGenerate` on every `packageName` target
- Root `formaLayoutCheckAll` / `formaLayoutGenerateAll`
- Opt-in `checkPackageLayoutAtConfiguration` (default false)
- Docs + agent skill task usage
- GH **#54** generate path is user-complete via Gradle tasks (close when merged)

**Still deferred**

- AST-safe build-script rewrite for migrate (IntelliJ/PSI or Kotlin compiler)
- packageName rename paired with directory move
- depgen resurrection or retire (separate decision)
- Deeper bazel-adapter integration with the same `FormaProjectModel` export

## Non-goals

- Full monorepo rewrite engine
- Teaching a second layout style beside includer + `packageName`
- Android `res/` scaffolding (content rules already constrain res targets)
- Finishing depgen transitive generation
- Mass-creating missing dirs during normal `build` (tasks are explicit)

## Agent skill

See [`examples/agent-skills/forma-fleet-tooling.md`](../examples/agent-skills/forma-fleet-tooling.md).

## Verify (plugins unit + shells compile)

```bash
source scripts/env-mac.sh
cd plugins && ./gradlew :core:test build
```
