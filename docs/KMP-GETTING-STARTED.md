# Getting started — Kotlin Multiplatform with Forma (F-110)

Forma is a **meta build system**. For shared multiplatform code you declare
**KMP targets** (`kmpLibrary`, `kmpApi`, …) instead of hand-wiring
`kotlin("multiplatform")`, per-module `kotlin { targets { … } }`, and ad-hoc
visibility. The **target type owns** the multiplatform plugin and the platform
set. Call sites stay attributes-only (`packageName`, `dependencies`, …).

This tutorial gets you from zero to a working **shared KMP library + JVM
consumer**. Design and matrix truth: [KMP-TARGETS.md](KMP-TARGETS.md). Live
validator tables (including Android/JVM → KMP): [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md).

---

## 1. Mental model (5 minutes)

| Concept | Meaning |
|---------|---------|
| **KMP target** | One Gradle project with a Forma KMP type (`kmp.library`, `kmp.api`, …). |
| **Suffix** | Project/folder name **must** end in the KMP-prefixed suffix (`…-kmp-library`, `…-kmp-api`, …). Plain `…-library` is **JVM** `library`, not KMP. |
| **Platforms** | Chosen **once** in root `kmpProjectConfiguration` (v1: `jvm` and/or `android`). Never listed per module. |
| **Composition root** | Still Android/JVM: `androidBinary` / `androidApp` / JVM `binary`. **No** `kmpBinary` / `kmpImpl` in v1. |
| **commonMain** | Shared sources live under `src/commonMain/kotlin/…` (not `src/main`). |

**What Forma does for each KMP target**

1. Validates the project name matches the KMP suffix.
2. Applies `org.jetbrains.kotlin.multiplatform` (and, when android platform is on, `com.android.kotlin.multiplatform.library`).
3. Creates the project-global platform set (jvm / android) — no call-site shopping.
4. Enforces **project** deps against the KMP matrix and (for consumers) Android/JVM → KMP edges.

You still write Kotlin and choose multiplatform library coordinates — Forma owns
structure, plugins, and boundaries.

### Rejected shapes (do not teach)

```kotlin
// REJECTED — raw KMP at every module
plugins { kotlin("multiplatform") }
kotlin { androidTarget(); jvm(); iosArm64() }

// REJECTED — platforms / plugins as call-site attrs
kmpLibrary(..., platforms = listOf("ios", "jvm"))

// REJECTED — restore androidLibrary as “shared”
androidLibrary(...) // removed F-063
```

---

## 2. Prerequisites

| Tool | Version / notes |
|------|-----------------|
| JDK | **17+** host; KMP/JVM compile target defaults to **11** |
| Android SDK | **Only** if `KmpPlatforms(android = true)` (needs `androidProjectConfiguration`) |
| Build tools | Gradle wrapper (repo baseline **9.6.x** / Kotlin **2.3.x** / AGP **9.3.x** when Android is on) |

```bash
source scripts/env-mac.sh   # repo helper — exports JAVA_HOME (+ SDK when present)
```

Pure KMP+JVM trees (example 01) do **not** need the Android SDK.

---

## 3. Path A — run the progressive example first (recommended)

```bash
git clone https://github.com/formatools/forma.git
cd forma
source scripts/env-mac.sh

cd examples/kmp/01-shared-library
./gradlew build
./gradlew :binary:run
```

Expected:

```
Hello, World — from Forma KMP shared library
KMP 01 (shared-library) build + run successful.

BUILD SUCCESSFUL
```

| Path | Role |
|------|------|
| `build.gradle.kts` | Root `kmpProjectConfiguration` once (`jvm=true`, `android=false`) |
| `shared-kmp-library/` | `kmpLibrary` + `src/commonMain/kotlin/…` |
| `binary/` | JVM `binary` composition root → `target(":shared-kmp-library")` |
| `settings.gradle.kts` | `tools.forma.kmp` + `tools.forma.jvm` + includer |

Step README: [examples/kmp/01-shared-library](../examples/kmp/01-shared-library).

---

## 4. Path B — greenfield skeleton (pure KMP + JVM)

Published plugin id: `tools.forma.kmp` (develop via composite `includeBuild` in this monorepo). Companion: `tools.forma.jvm` (consumer) + `tools.forma.includer`.

### 4.1 `settings.gradle.kts`

**Monorepo / composite** (matches example 01):

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("../plugins")
    includeBuild("../includer")
    includeBuild("../build-settings")
    apply(from = "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
}

plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.jvm")
    id("tools.forma.kmp")
}

includer { arbitraryBuildScriptNames = true }
rootProject.name = "my-kmp-app"
```

Portal path (when published): same plugin ids with `version "…"`.

### 4.2 Root `build.gradle.kts`

`kmpProjectConfiguration` lives in the **default package** (parity with
`androidProjectConfiguration`) so it resolves inside `buildscript { }` without
imports under Gradle Kotlin DSL.

```kotlin
// Platforms once for the whole product — modules never re-list targets.
buildscript {
    kmpProjectConfiguration(
        project = rootProject,
        platforms = tools.forma.kmp.settings.KmpPlatforms(jvm = true, android = false),
        // jvmTarget = "11", // default
    )
}
```

**Defaults** if you omit `platforms`: **both** `jvm` and `android` are **on**.
Android-on requires a prior/sibling `androidProjectConfiguration` (SDK pins) or
apply fails fast.

### 4.3 Shared library + JVM binary

```
my-kmp-app/
├── settings.gradle.kts
├── build.gradle.kts
├── shared-kmp-library/
│   ├── build.gradle.kts
│   └── src/commonMain/kotlin/com/example/shared/Greeting.kt
└── binary/
    ├── build.gradle.kts
    └── src/main/kotlin/com/example/binary/Main.kt
```

**`shared-kmp-library/build.gradle.kts`**

```kotlin
import tools.forma.kmp.kmpLibrary

kmpLibrary(packageName = "com.example.shared")
```

**`binary/build.gradle.kts`**

```kotlin
import tools.forma.jvm.binary

binary(
    packageName = "com.example.binary",
    mainClass = "com.example.binary.MainKt",
    dependencies = deps(
        target(":shared-kmp-library"),
    ),
)
```

Sources:

- KMP: `src/commonMain/kotlin/<package-as-dirs>/…` matching `packageName`
- JVM binary: `src/main/kotlin/…` as usual

### 4.4 Build / run

```bash
./gradlew build
./gradlew :binary:run
```

---

## 5. Target cheat sheet

| DSL | Type id | Name suffix | Role |
|-----|---------|-------------|------|
| `kmpLibrary` | `kmp.library` | `kmp-library` | Shared multiplatform library (default workhorse) |
| `kmpApi` | `kmp.api` | `kmp-api` | Shared public contracts |
| `kmpUtil` | `kmp.util` | `kmp-util` | Shared utilities |
| `kmpTestUtil` | `kmp.test-util` | `kmp-test-util` | Shared test helpers (`testDependencies`) |

**Not in v1:** `kmpImpl`, `kmpBinary`, iOS/JS/Wasm, per-module platform lists.

Imports: `tools.forma.kmp.kmpLibrary` (and siblings). Plugin id: `tools.forma.kmp`.

---

## 6. Declaring dependencies

### On a KMP module (→ commonMain)

```kotlin
import tools.forma.kmp.kmpLibrary
import tools.forma.deps.core.deps

kmpLibrary(
    packageName = "com.example.shared",
    dependencies = deps(
        target(":core-kmp-api"),
        // external multiplatform GAVs / libs.* catalog entries
    ),
    testDependencies = deps(
        // commonTest
    ),
)
```

Platform-only dep attrs (`androidDependencies` / `jvmDependencies`) are **deferred** in v1 — prefer common code or platform consumers.

### From Android / JVM consumers → KMP

```kotlin
// Android feature impl
impl(
    packageName = "com.example.hello.impl",
    dependencies = deps(
        target(":shared-kmp-library"),
        target(":feature-hello-api"),
    ),
)

// JVM binary (example 01)
binary(
    packageName = "com.example.binary",
    mainClass = "com.example.binary.MainKt",
    dependencies = deps(target(":shared-kmp-library")),
)
```

Allow-lists (summary):

| Consumer | May depend on |
|----------|----------------|
| `android.api` / `jvm.api` | `kmp.api` |
| `android.impl` / `app` / `binary` | `kmp.api`, `kmp.library`, `kmp.util` |
| `jvm.impl` / `jvm.binary` | `kmp.api`, `kmp.library`, `kmp.util` |
| `jvm.library` / `jvm.util` | `kmp.library`, `kmp.util` |

Full tables: [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md) · design [KMP-TARGETS.md](KMP-TARGETS.md) §6.

**Directionality:** KMP must **not** depend on Android `impl` / UI / `res` or JVM `impl`. Shared code stays leaf-ish toward platforms.

---

## 7. Mobile monorepo (Android + KMP)

When both platforms are enabled:

```kotlin
buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 26,
        targetSdk = 37,
        compileSdk = 37,
        agpVersion = "9.3.0",
        // …
    )
    kmpProjectConfiguration(
        project = rootProject,
        // defaults: KmpPlatforms(jvm = true, android = true)
    )
}
```

| Concern | Source of truth |
|---------|-----------------|
| minSdk / compileSdk / AGP | `androidProjectConfiguration` |
| KMP platforms | `kmpProjectConfiguration` only |
| Kotlin version | Align with Android/Forma settings — do not introduce a second pin |
| JVM target | Default `"11"`; align with `javaVersionCompatibility` |

Settings plugins typically include `tools.forma.android`, `tools.forma.kmp`, and often `tools.forma.jvm` if you also have pure-JVM binaries.

---

## 8. Rules that bite on day one

1. **Suffix must be KMP-prefixed** — `shared-kmp-library`, not `shared-library`.
2. **Platforms once** — never `kotlin { jvm(); androidTarget() }` in a module.
3. **Composition at Android/JVM roots** — wire KMP from `impl` / `binary` / `app`, not via a fake KMP app type.
4. **`impl` ↛ `impl` still holds** on Android/JVM; KMP does not create a back door.
5. **No KMP → platform UI/impl** — keep shared code free of Android/JVM feature leaves.
6. **Sources under `commonMain`** (and optional `androidMain` / `jvmMain` for actuals) — not `src/main` on KMP modules.
7. **No `res/` in common** — Android resources stay `androidRes` / platform UI targets.
8. **`:kmp` plugin must not depend on `:android`** (engine cycle rule) — consumers depend on KMP types; fine.

---

## 9. Day-to-day workflow

```bash
# Progressive example
cd examples/kmp/01-shared-library
./gradlew build
./gradlew :binary:run

# Plugins unit + happy-path coverage (contributors)
cd plugins
./gradlew :kmp:test test jacocoHappyPathCoverageVerification
```

- Prefer small `kmp-library` / `kmp-api` slices over one fat shared module.
- Expect/actual only at real platform edges; prefer expect-free common when possible.
- When a validator fails, fix the **edge or type** — do not disable validation.

---

## 10. Common pitfalls

| Symptom | Likely cause |
|---------|----------------|
| Wrong type / suffix validation | Folder not ending in `kmp-library` / `kmp-api` / … |
| Unresolved `kmpProjectConfiguration` in `buildscript` | Expect default-package API; do not put it only under a nested package import inside `buildscript` |
| Android platform apply fails | `android = true` without `androidProjectConfiguration` / SDK |
| Illegal project dependency | Matrix — see DEPENDENCY-MATRIX / KMP-TARGETS §6 |
| Trying to apply MPP in the module | Type owns plugins — remove raw `plugins { kotlin("multiplatform") }` |
| `src/main` empty on KMP module | Use `src/commonMain/kotlin/…` |
| iOS / JS targets | Out of scope for v1 |

---

## 11. Next reading

| Doc | Topic |
|-----|--------|
| [KMP-TARGETS.md](KMP-TARGETS.md) | Full design: types, platforms, matrix, rejected shapes |
| [DEPENDENCY-MATRIX.md](DEPENDENCY-MATRIX.md) | Live allow-lists (KMP + consumer edges) |
| [PROGRESSIVE-EXAMPLES.md](PROGRESSIVE-EXAMPLES.md) | Ladders + agent skills index |
| [JVM-GETTING-STARTED.md](JVM-GETTING-STARTED.md) | Pure JVM tutorial (composition roots) |
| [GETTING-STARTED.md](GETTING-STARTED.md) | Android tutorial |
| [TARGET-PLUGINS.md](TARGET-PLUGINS.md) | Type-owned external plugins (Path A/B) |
| [VISION.md](VISION.md) | Root principles + platform sequencing |
| [ENV.md](ENV.md) | JDK / SDK bootstrap |

Agent skill: [forma-kmp-targets](../examples/agent-skills/forma-kmp-targets.md).

---

## 12. Checklist — “I’m using Forma KMP correctly”

- [ ] Settings apply `tools.forma.kmp` (and consumer platform plugins as needed)
- [ ] Root calls `kmpProjectConfiguration` **once** (platforms not re-listed on modules)
- [ ] Shared modules use `kmpLibrary` / `kmpApi` / … with **`*-kmp-*` suffixes**
- [ ] Sources under `src/commonMain/kotlin/…` matching `packageName`
- [ ] No raw `kotlin { targets { } }` / per-module plugin shopping
- [ ] Apps/features compose on Android/JVM roots; they depend **on** KMP, not the reverse for UI/impl
- [ ] `./gradlew build` (and consumer run/assemble) succeeds on a clean machine with the right JDK/(SDK)
