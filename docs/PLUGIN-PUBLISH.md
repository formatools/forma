# Publishing Forma plugins (F-016)

How Forma itself is published to the **Gradle Plugin Portal**, and how the
in-repo publish DSL is structured (GH #132). Creating the shared Portal
organization account (GH #133) is a human/admin step — this doc records the
expected ownership model and credential flow so workers do not invent secrets.

## What gets published

| Project | Plugin id | Notes |
|---------|-----------|--------|
| `plugins/:android` | `tools.forma.android` | Main product entrypoint |
| `plugins/:target` | `tools.forma.target` | Target identity |
| `plugins/:validation` | `tools.forma.validation` | Validators |
| `plugins/:owners` | `tools.forma.owners` | Ownership metadata |
| `plugins/:config` | `tools.forma.config` | Settings store |
| `plugins/:deps` | `tools.forma.deps` | Dep model + catalogs |
| `plugins/:core` | (library, no plugin id) | `tools.forma:core` Maven GAV (jar + sources jar) |
| `includer/` | `tools.forma.includer` | Separate build (own version) |
| `depgen/` | `tools.forma.depgen` | Separate build |

Current shared version for the `plugins/` multi-project: **`0.1.3`**
(see `formaPluginConfiguration` in `plugins/build.gradle.kts`).

Core and plugins share the version (overridable for local testing via `-PformaLocalVersion`).

`:android`’s `publishPlugins` task depends on sibling `publishPlugins` so a
single `:android:publishPlugins` publishes the whole set.

## Coordinates (F-024)

**`tools.forma:core`** is a **library** (plain Maven jar), **not** a Gradle plugin.
Android consumers continue to use the unchanged plugin id `tools.forma.android`.

### GAV and plugin ids

| Artifact | Coordinates | Type | Notes |
|----------|-------------|------|-------|
| forma-core | `tools.forma:core:0.1.3` (+ `-sources`) | Maven library | Single jar. Contains `tools.forma.core.target`, `.restriction`, `.validation`. |
| Android | `tools.forma.android` (Plugin Portal) | Gradle plugin | Primary entrypoint. Depends on core (transitive). |
| Facades (compat) | `tools.forma.target`, `.validation`, `.deps`, `.config`, `.owners` | Gradle plugins (thin) | Remain published; delegate/re-export core. |

Group `tools.forma`, version `0.1.3` (shared source of truth in root `formaPluginConfiguration`).

### Consumers

- **Normal Android apps** (recommended): use the plugin — no direct core dep needed.
  ```kotlin
  plugins {
      id("tools.forma.android") version "0.1.3"
  }
  ```
- **Pure engine / future JVM or Bazel** (F-030+): depend directly on the library
  when writing adapters that do not need the Android DSL:
  `implementation("tools.forma:core:0.1.3")`.
  (A future `tools.forma.jvm` plugin would also pull core.)

### How plugin POMs declare the core dependency

- Build files declare `implementation(project(":core"))` (and sibling facades).
- During `maven-publish` (via `com.gradle.plugin-publish` for plugins):
  matching GAV project dependencies are rewritten as external
  `tools.forma:core:0.1.3` (and peer plugins) in the published POM.
- `publishAllToMavenLocal` explicitly depends on `:core:publishToMavenLocal`
  first so local resolution succeeds for plugin POMs and markers.

### Single-jar confirmation (F-024)

`tools.forma:core` is published as **one jar** (plus sources). There is no
`core` + `core-gradle` split for the initial extraction.

### Owners status (F-024)

`:owners` stays a **sibling published plugin** (thin facade over metadata types).
No forced merge of sources into the core jar during F-024.

### Deprecation policy for facade plugin ids (F-024)

- `tools.forma.target`, `tools.forma.validation`, `.deps`, `.config`, `.owners`
  **remain published and supported** as thin facades for the 0.1.x series
  (current minor + at least one subsequent minor).
- **No removal or breaking change** to these ids in 0.1.x.
- Deprecation (docs + Portal metadata) will be introduced later — e.g. after
  JVM targets land (F-030) or after consumers have had a full minor of dual
  availability (`tools.forma.android` + direct core).
- Primary documented path: `id("tools.forma.android")` for Android; direct
  `tools.forma:core` for non-Android engine consumers.
- Facades provide a safe compat window; existing builds using them will not
  break in 0.1.x.

See also:
- [`docs/forma-core-api.md`](forma-core-api.md) (design + resolved open questions)
- [`docs/ARCHITECTURE.md`](ARCHITECTURE.md) §6 (extraction map updated for F-024)
- Consumer usage stays `includeBuild` for development; mavenLocal for external repros.

## Forma-style publish configuration (GH #132)

Duplicated `rootProject.ext[…]` + per-module `gradlePlugin { … }` blocks are
replaced by two helpers from `plugins/buildSrc`:

### 1. Root: `formaPluginConfiguration { … }`

```kotlin
// plugins/build.gradle.kts
formaPluginConfiguration {
    group = "tools.forma"
    version = "0.1.3"
    website = "https://forma.tools/"
    vcsUrl = "https://github.com/formatools/forma.git"
    displayName = "Forma - Meta Build System with Gradle and Android support"
    description = "Best way to structure your Gradle Project"
    tags = listOf("kotlin", "android", "structure", "target", "rules", "project")
}
```

### 2. Subproject: `formaPublishedPlugin(…)`

```kotlin
// plugins/android/build.gradle.kts (and target, validation, …)
plugins {
    id("com.gradle.plugin-publish")
    // + kotlin-dsl or kotlin("jvm") as needed
}

formaPublishedPlugin(
    name = "android",
    // optional overrides:
    // description = "…",
    // extraTags = listOf("agp"),
    // implementationClass = "tools.forma.android.plugin.FormaPlugin",
)
```

Defaults (matching historical Portal metadata):

| Field | Default |
|-------|---------|
| `id` | `{group}.{name}` → e.g. `tools.forma.android` |
| `implementationClass` | `{id}.plugin.FormaPlugin` |
| `displayName` / `description` / `tags` | from root `formaPluginConfiguration` |
| `website` / `vcsUrl` | from root config |
| `group` / `version` | from root config |

Helpers live in:

- `plugins/buildSrc/src/main/kotlin/FormaPluginPublishExtension.kt`
- `plugins/buildSrc/src/main/kotlin/formaPluginPublish.kt`

`includer` and `depgen` keep standalone `gradlePlugin { … }` blocks for now
(different versions / release cadence). They can adopt the same helpers later
if those builds are folded into a shared convention.

## Portal user / credentials (GH #133)

**Goal:** publish under a shared **Forma** Plugin Portal identity so the team
is not tied to a single personal Gradle account.

| Item | Status / expectation |
|------|----------------------|
| Portal account / org for Forma | **Human action** — create/claim on [plugins.gradle.org](https://plugins.gradle.org/) (not automatable without owner login) |
| Plugin ownership transfer | Existing `tools.forma.*` plugins may need transfer from the historical personal publisher to the Forma org (Portal UI) |
| Local publish keys | `~/.gradle/gradle.properties` (never commit): `gradle.publish.key`, `gradle.publish.secret` |
| CI publish | Optional later: GitHub Actions secrets + a `workflow_dispatch` publish job — **not** enabled by default |

Do **not** store Portal keys in the repo, in cron env files, or in
`docs/PROGRESS.md`. Workers may validate packaging without credentials
(see below).

### Operator checklist (human)

1. Create or claim the Forma Plugin Portal user/org (GH #133).
2. Generate a publish key pair for that identity.
3. Transfer or re-publish `tools.forma.android` (and siblings) under that
   identity as needed.
4. Distribute keys to trusted maintainers via password manager / CI secrets —
   not git.
5. Bump `formaPluginConfiguration.version` when cutting a release, then run
   the publish command below.

## Validate packaging (no credentials)

From a machine with JDK 17+ (`source scripts/env-mac.sh` on the worker Mac):

```bash
cd plugins
./gradlew build
# Server-side + local descriptor validation without uploading (plugin-publish 1.x):
./gradlew :android:publishPlugins --validate-only
```

`--validate-only` exercises plugin markers / metadata without requiring a
successful upload. A full release still needs Portal credentials:

```bash
cd plugins
./gradlew :android:publishPlugins
```

## Local publishing for testing (mavenLocal)

Use this when you want to consume Forma from **another** Gradle project **without**
`includeBuild("../plugins")` and **without** Portal credentials.

### Publish

```bash
# From repo root (recommended wrapper):
./scripts/publish-local.sh                 # version 0.1.3
./scripts/publish-local.sh 0.1.3-LOCAL     # recommended for experiments

# Or:
cd plugins
./gradlew publishAllToMavenLocal
./gradlew publishAllToMavenLocal -PformaLocalVersion=0.1.3-LOCAL
```

What gets installed under `~/.m2/repository/tools/forma/`:

| Artifact | Notes |
|----------|--------|
| `core` | JVM library (`tools.forma:core`) |
| `android`, `target`, `validation`, `owners`, `config`, `deps` | plugin jars + Gradle plugin markers |

`:core` is published first so `:android`’s Maven POM can resolve
`tools.forma:core` from mavenLocal.

**Do not** pass `-PformaLocalVersion` for real Portal releases — keep Portal
on the plain `0.1.3` (or next release) version string.

### Consume from a test project

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// build.gradle.kts or settings plugins {}
plugins {
    id("tools.forma.android") version "0.1.3-LOCAL"
}
```

Also put `mavenLocal()` in `dependencyResolutionManagement.repositories` (or
`buildscript.repositories` / project `repositories`) so `tools.forma:core`
and sibling plugin jars resolve.

### vs composite includeBuild

| Mode | When |
|------|------|
| `includeBuild("../plugins")` | Day-to-day Forma development (sample `application/`) — no publish step |
| `publishAllToMavenLocal` | External smoke tests, versioned consumer repros, AGP upgrade spikes |

## Consumer coordinates

Unchanged for apps:

```kotlin
plugins {
    id("tools.forma.android") version "0.1.3"
}
```

Badge / maven-metadata still point at the Plugin Portal
(`tools.forma.android`). Composite include of `plugins/` remains the
recommended path for Forma development (see
[`docs/GETTING-STARTED.md`](GETTING-STARTED.md)).

## Related tickets

| ID | Scope |
|----|--------|
| F-016 | This doc + `formaPluginConfiguration` / `formaPublishedPlugin` |
| GH #132 | Target-shaped publish config (implemented) |
| GH #133 | Shared Portal user (admin; documented, not automated) |
| F-024 | Publish coordinates, consumer docs, POM wiring, deprecation policy for `tools.forma:core` vs facades (implemented) |
| F-018 | Gradle/AGP modernization; use local publish for upgrade smoke tests |
