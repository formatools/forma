# Buildscript classpath: projects, composites, and `extraPlugins` (F-100)

**Ticket:** F-100 / GH #111  
**Invariant (F-082):** `extraPlugins` is **buildscript classpath only** — it never
applies plugins to modules. Type-owned apply stays in
[`TARGET-PLUGINS.md`](TARGET-PLUGINS.md).

**Classifier:** `tools.forma.config.BuildscriptClasspath` (shared by Android and KMP).

---

## Supported shapes for `extraPlugins`

Pass a `List<Any>` to `androidProjectConfiguration(extraPlugins = …)` or
`kmpProjectConfiguration(extraPlugins = …)`. Each entry is resolved by
`BuildscriptClasspath.resolve` before `classpath(...)`.

| Shape | Example | Notes |
|-------|---------|--------|
| **String GAV** | `"androidx.navigation:navigation-safe-args-gradle-plugin:2.9.8"` | Direct Maven coordinate |
| **Catalog `plugin(...)`** | `libs.plugins.navigationSafeArgs` | `Provider<PluginDependency>` → `pluginId:strictVersion` |
| **Bare `PluginDependency`** | (unusual; catalog wraps in `Provider`) | Same string conversion |
| **`Provider<String>`** | version-catalog or custom provider of GAV | Resolved at configuration time |
| **`File` / `FileCollection`** | prebuilt plugin jar(s) | Escape hatch only; you own the jar lifecycle |
| **External module `Dependency`** | rare hand-built module deps | Not `ProjectDependency` |
| **Map module notation** | `mapOf("group" to …, "name" to …, "version" to …)` | Gradle module map |

**House style (recommended):** catalog `plugin("group:artifact", version)` +
`includeBuild` for local convention plugins. See sample and example 10 below.

---

## Same-build `project(":…")` — not supported

### Spike results (Gradle **9.6.1**)

| Attempt | Result |
|---------|--------|
| `buildscript { dependencies { classpath(project(":plugin")) } }` | **Fails:** `Project dependencies cannot be declared here.` |
| `classpath(rootProject.project(":plugin"))` | **Fails** configuring classpath (class-loader scope must be locked / project graph ordering) |
| `includeBuild("plugin-build")` + `classpath("group:artifact:version")` with composite substitution | **Works** — plugin classes visible on buildscript classpath |
| String GAV from Maven | **Works** (baseline) |

Root `buildscript { }` is evaluated **before** the same-build project dependency
graph is a legal source for that classpath. This is a **Gradle platform limit**,
not a Forma policy choice. Forma therefore **rejects** `Project` /
`ProjectDependency` in `extraPlugins` with an actionable error pointing here,
instead of letting Gradle fail later with a shorter message.

### What is **not** supported (and why)

| Not supported | Why |
|---------------|-----|
| `extraPlugins = listOf(project(":my-convention"))` | Gradle forbids project deps on root buildscript classpath |
| Passing a `Project` reference into `extraPlugins` | Same lifecycle / class-loader issue |
| Teaching raw `buildscript { dependencies { classpath(project(…)) } }` as the happy path | Dual path vs `androidProjectConfiguration`; still broken for same-build |
| Using `extraPlugins` to **apply** plugins | F-082 — classpath only; apply via type-owned registry |
| Per-module plugin shopping / `.withPlugin` | Removed (F-081); see TARGET-PLUGINS |

---

## Recommended house style: local convention plugins

**Do this** (status quo in the monorepo samples):

1. Put the convention / Path B plugin in an **included build**
   (`includeBuild("forma-defs")` or `includeBuild("../build-dependencies")`).
2. Publish or expose a **Maven-style plugin coordinate** from that included build
   (`group` + plugin marker / jar coordinates).
3. Declare the coordinate once in settings catalog:
   `plugin("tools.forma.examples:forma-defs", "0.0.1")`.
4. Put the catalog accessor on the root classpath only:
   ```kotlin
   buildscript {
       androidProjectConfiguration(
           project = rootProject,
           // ...
           extraPlugins = listOf(
               libs.plugins.toolsFormaExamplesDefs,
               libs.plugins.navigationSafeArgs,
           ),
       )
   }
   ```
5. **Apply** via type-owned registration (`targetPlugin` + `deriveTargetType` /
   `registerTargetPlugin`) — never from `extraPlugins`.

**References in this repo:**

| Consumer | Included build | Classpath entry |
|----------|----------------|-----------------|
| `application/` | `../build-dependencies` | `libs.plugins.toolsFormaDemoDependencies` |
| `examples/android/10-target-plugins` | `./forma-defs` | `libs.plugins.toolsFormaExamplesDefs` |

Composite builds substitute the GAV so you do **not** need `mavenLocal` for day-to-day
iteration on the included plugin.

### Optional: prebuilt jars via `files`

If you already produce a plugin jar outside the project graph, `File` /
`FileCollection` entries are accepted. Prefer includeBuild + GAV for anything
built from source in the monorepo — `files` does not rebuild or substitute.

---

## includeBuild vs `pluginManagement` includeBuild

| Mechanism | Role for Forma |
|-----------|----------------|
| `pluginManagement { includeBuild("../plugins") }` | Resolves **Forma’s own** settings/project plugins (`tools.forma.android`, …) |
| Root `includeBuild("forma-defs")` + catalog GAV in `extraPlugins` | Puts **your** convention plugin **implementation jars** on the **buildscript** classpath so type-owned `apply` can load classes |

These are related but not identical: settings plugin resolution ≠ buildscript
classpath for implementation jars used when applying plugins from target DSLs.

---

## API surface after F-100

- **Shared resolver:** `BuildscriptClasspath.resolve(entry, contextLabel)`
- **Android:** `buildScriptConfiguration` → resolver (`contextLabel = "extraPlugins"`)
- **KMP:** `kmpBuildscriptClasspath` → same resolver
  (`contextLabel = "kmpProjectConfiguration extraPlugins"`)
- **Errors:** `Project` / `ProjectDependency` → `IllegalArgumentException` with
  includeBuild recipe + link to this doc; unsupported `Provider` value types fail
  fast with the supported list

No new dual happy path. No change to type-owned apply.

---

## Cross references

- [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) — single config path + `extraPlugins`
- [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md) — classpath vs apply
- [`GETTING-STARTED.md`](GETTING-STARTED.md) — tutorial
- [`DEPS-CATALOG.md`](DEPS-CATALOG.md) — `plugin(...)` catalog factory
- Sample: `application/build.gradle.kts` + `application/settings.gradle.kts`
- Example: `examples/android/10-target-plugins`
