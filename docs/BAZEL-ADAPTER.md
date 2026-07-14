# Bazel Adapter Design (F-040)

**Status:** design accepted (F-040). **F-041 spike landed** (see [Spike results](#spike-results-f-041) below). F-042 (minimal sample) remains.

This document defines how forma-core concepts (TargetType, RestrictionGraph, TargetRegistry, validators) map onto Bazel so that later work can generate or validate BUILD files while preserving the same dependency discipline that Gradle platforms enforce today.

**JVM-first scope for the initial adapter:** the design and first spike use the pure JVM target set only (`jvm.api`, `jvm.impl`, `jvm.library`, `jvm.util`, `jvm.test-util`, `jvm.binary`). Android types are noted as future work using the same adapter surface.

Source of truth for current concepts:
- [`docs/forma-core-api.md`](forma-core-api.md) (TargetType, RestrictionGraph, TargetRegistry, NameMatcher, validators, ContentRule)
- [`docs/JVM-TARGETS.md`](JVM-TARGETS.md) + `plugins/jvm/.../JvmTargetRegistry.kt` (exact 6-type matrix)
- [`jvm-application/`](../jvm-application/) (concrete layout and naming for mapping examples)
- [`docs/DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) (Android reference; not primary for this ticket)

---

## 1. Goals and non-goals

### Goals
- Define a **deterministic, repeatable mapping** from forma-core `TargetType` + `RestrictionGraph` onto Bazel packages, labels, rules, `deps`, and `visibility` (or equivalent checks).
- Enable a later adapter (F-041/F-042) to produce or validate BUILD content that is **semantically consistent** with the Gradle platforms (same `impl ↛ impl`, same composition-root rules, same api contracts surface).
- Keep the adapter **dependent on forma-core only**; core must remain free of Bazel, Gradle Project, or AGP APIs.
- Provide a small, portable model (`FormaProjectModel`) that a generator or checker can consume without a running Gradle configuration.
- Prioritize **JVM kit v1** (proven, small, no content rules) so F-041 can deliver signal quickly. Android follows the same pattern later.
- Document concrete conventions derived from the `jvm-application/` tree so a future implementer has no ambiguity on package paths or target names.

### Non-goals (out of scope for F-040 / design only)
- Shipping a working Bazel build of any real project (including `jvm-application/`).
- Full `rules_android` / AGP parity or detailed Android kit mapping.
- A complete Bazel sample tree checked into the repo (F-042 will add a minimal sketch; design may include tiny illustrative fragments only).
- Changing forma-core or the JVM platform (this ticket is documentation + planning).
- bzlmod migration of the existing Gradle build or catalog translation details beyond notes.
- Writing the generator/checker code (F-041) or hand-authoring BUILD files (F-042).

---

## 2. Concept mapping table

| Forma (core / JVM)                  | Bazel (proposed)                                                                 | Notes |
|-------------------------------------|----------------------------------------------------------------------------------|-------|
| `TargetType.id` (e.g. `"jvm.impl"`) | `tags = ["forma:type=jvm.impl"]` + rule kind selection                           | Stable id used for lookup in registry snapshot and diagnostics. Tag enables aspects and queries. |
| `TargetType.nameSuffix` (e.g. `"impl"`) | Influences target `name` and package layout conventions (see §3)               | Suffix matching (`SuffixNameMatcher`) is re-used at generate/check time; not a Bazel name rule by itself. |
| `TargetRef.name`                    | Label base (normalized from Gradle path or directory)                            | In Gradle this is the project name (e.g. `feature-greeter-impl`). Bazel uses filesystem-derived package + target name. |
| `TargetRef`                         | Label (`//feature/greeter/impl:impl`)                                            | The canonical reference form in BUILD files and in the portable model. |
| `RestrictionGraph` + `allowedDependencies` (closed allow-list) | `visibility` attribute on targets + `deps` allowability (generated or checked) | Core graph is the source of truth. Bazel visibility is a best-effort mirror; exact enforcement can be done by a checker that calls `RestrictionGraph.isAllowed(...)`. |
| `ContentRule` (e.g. `NoResourcesUnderMain`) | Filegroup / glob constraints or package layout checks (optional, later)       | JVM kit currently attaches **no** content rules. Android rules would map here. |
| External / catalog GAV              | `maven_install` (WORKSPACE) or `bazel_dep` + `maven` (bzlmod); `jvm_import`     | Note only for F-040. Catalog translation strategy deferred. |
| Composition root (`jvm.binary`)     | `kt_jvm_binary` (or `java_binary`)                                               | Single place multiple `impl` modules meet. Enforce no `binary → binary`. |
| `TargetRegistry` + `registerJvmDefaults()` | Registry snapshot exported to the adapter (types + allow-lists)               | Adapter reads registrations (or a derived `RestrictionGraph`) to decide `deps` and `visibility`. |

**JVM-first principle:** The adapter design, model, and F-041 spike start with the six JVM types and their exact matrix from `JvmTargetRegistry`. Android types registered on `AndroidTargetRegistry` (or future registries) follow the identical adapter surface.

---

## 3. Label and package conventions

The mapping is derived from the concrete `jvm-application/` layout and how Gradle project paths are formed by the includer.

### Directory tree (source of truth for examples)

```
jvm-application/
├── binary/                         # jvm.binary
├── common/
│   ├── library/                    # jvm.library
│   ├── util/                       # jvm.util
│   └── test-util/                  # jvm.test-util
└── feature/
    ├── greeter/
    │   ├── api/                    # jvm.api
    │   └── impl/                   # jvm.impl
    └── calculator/
        ├── api/
        └── impl/
```

### Rules

1. **Bazel package path** = relative directory path from the workspace root, with `/` separators.
   - `feature/greeter/impl` → package `//feature/greeter/impl`
   - `common/test-util` → package `//common/test-util`
   - `binary` → package `//binary`

2. **Target name** inside the package = the leaf directory segment (which for the JVM sample coincides with the Forma suffix or the declared `binary` name).
   - `//feature/greeter/impl:impl`
   - `//common/test-util:test-util`
   - `//binary:binary`
   - Hyphens are preserved and are valid in Bazel target names.

3. **Full label form** used everywhere in generated BUILDs and in the portable model: `//pkg/segment:target`.

4. **Gradle project path normalization** (for model consumers that start from Gradle data):
   - `:feature:greeter:impl` → strip leading `:`, replace `:` with `/` → `feature/greeter/impl`
   - `:common:test-util` → `common/test-util`
   - `:binary` → `binary`

5. **Suffix identity across name styles**
   - `SuffixNameMatcher` (core) still applies during generate/check: a target whose `typeId` is `jvm.impl` must have a name that matches suffix `impl` (exact or `*-impl`).
   - When Bazel packages do not use the `-impl` directory style, the adapter uses the **explicit `typeId`** from the model (or `tags`) rather than inferring only from the leaf name. The leaf name is still chosen to be the suffix for human familiarity.

6. **Optional Forma metadata**
   - Every generated target includes `tags = ["forma:type=<id>"]` (e.g. `"forma:type=jvm.impl"`).
   - Additional tags or a `forma` provider can be added later for aspects (no requirement in v1).
   - `packageName` from the DSL is **source layout only**; it does not affect labels.

7. **BUILD file placement**
   - One `BUILD.bazel` (or `BUILD`) per package directory that contains a Forma target.
   - `srcs = glob(["src/main/kotlin/**/*.kt"])` (or equivalent) for pure JVM sources.
   - No `resources`/`assets` attributes for the JVM kit.

Example mapping for `feature/greeter/impl`:

```starlark
# //feature/greeter/impl:impl
kt_jvm_library(
    name = "impl",
    srcs = glob(["src/main/kotlin/**/*.kt"]),
    deps = [
        "//feature/greeter/api:api",
        "//common/util:util",
        "//common/library:library",
    ],
    visibility = ["//binary:binary"],  # granted by composition root; see §4
    tags = ["forma:type=jvm.impl"],
)
```

---

## 4. Visibility ↔ restriction graph

Bazel `visibility` is coarser than Forma’s closed per-type allow-lists. The adapter must preserve the **semantics** (especially `impl ↛ impl` and composition-root boundaries) even if exact 1:1 visibility lists are not always emitted.

### Design options considered

- **A (tight per-target)**: For each target, emit `visibility = ["//consumer1:__pkg__", "//consumer2:__pkg__", ...]` derived from reverse edges in the `RestrictionGraph`.
- **B (package-group per type)**: Pre-declare `//visibility:jvm_api`, `//visibility:jvm_impl`, etc. and assign type-level visibility. Simpler but looser.
- **C (check tool primary)**: Keep Bazel visibility intentionally loose (public or subtree) and enforce the real matrix with an offline validator or aspect that loads a `RestrictionGraph` snapshot and the declared `deps` + `tags`. Generate can still emit visibility as a helpful guard.

### Recommended primary approach: **Hybrid leaning on A + binary grants, with C as the correctness backstop**

**Primary for F-041 spike:** Start with **generate using the restriction graph** (A-style) for explicit consumers, augmented by type-level package groups (B) for api contracts, plus a **check mode** (C) that can be run against the emitted BUILDs or an existing tree.

**Rationale (correctness + implementability):**
- The closed-world `RestrictionGraph` is the single source of truth. Any generated visibility list is derived from it (or the originating `TargetRegistration.allowedDependencies`).
- Reverse-edge computation is straightforward given a small model: for a producer P of type T, the allowed consumers are exactly those U where `graph.isAllowed(U, T)`.
- Binary composition root is handled specially: it receives grants for every direct `impl` (and other) dep it declares; those grants are narrow (`//binary:__pkg__` or the package containing the binary target).
- `impl ↛ impl` is automatically preserved because the JVM matrix never registers `impl` as an allowed dep for `impl`. The generator simply never emits an impl→impl edge in `deps` or in a visibility grant.
- `binary` self-exclusion is preserved the same way (graph does not allow `binary → binary`).
- Package groups reduce boilerplate for broad api surfaces while still letting tight feature boundaries (one impl → its api) use per-pkg visibility.
- Check mode (C) gives **exact parity** with Gradle validators for F-041 without requiring perfect visibility emission on day one. It can be implemented by exporting the registry graph + target list and walking BUILD `deps` + tags.

### Concrete visibility policy (JVM kit)

- `jvm.api` targets: visible to their direct consumers + broadly within feature subtrees or via a `jvm_api` package group. Typical: `["//feature/greeter:__subpackages__", "//binary:__pkg__"]` or a package group.
- `jvm.impl`, `jvm.library`, `jvm.util`, `jvm.test-util`: private by default. Visibility granted only to explicit allowed consumers according to the graph (especially the declaring `binary`).
- `jvm.binary`: `visibility = ["//visibility:public"]` (or equivalent) so it can be the runnable entry. It does **not** grant itself visibility to other binaries.

Example emission sketch (illustrative only; generator will compute the list):

```starlark
# feature/greeter/api/BUILD.bazel
kt_jvm_library(
    name = "api",
    ...
    visibility = [
        "//feature/greeter/impl:__pkg__",
        "//binary:__pkg__",
        # plus any other graph-allowed consumers
    ],
    tags = ["forma:type=jvm.api"],
)
```

The generator can also emit a small `package_group` file at the workspace root for common type classes:

```starlark
# visibility/BUILD.bazel (or equivalent)
package_group(
    name = "jvm_api",
    packages = ["//..."],  # or a curated allow-list of api users
)
```

Later refinement can tighten this.

**Enforcement order for F-041:** generate first (produces BUILDs that a human or Bazel can consume), then add a check step that loads the same `RestrictionGraph` and reports violations on `deps` that would have failed the Gradle validator. This ordering gives immediate usable artifacts for F-042 while still guaranteeing matrix fidelity.

---

## 5. Rule mapping (JVM kit v1)

Assumptions (documented; no hard lock required for the design):
- Modern `rules_kotlin` (kt_jvm_library / kt_jvm_binary) + `rules_jvm_external` or bzlmod equivalent for external deps.
- Java 11+ compatibility (matches current JVM platform default).
- No Android-specific rules in v1.

| Forma type (id) | Bazel rule       | Key attributes (generated)                                                                 | Visibility notes | Tags |
|-----------------|------------------|--------------------------------------------------------------------------------------------|------------------|------|
| `jvm.api`       | `kt_jvm_library` | `name`, `srcs=glob`, `deps` (only api+library per matrix), `exports` (optional, for contract style), `visibility` (broader) | api contracts reachable by impls + binary | `["forma:type=jvm.api"]` |
| `jvm.impl`      | `kt_jvm_library` | `name`, `srcs`, `deps` (api + library + util + test-util only), `visibility` (narrow)     | granted primarily by the declaring binary | `["forma:type=jvm.impl"]` |
| `jvm.library`   | `kt_jvm_library` | `name`, `srcs`, `deps` (util + test-util), `visibility`                                   | narrow per graph | `["forma:type=jvm.library"]` |
| `jvm.util`      | `kt_jvm_library` | `name`, `srcs`, `deps` (util + library), `visibility`                                     | narrow per graph | `["forma:type=jvm.util"]` |
| `jvm.test-util` | `kt_jvm_library` | `name`, `srcs`, `deps`, `testonly = True` (recommended), `visibility`                     | narrow; usable from test deps | `["forma:type=jvm.test-util"]` |
| `jvm.binary`    | `kt_jvm_binary`  | `name`, `srcs`, `main_class` (from DSL), `deps` (api + impl + library + util + test-util), `visibility = ["//visibility:public"]` | composition root; no self-dep | `["forma:type=jvm.binary"]` |

**deps translation:** only project edges declared via `target(...)` are mapped through the registry. External GAVs are left as future work (they become `maven_install` labels or are ignored in the first spike).

**No content rules** for pure JVM (core `ContentRule` list will be empty for these registrations).

**Test handling:** `testDependencies` in the DSL become test-scoped deps or separate `kt_jvm_test` targets. The core matrix does not currently suffix-validate test edges (see `EdgeKind`); the adapter can start with implementation edges only.

---

## 6. Adapter architecture (SPI on forma-core concepts)

**Hard constraint:** `tools.forma:core` (and the published artifact) must **not** gain any Bazel dependency, direct or transitive. The adapter is a consumer of core.

### Recommended module location
Create a **new top-level directory** (e.g. `bazel-adapter/` or `tools/forma-bazel/`) rather than placing it inside `plugins/`.

Rationale:
- Keeps Gradle platform plugins (`plugins/jvm`, `plugins/android`) cleanly separated from Bazel concerns.
- The adapter can be a thin pure-Kotlin (or Gradle) project whose only production dep is the published `tools.forma:core` (or a composite reference in the monorepo).
- Easier to publish or run as a standalone CLI later.

If a thin Gradle wrapper is useful for discovery/export, it lives in the new module and is **not** required for the core adapter logic.

### Portable model (input)

The adapter receives a **Gradle-free snapshot** after all registrations and target declarations have occurred.

```kotlin
// Conceptual shape (exact serialization decided in F-041)
data class FormaProjectModel(
    val workspaceName: String,
    val targets: List<TargetSnapshot>,
    // Optional: serialized restriction rules or a handle to re-instantiate the graph
    val restrictionGraphSnapshot: RestrictionGraphSnapshot? = null,
)

data class TargetSnapshot(
    val gradlePath: String,          // e.g. ":feature:greeter:impl"
    val typeId: String,              // "jvm.impl"
    val packageName: String? = null, // source package, informational
    val dependencies: List<DepRef>,  // project edges only in v1
    val testDependencies: List<DepRef> = emptyList(),
    val metadata: Map<String, String> = emptyMap(), // e.g. mainClass for binary
)

data class DepRef(val path: String)  // Gradle colon form or normalized label
```

The model can be produced by:
- A Gradle task that walks the project tree after all DSL calls and serializes to JSON.
- In-process handoff inside a composite build for the spike.

### Adapter surface (pure)

```kotlin
// In the adapter module (depends on core for TargetType / RestrictionGraph if needed)
interface FormaToBazel {
    fun generate(model: FormaProjectModel): Map<String /* package dir */, String /* BUILD content */>
    fun check(model: FormaProjectModel, existingBuilds: Map<String, String>): CheckReport
}

data class CheckReport(
    val violations: List<String>,
    val warnings: List<String>,
)
```

The implementation:
- Looks up `TargetType` by `typeId` using a registry or the snapshot.
- Uses `registry.validatorFor(...)` or the raw `RestrictionGraph.isAllowed(...)` to validate/generate `deps`.
- Emits `visibility` according to §4 policy.
- For check: parses minimal attributes (`deps`, `tags`, `name`) from BUILD fragments or uses a Bazel aspect to feed data back.

The adapter **may** depend on core to obtain `DefaultTargetRegistry` behavior or to deserialize a graph snapshot. It must not require a live Gradle `Project`.

### Outputs
- **Generate:** map of package directory → full BUILD.bazel text (or fragments that can be written by a small driver).
- **Check:** structured report of disallowed edges, unknown types, name mismatches, etc.

---

## 7. Two modes for F-041

F-041 will implement at least one (preferably both) of:

1. **Generate** — given a `FormaProjectModel` (exported from Gradle or constructed), emit `BUILD.bazel` files (or patches) that follow the conventions in §3–§5.
2. **Check** — given existing BUILD files (or an aspect report) and a model/graph, report any `deps` edges that violate the `RestrictionGraph` (and name/type mismatches).

### Recommendation for spike order
**Start with model export + Generate**, then add Check (or a post-generate validation pass) inside the same spike.

Rationale:
- Generate produces immediately usable artifacts that can be dropped into a Bazel workspace and exercised (directly supporting F-042).
- The generator naturally exercises the mapping tables, visibility derivation, and label conventions.
- Check can be added as a second sub-step: after emitting, run the restriction graph against the declared `deps` in the generated output (or round-trip the model). This gives strong parity signal without requiring a full BUILD parser up front.
- Exporting the model is a prerequisite for both modes; doing it first de-risks the portable representation.

Alternative (if preferred by implementer): export + check-first gives the strongest “does this preserve the matrix?” answer before worrying about perfect BUILD emission. Either path is acceptable as long as the spike covers the JVM matrix, label rules, and at least one of generate or check using the core `RestrictionGraph`.

The spike must not claim a full end-to-end Bazel build of the sample; it demonstrates the mapping and produces/consumes the artifacts.

---

## 8. Minimal sample sketch (for F-042, design only)

F-042 will add a tiny Bazel workspace (separate from the Gradle `jvm-application/`) that exercises the same concepts. No need to create the tree during F-040.

Illustrative layout (hand-written or generated by the F-041 spike):

```
bazel-sample/                 # new, minimal, not a copy of jvm-application
├── WORKSPACE (or MODULE.bazel)
├── visibility/
│   └── BUILD.bazel           # optional package_group(s)
├── common/
│   ├── library/
│   │   ├── BUILD.bazel
│   │   └── src/main/kotlin/...
│   └── util/
│       └── ...
├── feature/
│   └── greeter/
│       ├── api/
│       │   └── BUILD.bazel   # kt_jvm_library, forma:type=jvm.api, broader visibility
│       └── impl/
│           └── BUILD.bazel   # kt_jvm_library, forma:type=jvm.impl, narrow visibility
└── binary/
    ├── BUILD.bazel           # kt_jvm_binary, main_class, deps on api+impl+shared
    └── src/main/kotlin/...
```

Key exercises for the sample (design goals only):
- At least one `api` → consumed by `impl`.
- `impl` depends on `api` + `library` + `util`; **no** other `impl`.
- `binary` depends on multiple `impl` modules (composition root).
- `impl ↛ impl` is visible in the graph (generator never emits it; checker would reject it).
- All targets carry `tags = ["forma:type=..."]`.
- Runnable via `bazel run //binary:binary`.

The sample should be small enough that a developer can read the BUILD files and the corresponding Gradle declarations side-by-side.

---

## 9. Open questions / decisions

| Decision area                  | Recommendation / status for F-040                                                                 |
|--------------------------------|---------------------------------------------------------------------------------------------------|
| Primary visibility strategy    | Hybrid (generate tight grants from graph + package groups for api + check backstop). Start with generate. |
| Spike order (F-041)            | Model export + Generate first (produces BUILDs for F-042), add Check or validation pass in same spike. |
| Adapter module location        | New top-level (`bazel-adapter/` or `tools/forma-bazel/`). Depends on published `tools.forma:core`. Thin or no Gradle required for core logic. |
| Model export mechanism         | Gradle task (or settings hook) → JSON snapshot of targets + type ids + declared edges. In-process for composite spike is OK. |
| rules_kotlin source            | Assume modern `rules_kotlin` (kt_jvm_*). Pinning / bzlmod choice left to F-041/F-042.             |
| bzlmod vs WORKSPACE            | Both acceptable for the spike. bzlmod preferred for new sample if low friction; WORKSPACE is simpler for a throwaway spike. |
| External dep mapping           | Note only. `maven_install` or bzlmod `maven` rules. Not required for JVM project-edge validation in v1. |
| Test edge handling             | Start with implementation edges (current core behavior). `EdgeKind.TEST` is future.               |
| ContentRule for JVM            | None today. Adapter should carry the list from `TargetRegistration` and ignore or warn for JVM.   |
| Android kit                    | Same adapter surface + model. Detailed `rules_android` mapping deferred (post F-042).             |
| NameMatcher extensibility      | Future: a Bazel-aware matcher could live in the adapter or be registered via core. Not required now. |
| Binary mainClass               | Carried in model metadata from the DSL. `kt_jvm_binary.main_class` is a direct mapping.           |

---

## 10. Implementation plan

Ordered steps that keep F-041 and F-042 small and reviewable. All work stays on `v2` base; PRs target `v2`.

### F-041 — Spike: generate or check Bazel BUILD from forma declarations

1. Add a new top-level module (or thin tree) that depends on `tools.forma:core` (composite or published).
2. Define the portable `FormaProjectModel` + snapshot types (Kotlin data or JSON schema). Keep it minimal.
3. Implement model export from the JVM sample (Gradle task or a small settings plugin hook that runs after registrations). Serialize targets with their type ids and declared `target(...)` edges.
4. Implement the generator: given the model + JVM registry (or snapshot graph), emit `BUILD.bazel` text per the label conventions (§3), rule table (§5), and visibility policy (§4).
5. Wire a “check” pass (or post-generate validation) that re-loads the `RestrictionGraph` and reports any `deps` that would violate `isAllowed`.
6. Run the generator against a subset of `jvm-application/` (or a constructed model) and capture the output as golden text or files for review. Do **not** require a green Bazel build.
7. Update `docs/BAZEL-ADAPTER.md` with any decisions made during the spike and add a short “Spike results” section (commands used, sample output, limitations).
8. Add a PROGRESS entry and leave F-042 as the next todo.

Acceptance boundaries for F-041: model export + generate (or check) works for the six JVM types, respects the matrix (especially `impl ↛ impl`), produces labels following the documented conventions, and the adapter has no compile/runtime dependency on Bazel inside core.

### F-042 — Minimal Bazel sample using forma-core concepts

1. Create a tiny new tree (`bazel-sample/` or similar) with a WORKSPACE/MODULE.bazel, minimal `kt_jvm_*` targets, and sources sufficient to exercise the matrix.
2. Include at least: one feature with `api` + `impl`, one `library` or `util`, and a `binary` that composes multiple impls.
3. Use generated BUILD content from F-041 where possible, or hand-author minimal equivalents that match the design.
4. Demonstrate (in docs or a README in the sample) that cross-feature code goes only through `api`, and that an `impl` depending on another `impl` is rejected by the model/check.
5. Document how to run the sample with Bazel (`bazel build //...`, `bazel run //binary:binary`).
6. Cross-link from `BAZEL-ADAPTER.md`, `JVM-SAMPLE.md` (or a new “Future: Bazel” note), and README Progress.
7. Set F-042 to done in TICKETS; append PROGRESS.

Acceptance boundaries for F-042: the sample is small, self-contained, exercises the documented mapping and restriction semantics, and is clearly labeled as experimental / non-production.

---

## References

- Core types & engine: `plugins/core/src/main/java/tools/forma/core/{target, restriction, validation}/**`
- JVM kit: `plugins/jvm/src/main/java/tools/forma/jvm/target/{JvmTargetTypes.kt, JvmTargetRegistry.kt}`
- Matrix tests: `plugins/jvm/src/test/java/tools/forma/jvm/target/JvmTargetRegistryTest.kt`
- Sample layout: `jvm-application/{binary,feature,common}/**/build.gradle.kts`
- Related docs: `VISION.md`, `forma-core-api.md`, `JVM-TARGETS.md`, `JVM-SAMPLE.md`, `ARCHITECTURE.md`, `DEPENDENCY-MATRIX.md`

---

## Spike results (F-041)

**Landed on branch `forma/F-041-bazel-spike`.** Top-level pure-Kotlin module `bazel-adapter/` depends only on `tools.forma:core` (composite `includeBuild("../plugins")` + dependency substitution). Core remains Bazel-free.

### Delivered
| Piece | Location |
|-------|----------|
| Portable model | `tools.forma.bazel.model.FormaProjectModel` + `TargetSnapshot` / `DepRef` |
| Adapter API | `FormaToBazel` + `JvmBazelAdapter` (`generate` + `check`) |
| JVM matrix (core-only) | Same six types + allow-lists as `registerJvmDefaults()` (duplicated in adapter; comment links `JvmTargetRegistry`) |
| Fixture | `JvmApplicationFixture` — 8 targets mirroring `jvm-application/` edges |
| Unit tests | `JvmBazelAdapterTest` — labels, tags, rule kinds, `impl ↛ impl`, illegal check, round-trip |
| Example BUILD output | `bazel-adapter/examples/jvm-application-build/{binary,feature/greeter/{api,impl}}/BUILD.bazel` |
| Driver | `./gradlew runSample` prints all packages + check summary |
| Module README | `bazel-adapter/README.md` |

### Commands verified (OpenJDK 17 + `scripts/env-mac.sh`)
```bash
cd bazel-adapter && ./gradlew test        # BUILD SUCCESSFUL
cd bazel-adapter && ./gradlew runSample   # violations=0 warnings=0
cd plugins && ./gradlew :core:test        # BUILD SUCCESSFUL (unchanged)
```
No Bazel binary required for the spike.

### Decisions made during spike
- **Composite substitution** for `tools.forma:core` → local `:core` (no mavenLocal required for day-to-day).
- **Matrix duplication** in adapter vs depending on `:jvm` — chosen to keep classpath free of Kotlin Gradle Plugin / platform facades.
- **Hand fixture** instead of Gradle export task (export deferred; fixture is enough for F-041 + F-042 seeds).
- **Visibility:** reverse edges from declared legal consumers + `//visibility:public` for binary; check mode is the fidelity backstop.
- **Check** validates model edges via `RestrictionGraph.isAllowed`; optional light scan of generated text for cross-impl labels. No full Starlark parser.
- **Illegal edges:** generator filters them out of `deps`; `check` reports them as violations (e.g. greeter-impl → calculator-impl).

### Limitations (unchanged / deferred)
- JVM kit only; no Android types; no external GAV → `maven_install`.
- No live Gradle model export task.
- No full Bazel workspace / `bazel build` (F-042).
- `testDependencies` carried in model but not emitted as `kt_jvm_test` yet.
- Example tree commits a subset of packages (binary + greeter); full set available via `runSample`.

### Next
**F-042** — minimal Bazel sample workspace using generated or hand-authored BUILD files that exercise the same matrix (`bazel run //binary:binary`).