# JVM sample application (F-031)

Pure-JVM product reference for **`tools.forma.jvm`**, parallel to the Android
[`application/`](../application/) gold standard.

Location: **`jvm-application/`** at the repo root.

## Layout

```
jvm-application/
├── binary/                         binary (composition root + run)
├── common/
│   ├── library/                    library
│   ├── util/                       util
│   └── test-util/                  testUtil
└── feature/
    ├── greeter/{api,impl}
    └── calculator/{api,impl}
```

Includer discovers every `build.gradle.kts` (`arbitraryBuildScriptNames = true`).
Project path uses `-` instead of `/` for Gradle names (e.g.
`feature/greeter/impl` → `:feature-greeter-impl`). Forma `target(":feature:greeter:impl")`
uses colon path form.

## What it exercises

| Target | Role in sample |
|--------|----------------|
| `api` / `impl` | Two features (greeter, calculator); **impl ↛ impl** |
| `library` | Shared math helpers |
| `util` | String helpers used by greeter impl |
| `testUtil` | Shared test helpers (wired as `testDependencies` on greeter impl) |
| `binary` | Composition root: wires both feature **api + impl** + shared modules; Gradle `application` plugin provides `:binary:run` |

Cross-feature code only through `api`. Multiple `impl`s meet only at `binary`.

## Package naming

| Module path | `packageName` |
|-------------|----------------|
| `feature/greeter/api` | `tools.forma.jvm.sample.feature.greeter.api` |
| `feature/greeter/impl` | `tools.forma.jvm.sample.feature.greeter.impl` |
| `feature/calculator/api` | `tools.forma.jvm.sample.feature.calculator.api` |
| `feature/calculator/impl` | `tools.forma.jvm.sample.feature.calculator.impl` |
| `common/library` | `tools.forma.jvm.sample.common.library` |
| `common/util` | `tools.forma.jvm.sample.common.util` |
| `common/test-util` | `tools.forma.jvm.sample.common.testutil` |
| `binary` | `tools.forma.jvm.sample.binary` |

Sources live under `src/main/kotlin/<package-as-dirs>/…`.

## Build / run

Requires JDK **17+** (same host bootstrap as Android: `source scripts/env-mac.sh`).
**No Android SDK.**

```bash
source scripts/env-mac.sh
cd jvm-application
./gradlew build
./gradlew :binary:run
```

Expected run output:

```
Hello, World! (2 + 3 = 5)
JVM sample (tools.forma.jvm) build + run successful.
```

Composite includes: `../plugins` (`tools.forma.jvm`), `../includer`,
`../build-settings` (`convention-dependencies` for `mavenCentral` /
`google` / Plugin Portal on project resolution).

## Consumer DSL notes

JVM entrypoints live in package **`tools.forma.jvm`** (unlike Android DSL files
in the default package). Sample scripts import them explicitly:

```kotlin
import tools.forma.jvm.api
import tools.forma.jvm.impl
import tools.forma.jvm.binary
// …

binary(
    packageName = "tools.forma.jvm.sample.binary",
    mainClass = "tools.forma.jvm.sample.binary.MainKt",
    dependencies = deps(
        target(":feature:greeter:api"),
        target(":feature:greeter:impl"),
        // …
    )
)
```

For a top-level `fun main()` in `Main.kt`, `mainClass` is `…MainKt`.

## Matrix

Full pure-JVM dependency matrix (including `binary`):
[`JVM-TARGETS.md`](JVM-TARGETS.md).

## See also

- [`JVM-TARGETS.md`](JVM-TARGETS.md) — types + matrix
- [`SAMPLE-APP.md`](SAMPLE-APP.md) — Android multi-feature analog
- [`VISION.md`](VISION.md) — product sequencing (JVM after Android + core)
- [JVM getting-started tutorial](JVM-GETTING-STARTED.md) (F-032)
- Bazel sample (F-042) exercising the same matrix with `kt_jvm_*` rules: see [`bazel-sample/`](../bazel-sample/) and [`BAZEL-ADAPTER.md`](BAZEL-ADAPTER.md).
