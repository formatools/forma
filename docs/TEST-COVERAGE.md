# Test coverage (plugins)

Forma enables **JaCoCo** on the `plugins/` multi-project and gates **happy-path**
line coverage at **≥ 60%**.

## Commands

From `plugins/`:

```bash
source ../scripts/env-mac.sh   # mac host JDK/SDK env when needed
./gradlew test                 # unit tests + per-module jacocoTestReport
./gradlew jacocoHappyPathReport
./gradlew jacocoHappyPathCoverageVerification   # fails if happy-path LINE < 60%
./gradlew jacocoRootReport     # all unit-tested modules (core/config/deps/jvm)
./gradlew check                # includes happy-path verification
./gradlew build                # CI path — also enforces the gate
```

Reports:

| Report | Path |
|--------|------|
| Happy-path HTML | `plugins/build/reports/jacoco/jacocoHappyPathReport/html/index.html` |
| Aggregate HTML | `plugins/build/reports/jacoco/jacocoRootReport/html/index.html` |
| Per-module | `plugins/<module>/build/reports/jacoco/test/html/index.html` |

## What is “happy path”?

Pure, unit-tested engine surface (no AGP TestKit / `Project` apply paths):

| Area | Classes |
|------|---------|
| **forma-core** | all `tools.forma.core.**` |
| **config model** | `AndroidProjectSettings`, `FormaBuildFeatures`, `FormaFeatureFlags`, `FormaSettingsStore`, dependency-validation helpers |
| **deps pure** | catalog `Generators*`, `TargetPlugin*` registry, configuration/dep model types |
| **jvm targets** | `tools.forma.jvm.target.**` |

**Out of the gate** (still reported in aggregate when present): Android DSL apply,
`applyDependencies`, fleet Gradle tasks, plugin entrypoints, Settings
`projectDependencies` / `VersionCatalogBuilder` wiring. Those are covered by
sample `application/` builds and progressive examples, not line-count unit suites.

Implementation: `plugins/buildSrc/src/main/kotlin/formaCoverage.kt`.

## Other test suites

CI / full local verify also runs (separate Gradle trees):

| Tree | Command |
|------|---------|
| `plugins/` | `./gradlew build` (tests + happy-path coverage) |
| `includer/` | `./gradlew build` (unit + functionalTest) |
| `depgen/` | `./gradlew build` (unit + functionalTest) |
| `bazel-adapter/` | `./gradlew test` |
| `application/` | `./gradlew build` (sample app; needs Android SDK) |

## Baseline (2026-07-22)

Measured on host after enabling the gate:

| Scope | LINE | BRANCH | INSTRUCTION |
|-------|------|--------|-------------|
| **Happy-path gate set** | **~96%** | ~79% | ~88% |
| Aggregate unit-tested modules | ~44% | ~41% | ~44% |
| `:core` alone | ~95% | ~72% | ~88% |

The gate fails the build if happy-path **LINE** covered ratio drops below **0.60**.
