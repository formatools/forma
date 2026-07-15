# Getting started — JVM project with Forma (F-032)

Forma is a **meta build system** for pure JVM (Gradle plugin). You declare
**targets** with typed, single-method builders (`api`, `impl`, `binary`, …)
instead of hand-wiring Kotlin JVM plugins, application configuration, and
ad-hoc dependency rules. Forma applies plugins and **dependency visibility
validation** for you.

This tutorial gets you from zero to a working multi-target pure-JVM app. For
the full multi-feature reference layout, see [JVM-SAMPLE.md](JVM-SAMPLE.md).

---

## 1. Mental model (5 minutes)

| Concept | Meaning |
|---------|---------|
| **Target** | One Gradle project / module with a Forma type (`impl`, `api`, …). Prefer “target” over “module” in Forma docs. |
| **Suffix** | Folder / project name ending that encodes type (`…-impl`, `…-api`, `binary`, …). Validators use suffixes. |
| **Composition root** | `binary` — where feature graphs are wired (entry point for `main`). |
| **Feature slice** | Typical feature folder: `api` + `impl`. |
| **Catalogs** | External deps declared once (`projectDependencies` / typed catalogs), consumed via `deps(...)`. |

**What Forma does for each target**

1. Validates the project name matches the expected suffix.
2. Applies the right Kotlin JVM plugins (and Gradle `application` for `binary`).
3. Enforces **project** dependencies against the [JVM dependency matrix](JVM-TARGETS.md).

You still write Kotlin and choose library coordinates — Forma owns
structure and boundaries, not business logic.

---

## 2. Prerequisites

| Tool | Version / notes |
|------|-----------------|
| JDK | **17+** (Temurin / OpenJDK recommended) |
| Android SDK | **Not required** (pure JVM; the Android sample still needs it) |
| Build tools | Gradle wrapper (sample uses 8.x) |

Worker / macOS install steps: [ENV.md](ENV.md) (`source scripts/env-mac.sh` — it exports `JAVA_HOME`; Android-related warnings are harmless for JVM-only work).

```bash
# After JDK is installed:
source scripts/env-mac.sh   # if using this repo’s helper
```

No `local.properties` or SDK setup is needed for pure-JVM projects.

---

## 3. Path A — run the sample first (recommended)

The `jvm-application/` directory is the **gold-standard pure-JVM product**.
Build and run it before starting a greenfield project so you see real targets
and wiring.

```bash
git clone https://github.com/formatools/forma.git
cd forma
source scripts/env-mac.sh   # or export JAVA_HOME yourself

cd jvm-application
./gradlew :binary:run
# or full verification:
./gradlew build
```

Expected output:

```
Hello, World! (2 + 3 = 5)
JVM sample (tools.forma.jvm) build + run successful.
```

Open `jvm-application/` in your IDE (import the `jvm-application` Gradle project).
Explore:

| Path | Target | Role |
|------|--------|------|
| `binary/` | `binary` | Composition root + runnable entry (`:binary:run`) |
| `feature/greeter/{api,impl}` | feature slice | Public contracts + impl |
| `feature/calculator/{api,impl}` | feature slice | Second feature (cross-feature via api only) |
| `common/library/` | `library` | Shared pure JVM code |
| `common/util/` | `util` | Small helpers |
| `common/test-util/` | `testUtil` | Shared test helpers (used via `testDependencies`) |
| `settings.gradle.kts` | — | Plugin management + composite includes |

Deep dive: [JVM-SAMPLE.md](JVM-SAMPLE.md).

---

## 4. Path B — greenfield project skeleton

You can consume Forma from the **Gradle Plugin Portal** or via **composite
`includeBuild`** (as this monorepo does for development).

Published plugin id: `tools.forma.jvm` (badge version on the [README](../README.md);
sample monorepo develops **0.1.3** line). Companion settings plugin:
`tools.forma.includer` (auto-`include` subprojects that contain `build.gradle.kts`).

### 4.1 `settings.gradle.kts`

Minimal shape (Portal publish path — adjust version to the badge):

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("tools.forma.includer") version "0.2.0"
    id("tools.forma.jvm") version "0.1.3"
}

// Discover every build.gradle.kts under this root (skip nested settings.gradle*).
includer {
    // true when folder names are not forced to end in the suffix only
    // (sample uses nested paths like feature/greeter/impl).
    arbitraryBuildScriptNames = true
}

rootProject.name = "my-jvm-app"

// Optional: settings-level version catalog — see docs/DEPS-CATALOG.md
// projectDependencies("libs", "com.squareup.okhttp3:okhttp:4.12.0", …)
```

**Monorepo / composite** (how `jvm-application/` is wired today):

```kotlin
pluginManagement {
    repositories { gradlePluginPortal(); mavenCentral() }
    includeBuild("../plugins")
    includeBuild("../includer")
    includeBuild("../build-settings")
    apply(from = "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
}
plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.jvm")
}
includer { arbitraryBuildScriptNames = true }
```

Do **not** list every module with `include(...)` when using Includer — drop a
`build.gradle.kts` in a directory and it becomes a project. Nested projects that
have their own `settings.gradle.kts` are skipped.

Project path mapping (Includer): `feature/greeter/impl` → Gradle name
`:feature-greeter-impl` (type-safe accessors). Forma `target(":feature:greeter:impl")`
uses colon path form.

### 4.2 Root `build.gradle.kts`

**JVM does not require (and does not have) a `jvmProjectConfiguration` or
`androidProjectConfiguration` call.** The matrix and plugin application are
registered automatically the first time any JVM DSL entrypoint is used.

A minimal root file can be empty or contain only comments (as in the sample):

```kotlin
// Root build for a pure-JVM Forma project.
// No androidProjectConfiguration or jvmProjectConfiguration hook is required.
// The tools.forma.jvm settings plugin + DSL entrypoints (api/impl/library/.../binary)
// configure each module when their build.gradle.kts are evaluated.
```

Useful `gradle.properties` (sample-aligned):

```properties
org.gradle.parallel=true
org.gradle.caching=true
```

### 4.3 First targets — “hello JVM app”

Create folders + one-line builders. Suggested minimal tree:

```
my-jvm-app/
├── settings.gradle.kts
├── build.gradle.kts
├── binary/build.gradle.kts          # binary (composition root + main)
├── feature/hello/
│   ├── api/build.gradle.kts
│   └── impl/build.gradle.kts
└── common/library/build.gradle.kts  # optional shared library
```

**`binary/build.gradle.kts`** — composition root + runnable app:

```kotlin
import tools.forma.jvm.binary

binary(
    packageName = "com.example.myapp.binary",
    mainClass = "com.example.myapp.binary.MainKt",
    dependencies = deps(
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        // target(":common:library"),
    ),
)
```

For a top-level `fun main()` inside `Main.kt`, the `mainClass` value ends in `MainKt`.

**`feature/hello/api/build.gradle.kts`** — public contracts only:

```kotlin
import tools.forma.jvm.api

api(packageName = "com.example.myapp.feature.hello.api")
```

**`feature/hello/impl/build.gradle.kts`**:

```kotlin
import tools.forma.jvm.impl

impl(
    packageName = "com.example.myapp.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api"),
        // target(":common:library"),
    ),
)
```

**`common/library/build.gradle.kts`** (shared pure JVM):

```kotlin
import tools.forma.jvm.library

library(packageName = "com.example.myapp.common.library")
```

Put Kotlin under `src/main/kotlin/<package-as-dirs>/…` matching `packageName`.

### 4.4 Build / run

```bash
cd my-jvm-app
./gradlew :binary:run
```

If validation fails, the error names the **consumer suffix**, the **illegal
dependency**, and the allowed set — fix the edge, don’t weaken the graph.

---

## 5. Target cheat sheet (when to use what)

| DSL | Suffix | Use for |
|-----|--------|---------|
| `binary` | `binary` | Runnable application; top composition root (`mainClass` required) |
| `api` | `api` | Feature public contracts (JVM); no impls |
| `impl` | `impl` | Feature implementation (depends on its own `api` + shared) |
| `library` | `library` | Shared pure JVM library (leaf-ish) |
| `util` | `util` | Small JVM helpers / extensions |
| `testUtil` | `test-util` | Shared test helpers (wired via `testDependencies`) |

Full matrix and rules: [JVM-TARGETS.md](JVM-TARGETS.md).

---

## 6. Declaring dependencies

### External

```kotlin
dependencies = deps(
    libs.okhttp,           // from projectDependencies catalog
    libs.bundles.testing,
    // or bare coordinates when not using catalogs
)
```

Combine groups with `+`:

```kotlin
dependencies = deps(libs.okhttp) +
    deps(
        target(":feature:hello:api"),
    )
```

### Project / internal

```kotlin
target(":feature:hello:api")           // Forma path (colons)
// Gradle project path is often :feature-hello-api (Includer dashes)
```

**Composition roots (`binary`) must list feature `api` + `impl` explicitly** — do not rely
on transitive feature wiring alone. That keeps graphs honest and matches
validator intent.

---

## 7. Rules that bite on day one

1. **`impl` → `impl` is forbidden.** Cross-feature collaboration goes through
   `api` (or shared `library` / `util` modules).
2. **`api` only sees contracts** — `api` + `library`.
3. **`library` stays leaf-ish** — only `util` / `test-util`.
4. **`util` cannot depend on `api` / `impl`**.
5. **`binary` is the composition root** — may pull `api` + multiple `impl`s + shared.
6. **Name your folder / project so the suffix matches the DSL** (`…/impl` →
   `impl { }`).
7. **Align `packageName` with source roots** under `src/main/kotlin`.

If you fight the matrix, usually the **type** is wrong — not the tool.

---

## 8. Adding a second feature

Copy the `feature/hello` slice to `feature/settings`. Then:

1. Implement `api` contracts the other features may need.
2. Implement code in `impl` depending only on `api` + shared libs.
3. Register **both** `api` and `impl` on `binary`.

```kotlin
// binary/build.gradle.kts (excerpt)
dependencies = deps(
    target(":feature:hello:api"),
    target(":feature:hello:impl"),
    target(":feature:settings:api"),
    target(":feature:settings:impl"),
)
```

---

## 9. JVM-only considerations

- No Android SDK, no AGP, no `androidProjectConfiguration`.
- No `res/`, `viewbinding`, Compose widgets, or Android-specific targets.
- The registry (`registerJvmDefaults`) activates on first use of any `tools.forma.jvm.*` DSL entrypoint.
- Default Kotlin JVM target is Java 11 (see `JvmDefaults`).
- Sources go under `src/main/kotlin` (matching `packageName`).
- For mixed Android + JVM in one repo, the Android tutorial still applies to the Android side; the JVM plugin is independent.

See the Android parallel: [GETTING-STARTED.md](GETTING-STARTED.md).

---

## 10. Day-to-day workflow

```bash
# From the app root (jvm-application/ in this repo)
./gradlew :binary:run
./gradlew build
./gradlew :feature-greeter-impl:compileKotlin
```

- Prefer small targets; keep feature folders consistent.
- Put shared non-feature code under `common/` or `core/`.
- When a validator fails, fix the dependency edge or re-slice the target —
  don’t disable validation.

---

## 11. Common pitfalls

| Symptom | Likely cause |
|---------|----------------|
| “Java not found” / old JDK | Use JDK 17+; export `JAVA_HOME` ([ENV.md](ENV.md)) |
| `mainClass` not found / wrong name | For top-level `fun main()` in `Main.kt` use `...MainKt` |
| Illegal project dependency | Matrix violation — see [JVM-TARGETS.md](JVM-TARGETS.md) |
| Project not included | Missing `build.gradle.kts`, or nested `settings.gradle.kts` blocked Includer |
| Empty / wrong package | `packageName` ≠ directory under `src/main/kotlin` |
| Missing import | JVM DSL lives in `tools.forma.jvm` — add `import tools.forma.jvm.*` |

---

## 12. Next reading

| Doc | Topic |
|-----|--------|
| [JVM-SAMPLE.md](JVM-SAMPLE.md) | Multi-feature pure-JVM gold standard layout + run |
| [JVM-TARGETS.md](JVM-TARGETS.md) | All JVM target types + full dependency matrix |
| [GETTING-STARTED.md](GETTING-STARTED.md) | Android parallel tutorial |
| [PROGRESSIVE-EXAMPLES.md](PROGRESSIVE-EXAMPLES.md) | Feature-by-feature ladders + agent skills (F-050) |
| [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md) | Full matrix (includes JVM rows) |
| [DEPS-CATALOG.md](DEPS-CATALOG.md) | External catalogs (`library`, `bundle`, `plugin`) |
| [ENV.md](ENV.md) | JDK bootstrap |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Monorepo + plugin module graph |
| [VISION.md](VISION.md) | forma-core → JVM → Bazel roadmap |
| [forma-core-api.md](forma-core-api.md) | Core registry & validation SPI |

---

## 13. Checklist — “I’m using Forma correctly”

- [ ] Includer (or explicit includes) discovers every target
- [ ] Features use `api` / `impl` (binary wires both explicitly)
- [ ] No `impl` → `impl` edges
- [ ] `binary` supplies `mainClass` and acts as composition root
- [ ] Explicit `import tools.forma.jvm.*` (or per-entry imports) in target scripts
- [ ] `./gradlew :binary:run` succeeds on a clean machine with JDK 17+
- [ ] External versions live in a catalog (when scaling)
