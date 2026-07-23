# Principle audit (F-085)

Full-tree check that **sample**, **progressive examples**, **agent skills**, and
**teaching docs** match Forma root principles after P7–P8 landed.

| Field | Value |
|-------|--------|
| Date | **2026-07-21** |
| Tip audited | `origin/v2` @ `36d52ba` (+ this F-085 docs slice) |
| Principles | [`VISION.md`](VISION.md) § Root principles |
| Prior tickets | F-070–F-073, F-080–F-084, F-019/F-086 |

## Pass criteria

1. **Bazel-like rules** — type owns behavior; call sites = attributes only.
2. **One global way** — no dual happy paths taught as equals.
3. **Explicit structure + tooling** — matrix + fleet docs present; no generic buckets.

Hard rejects (live code or taught as current):

- `.withPlugin` / `.withPlugins` / `TargetBuilder` / `PluginWrapper`
- Free-form `plugins = plugins(plugin("id"))` on targets
- Restored `androidLibrary`
- `Project.androidProjectConfiguration` dual path
- Equal peer teaching of typed `build-dependencies/` vs `projectDependencies`

## Surfaces scanned

| Surface | Path | Result |
|---------|------|--------|
| Gold sample call sites | `application/**/build.gradle.kts` | **PASS** |
| Sample root config | `application/build.gradle.kts` | **PASS** — `buildscript` + `extraPlugins` classpath only |
| Navigation | `application/core/navigation/res` | **PASS** — Path B `navigationRes` |
| Progressive Android | `examples/android/01`…`10` | **PASS** (wording fix in 10) |
| Progressive JVM | `examples/jvm/01`…`05` | **PASS** |
| Agent skills | `examples/agent-skills/*` | **PASS** (overview/compose/README tightened) |
| User docs | `docs/*`, root `README.md` | **PASS** (VISION sequencing + cross-links) |
| Plugin sources | `plugins/**` | **PASS** — no `TargetBuilder` / `PluginWrapper` types |

## Checklist by principle

### 1. Bazel-like rules / call-site surface

| Check | Status | Evidence |
|-------|--------|----------|
| All Android/JVM target DSLs return `Unit` | PASS | [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) |
| Zero live `.withPlugin` call sites | PASS | `rg` on `application`/`examples`/`plugins` (only anti-example comments + TestKit) |
| Type-owned external plugins taught | PASS | step 10, `forma-target-plugins`, sample `navigationRes` |
| `extraPlugins` = classpath only | PASS | sample + example 10 + PROJECT-CONFIGURATION |

### 2. One global way

| Check | Status | Evidence |
|-------|--------|----------|
| Single `androidProjectConfiguration` path | PASS | F-082; `ScriptHandlerScope` only |
| House-style external deps | PASS | DEPS-CATALOG §1; example 08; `forma-deps-catalog` |
| Typed catalogs framed advanced | PASS | DEPS-CATALOG §2; sample may use at monorepo scale |
| No dual “or use either” happy paths | PASS | no equal-peer language in skills/examples |

### 3. Explicit structure + tooling

| Check | Status | Evidence |
|-------|--------|----------|
| `androidLibrary` removed / not taught live | PASS | F-063; deprecation doc is historical |
| Closed matrix documented | PASS | DEPENDENCY-MATRIX + agent skill |
| Fleet check/generate/migrate | PASS | FLEET-TOOLING + `forma-fleet-tooling` |
| Flat role-typed sample graph | PASS | SAMPLE-APP + application layout |

## Findings and fixes (this ticket)

| ID | Severity | Location | Issue | Resolution |
|----|----------|----------|-------|------------|
| A1 | Teaching | `examples/android/10-target-plugins/README.md` | Chain API labeled “(deprecated)” after F-081 hard-remove | **Fixed** → “**removed** F-081”; Next/docs links updated |
| A2 | Teaching | `docs/VISION.md` sequencing § | Still said “deprecate chain withPlugin” | **Fixed** → removed (P7 + F-081); P8 points at this audit |
| A3 | Teaching | `docs/TARGET-PLUGINS.md` F-072 row | “deprecate TargetBuilder” stale vs F-081 | **Fixed** → hard-deprecate then F-081 remove |
| A4 | Completeness | `examples/agent-skills/forma-overview.md` | Ladder stopped at 09; missing P8 docs | **Fixed** → 01…10 + CALL-SITE / PROJECT-CONFIGURATION / FLEET / PRINCIPLE-AUDIT |
| A5 | Completeness | `examples/agent-skills/README.md` | Hard rules omitted plugins/deps/call-site | **Fixed** → rules 6–8 |
| A6 | Teaching | `examples/agent-skills/forma-compose.md` | Implied sample typed catalogs as peer | **Fixed** → house-style note + composeWidget preference |
| A7 | Index | README / PROGRESSIVE-EXAMPLES / CALL-SITE-SURFACE | No audit pointer | **Fixed** — links to this doc |

## Explicit non-issues

| Item | Why OK |
|------|--------|
| Includer TestKit `runner.withPluginClasspath()` | Gradle TestKit API, not Forma chain DSL |
| TARGET-PLUGINS anti-examples showing `.withPlugin` | Clearly “wrong / removed” |
| ANDROID-LIBRARY-DEPRECATION.md | Historical migration guide |
| Sample `build-dependencies/` typed catalogs | Advanced monorepo scale (F-083); not dual default |
| Progressive steps 01–07 bare GAV in `deps("…")` | Teaching ladder before step 08; skills say prefer catalogs in real apps |
| Sample Firebase/Crashlytics classpath without derived `firebaseBinary` yet | Comment documents Path B intent; not a live `.withPlugins` chain |
| PROGRESS.md historical “deprecate” language | Chronological log; not user teaching |

## Residual / out of scope

Not opened as new product tickets here (board empty after F-085; promote only on request):

- Mass-migrate sample modules from typed catalogs → `projectDependencies` (F-083 intentionally out of scope)
- Optional Path B `firebaseBinary` for Crashlytics/GMS apply (sample currently classpath + comment)
- F-084 follow-ups (Gradle tasks, AST migrate) — listed in FLEET-TOOLING.md
- F-019 AndroidX absolute ceiling / compileSdk chase
- ~~Legacy `.kapt` API removal once no consumers~~ → **F-093** done (KSP-only)
- Backlog GH issues (#110, #97, #88, #82, …)

## Re-audit recipe

```bash
# Live anti-patterns (expect empty for production Forma DSL)
rg -n '\.withPlugin|\.withPlugins|TargetBuilder|PluginWrapper' \
  plugins application examples jvm-application build-dependencies \
  -g '*.kt' -g '*.kts'

rg -n '\bandroidLibrary\s*\(' -g '*.kts' -g '*.kt' application examples plugins
rg -n 'plugins\s*=\s*plugins\s*\(' -g '*.kts' application examples

# Teaching: chain must be "removed", not live "deprecated" happy path
rg -n 'withPlugin.*deprecat|chains \(deprecated\)' examples docs README.md
```

## Related

- [`CALL-SITE-SURFACE.md`](CALL-SITE-SURFACE.md) · [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md)
- [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md) · [`DEPS-CATALOG.md`](DEPS-CATALOG.md)
- [`FLEET-TOOLING.md`](FLEET-TOOLING.md) · [`PROGRESSIVE-EXAMPLES.md`](PROGRESSIVE-EXAMPLES.md)
- Board: `TICKETS.md` P8
