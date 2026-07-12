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
| `includer/` | `tools.forma.includer` | Separate build (own version) |
| `depgen/` | `tools.forma.depgen` | Separate build |

Current shared version for the `plugins/` multi-project: **`0.1.3`**
(see `formaPluginConfiguration` in `plugins/build.gradle.kts`).

`:android`’s `publishPlugins` task depends on sibling `publishPlugins` so a
single `:android:publishPlugins` publishes the whole set.

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
| F-024 | Coordinate plan when `forma-core` is extracted (`tools.forma:core` vs android plugins) |
