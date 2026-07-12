# Forma progress log

Newest entries first.

## 2026-07-12 — F-014 Sample app gold-standard multi-feature structure

- **Ticket:** F-014 → `done`
- **Branch:** `forma/F-014-sample-gold-standard` (from `origin/v2`)
- **Sample modernization:**
  - Relocated leftover `com/stepango/blockme/…` source trees under
    `tools/forma/sample/…` (mvvm lifecycle, extensions, Marvel service/repo/mapper/tests)
  - Aligned `packageName` with module path for home/characters api+res+viewbinding,
    characters core api, root-res (`tools.forma.sample.root.res`)
  - Moved `toggle-widget` sources into `tools.forma.sample.widget.toggle` (+ menu XML)
  - Home viewbinding UI types under `…home.viewbinding.ui`; list contracts under
    `…list.viewbinding.domain.model` (impl imports updated)
  - Normalized `feature/characters/list/impl` deps to consistent `target(":…")` form
  - Placeholder `FeatureHomeApi` / `FeatureCharactersListApi` objects for empty api modules
- **Docs:** `docs/SAMPLE-APP.md` gold-standard guide; ARCHITECTURE §3, DEPENDENCY-MATRIX,
  README pointers; TICKETS status
- **Verify (real tool output, OpenJDK 17 + SDK 34):**
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** (2112 tasks, 54s after clean)
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-015 Android project tutorial (getting started)

## 2026-07-11 — F-013 Compose support (GH #96)

- **Ticket:** F-013 → `done`
- **Branch:** `forma/F-013-compose-support` (from `origin/v2`)
- **Plugin / DSL:**
  - `AndroidLibraryFeatureConfiguration.compose` + `AndroidBinaryFeatureConfiguration.compose`
    → `buildFeatures.compose` + `composeOptions.kotlinCompilerExtensionVersion`
  - Per-target `compose` flag (default = project `androidProjectConfiguration(compose=…)`)
    on `impl`, `androidLibrary`, `androidUtil`, `androidApp`, `uiLibrary`, `androidBinary`
  - New target **`composeWidget`** / suffix `compose-widget` (always Compose)
  - Project settings: `composeCompilerVersion` (default **1.5.3** for Kotlin 1.9.10)
  - Validators: `compose-widget` allowed from impl/app/binary/uiLibrary/widget/viewBinding/res;
    widget ↔ compose-widget mutual deps (View + Compose coexist)
- **Sample:**
  - `application/common/greeting/compose-widget` with `@Composable GreetingCard`
  - `binary` depends on it with `compose = true`
  - `androidx.compose` catalog cluster (`transitiveDeps`) in build-dependencies
  - Sample `compileSdk` **34** (Compose emoji2 AAR metadata); `targetSdk` remains 33
- **Docs:** `docs/COMPOSE.md`; matrix/README/ARCHITECTURE/ENV/CI updated
- **Verify (real tool output, OpenJDK 17 + SDK 34):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** (2151 tasks)
  - `:common-greeting-compose-widget:compileDebugKotlin` + `:binary:assembleDebug` green
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-014 sample app gold-standard multi-feature structure

## 2026-07-11 — F-012 External deps catalog UX

- **Ticket:** F-012 → `done`
- **Branch:** `forma/F-012-deps-catalog-ux` (from `origin/v2`)
- **Code (`plugins/deps` catalog):**
  - Pure name generators (removed configuration-time `println`)
  - Clear GAV validation via `parseGroupArtifactVersion` (exactly `group:artifact:version`)
  - Fail-fast when auto-name would be empty (all tokens filtered)
  - New `library(gav, name = …)` factory for stable short accessors
  - `projectDependencies` accepts `library()` / `bundle()` / `plugin()` / bare GAV; better error for unknown types
  - Unit tests: `GeneratorsTest` (`:deps:test`)
- **Sample:** `application/settings.gradle.kts` uses `library(..., name = "coil"|"coilBase")` inside the coil bundle
- **Docs:** `docs/DEPS-CATALOG.md` (user guide); README section; ARCHITECTURE §2.4 pointer
- **Verify (real tool output, OpenJDK 17 + SDK 33):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `plugins/`: `./gradlew :deps:test` → **BUILD SUCCESSFUL** (GeneratorsTest)
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** (2080 tasks)
  - Configuration log no longer spam-prints `Generated name …`
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none
- **Next step:** F-013 Compose support (GH #96)

## 2026-07-11 — F-011 api/impl + composition-root validation

- **Ticket:** F-011 → `done`
- **Branch:** `forma/F-011-api-impl-validation`
- **Code:**
  - `androidApp` / `androidBinary` / `androidLibrary`: replace `EmptyValidator` with Dagger-friendly project-dep allowlists
  - `androidLibrary` **cannot** depend on `impl` (or widget/viewbinding)
  - `impl` still cannot depend on `impl`; KDoc on `api`/`impl` documents boundaries
  - Clearer validation error text in `Validator.kt`
- **Docs:** `docs/DEPENDENCY-MATRIX.md`, README summary, TICKETS
- **Verify:** `plugins` + `application` `./gradlew build` (see commit/PR notes)
- **Next:** F-012 external deps catalog UX

## 2026-07-11 — F-010 Dependency matrix from live validators

- **Ticket:** F-010 → `done`
- **Branch:** `forma/F-010-dependency-matrix` (from `origin/v2` after F-003/F-004 merge)
- **Actions:**
  - Added `docs/DEPENDENCY-MATRIX.md` as canonical code-truth matrix
  - Replaced aspirational README matrix with code-aligned summary
  - Pointed `docs/ARCHITECTURE.md` at the new doc; marked F-010 done
- **Also that run:** F-003 #152 and F-004 #153 merged into `v2` (`123ec44`)
- **PR:** #154 squash-merged to `v2` as `619ce05`

## 2026-07-11 — v2 base of operations (merge open PRs)

- Created `v2` from `master`; integrated F-001/F-002; workers branch/PR against `v2`
- Tip later advanced with F-003/F-004/F-010/F-011

## 2026-07-11 — F-004 CI green (workflow + badge)

- **Ticket:** F-004 → `done`
- **Branch:** `forma/F-004-ci-green` (from `origin/v2`; independent of open F-003 PR #152)
- **Actions:**
  - Rewrote `.github/workflows/main.yml`:
    - Display name **CI**; triggers `push` + `pull_request` + `workflow_dispatch`
    - Concurrency cancel-in-progress per ref
    - **All four jobs** pin Temurin 17 (`actions/setup-java@v4`)
    - Gradle via `gradle/actions/setup-gradle@v4`
    - `build_application`: `android-actions/setup-android@v3` with `platforms;android-33`, platform-tools, build-tools 33.0.2 + 34.0.0
    - Dropped unconditional `--scan`; use `--stacktrace --console=plain`
  - README CI badge + code-size shield → `formatools/forma` + `actions/workflows/main.yml/badge.svg`
  - `docs/ARCHITECTURE.md` §4 updated for new CI layout
- **GHA verification (PR run 29162702015):** **all success**
  - Plugins: success
  - Includer: success
  - Depgen: success
  - Application: success (SDK setup + full sample build)
- **Commits/PRs:** https://github.com/formatools/forma/pull/153
- **Blockers:** none
- **Next step:** P0 complete after #152 (F-003) + #153 (F-004) merge to `v2`. Then P1 top is F-010 (live dependency matrix docs).

## 2026-07-11 — F-003 Modern toolchain (AGP compile/runtime align)

- **Ticket:** F-003 → `done`
- **Branch:** `forma/F-003-modern-toolchain` (from `origin/v2`)
- **Actions:**
  - Bumped `plugins/android` compile dep `com.android.tools.build:gradle` **7.4.2 → 8.1.2** to match sample runtime force (`application/settings.gradle.kts` + `agpVersion = "8.1.2"`)
  - README sample config: `agpVersion` + nav safe-args / crashlytics plugin versions aligned with application
  - Docs: `ARCHITECTURE.md` toolchain snapshot + inconsistency table; `ENV.md` re-verify notes; `TICKETS.md` status
- **Build verification (real tool output, OpenJDK 17 + SDK 33):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** in 42s (58 tasks; `:android:compileKotlin` clean after AGP bump)
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** in 3m8s (2080 tasks)
- **Not in this slice:** CI workflow (F-004 — still missing Java on plugin jobs + Android SDK setup); further AGP/Gradle bumps beyond 8.1.2/8.4
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none for local modern-toolchain claim
- **Next step:** F-004 CI green (pin Temurin 17 on all jobs; install Android SDK for `build_application`; fix badge/name drift)

## 2026-07-11 — v2 base of operations (merge open PRs)

- **Action:** Created integration branch `v2` from `origin/master`, merged open work from:
  - PR #150 / `forma/F-001-jdk-bootstrap` (F-001)
  - PR #151 / `forma/F-002-architecture-audit` (F-002, stacked on F-001)
- **Tip:** `b517e1b` merge commit (or later tip if docs follow-up landed on same push)
- **Policy:** Workers branch from `origin/v2`; PRs use `--base v2`. `master` remains public default until explicit promote.
- **Docs:** `AGENTS.md`, `docs/cron-worker-prompt.txt` updated for v2 base.
- **Not reopened:** old closed PRs #147 / #127 / #112 (pre-2024 / superseded) — left closed unless user prioritizes.
- **Next:** F-003 or F-004 on top of `v2`; retarget or close #150/#151 once v2 is published.

## 2026-07-11 — F-002 Architecture audit (build graph)

- **Ticket:** F-002 → `done`
- **Branch:** `forma/F-002-architecture-audit` (stacked on F-001 docs commit)
- **Actions:**
  - Mapped composite builds: `plugins/`, `application/`, `includer/`, `depgen/`, `build-settings/`, `build-dependencies/`
  - Documented plugins module DAG (`target` → `validation`/`owners`/`config` → `deps` → `android`)
  - Extracted **live** allowed-dependency validators from every Android DSL entrypoint into a table (code truth vs README matrix)
  - Listed sample app ~35 targets (feature api/impl/res/viewbinding pattern)
  - Audited CI workflow gaps (Java only on application job; no Android SDK step; badge/name drift)
  - Marked forma-core extraction candidates vs Android-only code
- **Artifact:** `docs/ARCHITECTURE.md`
- **Commits/PRs:** this run — push + PR
- **Blockers:** none (docs-only slice; builds not re-run; F-001 host green still the baseline)
- **Next step:** F-003 (toolchain modernization / AGP compile vs runtime skew) or F-004 (CI green) — prefer F-003 only if code changes needed; CI is the remaining P0 risk

## 2026-07-10 — F-001 Environment bootstrap (JDK + Android SDK)

- **Ticket:** F-001 → `done`
- **Branch:** `forma/F-001-jdk-bootstrap` (from `origin/master`)
- **Host toolchain:**
  - JDK: Homebrew `openjdk@17` 17.0.19 at `/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home` (keg-only; no sudo). `brew install --cask temurin@17` fails without interactive sudo.
  - Android SDK: `brew install --cask android-commandlinetools` → `/usr/local/share/android-commandlinetools`; installed `platforms;android-33`, `platform-tools`, `build-tools;33.0.2` + `34.0.0`; licenses accepted.
  - `application/local.properties` written (gitignored) with `sdk.dir`.
  - `~/.zshrc` exports `JAVA_HOME` / `ANDROID_HOME` for interactive shells.
- **Repo docs/scripts:**
  - `docs/ENV.md` — install + verify steps
  - `scripts/env-mac.sh` — sourceable env for cron/workers
- **Build verification (real tool output):**
  - `plugins/`: `./gradlew build` → **BUILD SUCCESSFUL** (58 tasks)
  - `includer/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `depgen/`: `./gradlew build` → **BUILD SUCCESSFUL**
  - `application/`: `./gradlew build` → **BUILD SUCCESSFUL** in 3m29s (2080 tasks)
- **Commits/PRs:** (this run) push + PR for env docs/scripts; host packages stay machine-local
- **Blockers:** none for local Gradle; optional `sudo ln -sfn …/openjdk.jdk` into `/Library/Java/JavaVirtualMachines` not done (sudo password unavailable)
- **Next step:** F-002 audit build graph → `docs/ARCHITECTURE.md`; F-003 may largely be closed by host green builds (confirm CI separately as F-004)

## 2026-07-10 — Workspace init + cron online

- Cloned `formatools/forma` → `/Users/claw/work/forma` (branch `forma/agent-workspace`)
- Wrote `docs/VISION.md`, `TICKETS.md`, `AGENTS.md`, this log
- Cron jobs:
  - `18717ea2093c` **forma 4h ticket worker** — every 240m, workdir `/Users/claw/work/forma`, deliver `telegram:-1003754340081:136`, model `grok-4.5` / `custom:xai`, toolsets terminal+file+web
  - `1c872230b69e` **forma daily progress report** — `0 10 * * *` (10:00 America/Los_Angeles), same deliver/workdir, toolsets terminal+file
- Prompt sources: `docs/cron-worker-prompt.txt`, `docs/cron-daily-prompt.txt`
- Host note: no Java runtime installed yet (blocks Gradle until F-001)
