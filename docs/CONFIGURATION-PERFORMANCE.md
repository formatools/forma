# Configuration-time performance (F-017)

Forma should keep **Gradle configuration** cheap so large multi-module Android
graphs stay interactive. This note captures what we measure, what we changed
for GH #106 / #42, and what to avoid when extending Forma.

## How to measure (sample `application/`)

From a clean daemon + no configuration cache:

```bash
source scripts/env-mac.sh
cd application
./gradlew --stop
rm -rf .gradle/configuration-cache
./gradlew help --no-configuration-cache --profile --offline
# open build/reports/profile/profile-*.html
# look at "Configuring Projects"
```

Warm / configuration-cache path:

```bash
./gradlew help --profile --offline
# second run should reuse configuration cache when inputs are unchanged
```

Optional: `./gradlew help --scan` when a Build Scan account is available
(GH #42). Local `--profile` is enough for regression checks on this host.

### Profile anchors on worker host (2026-07-12)

Absolute numbers move with machine load, daemon state, and composite plugin
recompilation. Prefer same-machine before/after pairs.

| Run | Configuring projects (before → after F-017) | Notes |
|-----|---------------------------------------------|-------|
| Daemon warm, `help --no-configuration-cache --offline` | ~4.8s → **~1.1s** (best pair) | first cold after heavy load can spike |
| `help` with configuration cache | ~0.7–1.3s | sample already enables CC |

Treat F-017 as **allocation / hot-path** work (validators, deps, feature
definitions, skip empty repo blocks), not as a claim of a fixed wall-clock %.

## What costs configuration time in Forma

1. **Per-target work** — every `api` / `impl` / `androidLibrary` / … call:
   - suffix validation for self + each project dependency
   - AGP / Kotlin plugin apply + extension configuration
   - dependency registration (`applyDependencies`)
2. **Dependency list plumbing** — `map` / `filter` / `flatMap` on dep specs
   allocates intermediate `ArrayList`s (GH #106).
3. **Repeated repository configuration** — historically each target invoked
   `Forma.settings.repositories` on `project.repositories`. Prefer settings /
   `dependencyResolutionManagement` (sample already does); Forma no longer
   re-applies empty or default repo lambdas per module.
4. **Composite builds** — sample includes `plugins/`, `includer/`,
   `build-settings/`, `build-dependencies/` (settings + classpath). That cost
   is paid once per cold daemon, not per target, but dominates small samples.
5. **Includer** — uses flat module names (`path` with `-` separators) to avoid
   Gradle intermediate projects (see `IncluderPlugin`).

## Changes shipped in F-017

| Area | Change |
|------|--------|
| `validator(...)` | Identity-cache single- and multi-suffix validators; hot path uses precomputed `endsWith` strings, no intermediate `map`/`contains` lists |
| `kotlinFeatureDefinition` / `kotlinAndroidFeatureDefinition` / kapt | Singleton `FeatureDefinition` instances; read live `Forma.settings` at apply time |
| `applyDependencies` | Skip `repositories {}` when config is the empty sentinel; skip entire `dependencies {}` block when all three dep args are `EmptyDependency`; skip plugin lookup when no plugin deps registered |
| `deps` / `FormaDependency.plus` | Prefer typed fields over `filterIsInstance`; pre-size / single-pass merges; indexed `forEach` over specs |
| Catalog generators | `filteredTokens` is a `Set` for O(1) membership |
| Android targets | Drop redundant `repositoriesConfiguration = Forma.settings.repositories` (default empty) |

## Guidance for contributors

**Do**

- Reuse shared validators / feature definitions instead of allocating per call.
- Prefer `FormaDependency.forEach` over materializing `.names` / `.targets` /
  `.files` when applying deps.
- Put repositories in **settings** (`dependencyResolutionManagement` /
  `pluginManagement`), not in every subproject.
- Keep content validators (`onlyAllowResources`, …) cheap — they touch the
  filesystem once per target that needs them.
- Profile cold configuration after non-trivial plugin changes.

**Avoid**

- Configuration-time `println` / logging in hot generators (removed in F-012).
- Building new `List` pipelines on every dependency edge.
- Calling `project.repositories { … }` from each target with the same block.
- Nested Gradle projects named with `:` path separators (Includer already
  flattens this).

## Configuration cache & build cache

Sample `application/gradle.properties` already enables:

- `org.gradle.caching=true`
- `org.gradle.parallel=true`
- `org.gradle.configureondemand=true`
- configuration-cache (unsafe flags for Gradle 8.4)

Forma still uses a process-wide `FormaSettingsStore` singleton. That is
configuration-cache friendly only when settings are written during settings /
root buildscript evaluation and read during project configuration of the same
build — which is the supported layout. Do not mutate the store from task
actions.

## Related

- GH #106 Optimize configuration time
- GH #42 Build performance impact
- `docs/ARCHITECTURE.md` — module map
- `docs/DEPS-CATALOG.md` — pure generators
- Ticket **F-017**
