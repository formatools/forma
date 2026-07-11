# Forma progress log

Newest entries first.

## 2026-07-11 — F-004 CI green (workflow + badge)

- **Ticket:** F-004 → `in_progress` (workflow shipped; full green claim waits on GHA run after PR)
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
- **Local verification:** host still has JDK 17 + SDK 33 from F-001; full GHA run not available until push/PR
- **Commits/PRs:** this run — push + PR base `v2`
- **Blockers:** none for the workflow change itself; mark F-004 `done` only after GHA jobs are green on the PR
- **Next step:** merge order note — F-003 (#152 AGP 8.1.2 compile align) still open on `v2`; CI PR can land independently. After both merge, re-check application job on combined tip
- **Related open PRs:** #150 F-001, #151 F-002 (already in `v2` tip), #152 F-003

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
