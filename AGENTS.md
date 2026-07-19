# Forma autonomous worker instructions

Repo: `formatools/forma` (local: `/Users/claw/work/forma`)

## Mission

Ship Forma as a **working Android product**, then **forma-core** extraction, then **JVM**, then **Bazel**. Full vision: `docs/VISION.md`. Prioritized work: `TICKETS.md`.

## Core design principles (axioms — do not re-litigate)

Forma is a **meta build system**: **project structure declaration**, not ad-hoc Gradle configuration. Every design/implementation must obey:

1. **Target type = rule (Bazel-like)** — behavior on the type (features, external plugins, content rules, matrix) **auto-applies** on every call site. Call sites set **attributes only**.
2. **Flat, role-typed graph** — most specific role; no generic buckets (`androidLibrary` removed). Composition only at `androidApp` / `androidBinary` / JVM `binary`; `impl` ↛ `impl`.
3. **Closed dependency matrix** — allow-listed project edges only (`docs/DEPENDENCY-MATRIX.md`).
4. **Extensibility via types** — new behavior → register/derive/extend a target type; never per-module plugin id lists, `.withPlugin`, or “just apply this Gradle plugin in the module” as product API.
5. **Portable core** — types/restrictions/validation/registry in forma-core; platforms adapt.

Canonical: `docs/VISION.md`, `docs/TARGET-PLUGINS.md`, `docs/forma-core-api.md`.

**Reject without discussion:** free-form `plugins = plugins(plugin("id"))`, call-site plugin re-selection, builder chains for identity, unrestricted deps, restoring `androidLibrary`.

## Hard rules

1. Work **only** from `TICKETS.md` priority order. Pick the top `todo` / continue `in_progress` ticket.
2. Prefer small, reviewable changes. One ticket slice per 4h run when possible.
3. After functional changes: update `README.md` if user-facing; append `docs/PROGRESS.md`.
4. Keep files under ~1000 lines; split rather than grow blobs.
5. Do **not** create new cron jobs from a cron run.
6. Do **not** force-push `master` or `v2` on upstream. Prefer branch + PR to `formatools/forma` (or stepango fork if permissions require).
7. Ground reports in tool output (builds, git, gh). Never invent green builds.
8. If JDK/Android SDK missing, bootstrap first (`brew install openjdk@21` + `source scripts/env-mac.sh`; document exact commands in PROGRESS).
9. **Base of operations is `v2`** (not `master`). Feature branches and PRs target `v2`. Promote `v2` → `master` only when the user asks or CI is ready for public default.

## Git workflow

```bash
cd /Users/claw/work/forma
git fetch origin
git checkout -B forma/F-XXX-short-slug origin/v2   # or continue existing forma/* branch
# ... implement ...
git status && git diff
# commit with message: "F-XXX: concise summary"
# push and open PR with base v2 when slice is meaningful:
#   gh pr create --base v2 --title "F-XXX: ..." --body "..."
# auto-merge when green (squash only — repo allows squash only):
#   gh pr merge --auto --squash --delete-branch
# or immediately if CLEAN: gh pr merge --squash --delete-branch
```

## Verify

- Prefer `./gradlew` in `plugins/` and `application/` (not system gradle).
- Capture last 30–50 lines of failures into PROGRESS notes.
- If build cannot run (no JDK), say so clearly and only do non-build work that still advances tickets (docs/architecture) after attempting F-001.

## Progress bookkeeping

Every run must:

1. Read `TICKETS.md` + last entries of `docs/PROGRESS.md`
2. Update ticket status
3. Append a dated section to `docs/PROGRESS.md` with: ticket id, actions, commits/PRs, blockers, next step
4. Final cron response = short human report (what moved, PR links, blockers)

## Out of scope for workers

- Unrelated personal tasks, email, grkr
- Large rewrites without a ticket
- Reordering the priority list without user instruction
