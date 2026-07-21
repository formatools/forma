# Fleet tooling — check / generate / migrate (F-084)

**Status:** v1 toolkit shipped in `tools.forma:core` (`tools.forma.core.fleet`).  
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

| Mode | Job | v1 entry |
|------|-----|----------|
| **check** | Fail when layout does not match declared `packageName` (CI-friendly pure API) | `LayoutChecker` |
| **generate** | Create missing `src/main/{java\|kotlin}/…` trees from `packageName` | `LayoutGenerator` |
| **migrate** | Plan module path renames + suggested reference rewrites (not AST rewrite) | `MigratePlanner` |

Do **not** invent a second parallel layout convention per team. Extend these
helpers (or thin Gradle tasks that call them), not ad-hoc scripts with different
path rules.

## Tool map

| Tool | Role today | Notes |
|------|------------|--------|
| **includer** (`includer/`) | **Discover** modules: any dir with `build.gradle(.kts)` is included; nested `settings` skipped | Naming: `feature/home/impl` → Gradle `:feature-home-impl` |
| **forma-core validators** | **Runtime check** of content rules + restriction graph | Configuration-time; not generate |
| **`tools.forma.core.fleet`** (this doc) | **Offline** check / generate / migrate-plan | Pure Kotlin, unit-tested, no Gradle APIs |
| **bazel-adapter** | Bazel **generate** + **check** from `FormaProjectModel` | F-041; JVM-first; see [`BAZEL-ADAPTER.md`](BAZEL-ADAPTER.md) |
| **depgen** (`depgen/`) | Intended transitive external-deps generation | **Stub** plugin + unfinished task; not on sample critical path; not part of F-084 v1 |

## Path conventions (source of truth)

| Form | Example | Producer |
|------|---------|----------|
| Filesystem relative dir | `feature/home/impl` | Repo layout + includer walk |
| Gradle project path | `:feature-home-impl` | Includer (`/` → `-`) |
| Forma target ref | `:feature:home:impl` | `target(":feature:home:impl")` |
| Package source dir | `src/main/kotlin/com/foo/bar` | `packageName` attribute |

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

### Using from a one-off script / future task

1. Depend on `tools.forma:core` (same version as Forma plugins).
2. Call pure APIs above (temp dirs in tests; real module dirs in tools).
3. Wire a Gradle task later if desired — **logic stays in core**, task is a thin shell.

v1 does **not** ship a Gradle plugin task or CLI binary; the library + tests are
the product surface so agents and future tasks share one implementation.

## What v1 ships vs follow-ups

**Shipped (F-084 v1)**

- Design (this doc) + agent skill
- Pure `check` / `generate` / `migrate-plan` + path forms in `plugins/core`
- Unit tests including GH #54 package example
- Cross-links from README / VISION / GETTING-STARTED

**Follow-ups (not blocking F-084 done)** — board: **F-088**

- Gradle `formaLayoutCheck` / `formaLayoutGenerate` tasks on Android/JVM platforms
- Optional configuration-time hook: fail if `packageName` dir missing (opt-in)
- AST-safe build-script rewrite for migrate (IntelliJ/PSI or Kotlin compiler)
- packageName rename paired with directory move
- depgen resurrection or retire (separate decision)
- Deeper bazel-adapter integration with the same `FormaProjectModel` export
- Bulk “generate missing trees for whole includer graph” driver

## Non-goals (v1)

- Full monorepo rewrite engine
- Teaching a second layout style beside includer + `packageName`
- Android `res/` scaffolding (content rules already constrain res targets)
- Finishing depgen transitive generation

## Agent skill

See [`examples/agent-skills/forma-fleet-tooling.md`](../examples/agent-skills/forma-fleet-tooling.md).

## Verify

```bash
source scripts/env-mac.sh
cd plugins && ./gradlew :core:test
# or full
./gradlew build
```
