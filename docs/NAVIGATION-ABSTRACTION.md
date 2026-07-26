# Navigation abstraction (F-102 / GH #46)

**Status:** design shipped (this doc). Implementation is **not** a Forma engine
feature — it is **sample architecture + progressive teaching**, with optional
reuse of existing Path B `navigationRes` only where type=rule already fits.

| Slice | Ticket | Scope |
|-------|--------|--------|
| Design (this doc) | **F-102** | Problem, layers A/B, v1 approach, rejects, ticket map |
| Sample ports + root adapter | **F-111** | Gold-standard `application/` refactor |
| Progressive example | **F-112** | `examples/android/12-navigation-ports` |
| Optional type tweaks | only if evidence | No new forever router DSL in plugins |

---

## 1. Problem

The gold-standard sample uses **Jetpack Navigation** with **Safe Args** codegen.
That is a fine product choice for the APK. The failure mode is **bleed**: feature
`impl` / ViewModels / shared UI helpers take **hard dependencies** on navigation
APIs and generated symbols, so structure and presentation are glued to one tool.

### Concrete bleed in `application/` today (GH #46)

| Location | Leak |
|----------|------|
| `CharactersListFragment` | `findNavController().navigate(CharactersListFragmentDirections…)` — Safe Args `*Directions` + NavController in feature `impl` |
| `HomeViewModel` (home `viewbinding`) | `NavController` listener + `core.navigation.library.R.id.*` graph destination ids |
| `home/impl` bottom nav | `setupWithNavController(navGraphIds=…)` multi-backstack helper wired to graph R.ids |
| Feature `impl` / `viewbinding` `build.gradle.kts` | `androidx.navigation` + `target(":core:navigation:res")` so codegen/R reach UI modules |
| `common/extensions/android-util` | Multi-backstack `BottomNavigationView.setupWithNavController` — useful, but Nav-typed surface shared widely |

In-code TODOs already name the gap:

- *“Need abstract navigation layer here”* (`CharactersListFragment`)
- *“Move out from here strong deps of navigation component”* (`HomeViewModel`, issue #46)

### Why this hurts Forma structure

1. **Feature `impl` becomes tool-shaped** — swapping Navigation Component,
   Compose Navigation, or a router library rewrites every feature, not one adapter.
2. **Codegen becomes a cross-feature API** — `*Directions` and graph `R.id`s are
   not domain contracts; they couple modules to XML graph layout and safe-args packages.
3. **Composition boundary blurs** — navigation wiring belongs next to the host
   (`androidApp` / `androidBinary` / dedicated adapter), not deep in every feature.
4. **Teaching risk** — if the sample’s happy path is raw `findNavController` in
   `impl`, learners copy that into every module and ignore the flat graph.

This ticket is **not** “replace Jetpack Navigation with a Forma-owned router.”
It is **protect presentation and the module graph from navigation tooling**.

---

## 2. Alignment with Forma root principles

Full axioms: [`VISION.md`](VISION.md). Consequences for navigation:

| Principle | Navigation consequence |
|-----------|------------------------|
| **Bazel-like rules** | Safe-args / nav graph modules stay **type-owned** (`navigationRes`). Call sites = package + deps. No per-module plugin shopping. |
| **One global way** | One presentation-port pattern for the sample (and the progressive example). One Path B type for nav graphs. Do not teach raw Nav *and* ports as dual happy paths. |
| **Explicit structure + tooling** | Destinations and “who may navigate where” are declared as **app contracts** (sealed types / ports), not implied by whichever fragment imported `Directions`. |
| **Composition only at roots** | Jetpack `NavController`, graph inflation, Safe Args execute **only** in composition-root or a dedicated adapter module depended on by the root — never `impl` → `impl` for “nav wiring.” |
| **Closed dependency matrix** | No new `navImpl` / free-form edges unless a future ticket proves a matrix change. Default: ports live on existing roles (`api`, `androidUtil`, root). |
| **External plugins type-owned** | Keep [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md) Path B `navigationRes`. Never restore `.withPlugin` / free-form plugin id lists / `androidLibrary`. |

**Forma plugins do not embed Cicerone, Compose Navigation, or a custom router
framework.** Optional external libraries remain **app dependencies** behind the
same ports if a product chooses them.

---

## 3. Two layers (do not conflate)

### Layer A — Presentation-layer navigation ports (app architecture)

**Owner:** the product / sample app, not `tools.forma` engine DSLs.

**Goal:** feature `api` / `impl` / ViewModels express **intent** (“open character
detail for id X”, “leave full-screen”, “back”) without importing:

- `androidx.navigation.*`
- Safe Args `*Directions` / `*Args`
- Navigation graph `R.id` / `R.navigation`

**Typical shapes (v1 — pick one style and stick to it in the sample):**

```text
feature api or thin shared androidUtil
  └── Navigator (interface)  and/or  sealed AppDestination / feature events
feature impl / ViewModel
  └── emits destinations or one-way nav events (no NavController)
composition root or navigation adapter module
  └── implements Navigator: NavController + Safe Args + graph ids
navigationRes (existing)
  └── XML graphs + type-owned safe-args codegen (consumed only by adapter/root)
```

| Piece | Suggested home | Depends on androidx.navigation? |
|-------|----------------|-----------------------------------|
| `Navigator` / destination types / nav events | feature `api`, or small shared `androidUtil` / `api` used by features | **No** |
| Feature UI + ViewModels | feature `impl` / `viewbinding` | **No** (after F-111) |
| Adapter (`NavigatorImpl`, graph host, bottom-nav wiring) | `androidApp` / dedicated module next to root (e.g. `androidUtil` or root-owned impl) | **Yes** |
| Nav graphs + Safe Args | `core/navigation/res` via **`navigationRes`** | plugin on **type**, not call-site shopping |

**Dependency direction:**

```text
feature impl ──► feature api (ports) ──► (no nav library)
androidApp / adapter ──► feature api + navigationRes + androidx.navigation
androidBinary ──► androidApp + feature impls + …
```

Feature `impl` must **not** depend on `core/navigation/res` once ports land
(unless a rare, documented exception — default is adapter-only).

### Layer B — Optional Forma target / types

**Only** when type=rule genuinely fits. Today that bar is already met by:

| Existing | Role |
|----------|------|
| Path B **`navigationRes`** | Derived `androidRes` + type-owned `androidx.navigation.safeargs.kotlin` |
| Safe Args on buildscript classpath | `extraPlugins` / catalog — jars only; apply is type-owned ([PROJECT-CONFIGURATION.md](PROJECT-CONFIGURATION.md)) |
| Cache-friendly plugin line | **F-089** / GH #110 — keep `navigation-safe-args-gradle-plugin` on a line with relative `navigationFiles` sensitivity ([CONFIGURATION-PERFORMANCE.md](CONFIGURATION-PERFORMANCE.md)) |

**Do not invent** in Forma plugins:

- A forever DSL for routers, back stacks, or `BackTo(Class<Destination>)` engines
- Built-in Cicerone (or any third-party navigator) integration
- A new first-class `navImpl` / `navigationLibrary` target that bypasses the matrix
  “because navigation is special”
- Dual paths: plain `androidRes` + `.withPlugin(safe-args)` **and** `navigationRes`

Optional future type work (only with evidence, separate ticket): e.g. document a
second derived type if graphs must split by product flavor — still Path B, still
no call-site plugin lists.

---

## 4. Recommended v1 approach (sample + progressive example)

**Stay on Jetpack Navigation + Safe Args for the APK.** Abstract at the
**presentation boundary**, not by forking a new navigation product inside Forma.

### 4.1 Contracts (no AndroidX Navigation)

Illustrative sketch (names flexible in F-111):

```kotlin
// e.g. feature/characters/list/api or shared navigation-api androidUtil
sealed interface AppDestination {
    data class CharacterDetail(val id: Long) : AppDestination
    // …home tabs / favorite as needed — prefer feature-local sealed types
    // composed at root if the app grows
}

interface Navigator {
    fun navigate(to: AppDestination)
    fun back()
}
```

ViewModels expose **one-way events** (`SingleLiveData` / `SharedFlow` / existing
MVVM helpers) carrying `AppDestination` or feature-specific nav events.
Fragments/Activities **collect** and call `Navigator` — or the adapter observes
events at the host. Either way, **no** `*Directions` in feature modules.

Optional GH #46 idea (`BackTo` with `Class<Destination>`): fine as an **app-level**
API on the port if the sample needs typed back-stack pops; implement with Nav
APIs **only inside the adapter**. Do not promote it to a Forma plugin API.

### 4.2 Adapter at the composition boundary

- Implement `Navigator` where `NavController` is owned (activity / root nav host /
  home host fragment coordinator).
- Map `AppDestination` → Safe Args directions / `navigate(resId)` / deep links.
- Keep multi-backstack `setupWithNavController` helpers in a module that **already**
  may depend on Navigation (e.g. root or nav adapter `androidUtil`) — not in
  feature ViewModels.
- Replace `HomeViewModel`’s `NavController` + raw `R.id` set with either:
  - destination **logical** ids / sealed “chrome mode” events from the adapter, or
  - chrome state pushed in by the host after destination changes (adapter → VM).

### 4.3 Graphs stay on `navigationRes`

Unchanged happy path for **resources + codegen**:

```kotlin
// application/core/navigation/res/build.gradle.kts
navigationRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(androidx.navigation),
)
```

See [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md) Path B and sample
`build-dependencies` `NavigationRes.kt`. **F-089** cache notes remain the
build-performance contract; abstraction work must not reintroduce absolute-path
safe-args lines or call-site plugin apply.

### 4.4 Progressive example shape (F-112 — implement later)

`examples/android/12-navigation-ports/` (name locked here for the ladder):

| Module role | Responsibility |
|-------------|----------------|
| `navigation-api` (`api` or `androidUtil`) | `Navigator` + sealed destinations — **zero** androidx.navigation |
| `feature/*/impl` | UI emits destinations / events only |
| `navigation-adapter` (`androidUtil` or root-local) | `Navigator` impl + optional tiny graph |
| `core/nav/res` or `navigationRes` | XML + safe-args (Path B), depended on by **adapter/root only** |
| `androidApp` / `androidBinary` | Composition: wire adapter + features |

Keep the example **minimal** (one or two destinations), not a second Marvel app.
Curriculum: add a row on [`PROGRESSIVE-EXAMPLES.md`](PROGRESSIVE-EXAMPLES.md) when
F-112 lands (placeholder noted there after this design).

### 4.5 External libraries (Cicerone, Compose Navigation, …)

Documented pattern only:

1. Ports stay the same (Layer A).
2. Swap or dual-write the **adapter** implementation.
3. Do **not** add Forma engine support or a second sample happy path that teaches
   the library without ports.

---

## 5. Explicit reject list

| Reject | Why |
|--------|-----|
| Per-module plugin lists / `.withPlugin(safe-args)` | Type-owned plugins only; dual happy path; F-081 removed chain API |
| Restoring `androidLibrary` for “nav stuff” | Removed F-063; use role-specific targets |
| `impl` → `impl` edges for graphs or navigators | Matrix + composition-at-roots |
| Teaching raw `findNavController` / `*Directions` in feature `impl` as the **structural** happy path | Re-creates GH #46 bleed; ports are the happy path after F-111 |
| Dual happy paths (ports **or** raw Nav in features “both fine”) | Violates one global way for the sample/docs |
| Forever Forma DSL / built-in router framework in `plugins/` | Out of product scope; competes with app libraries; not type=rule |
| Embedding Cicerone (or similar) inside Forma | Optional **app** dependency behind ports only |
| New matrix type `navImpl` without a separate evidence-based ticket | Prefer existing `api` / `androidUtil` / root |
| Changing validators “so features can depend on navigationRes freely” | Wrong layer; fix direction of deps via adapter |
| Large greenfield navigation library as this ticket’s deliverable | Design + progressive/sample slices only |

---

## 6. Ticket map / follow-ups

| ID | Status intent | Deliverable |
|----|---------------|-------------|
| **F-102** | **done** (design) | This document + cross-links + board split |
| **F-111** | todo | Sample: presentation ports + adapter at composition root; strip Nav/Safe Args from feature `impl`/VM where practical; keep `navigationRes`; `application/` still green |
| **F-112** | todo | Progressive example `examples/android/12-navigation-ports` + ladder/skill links |
| F-102c (optional) | only if needed | Tiny type/docs tweak to `navigationRes` / TARGET-PLUGINS — **no** router engine |

**Ordering:** F-111 may land before or after F-112; prefer **F-112 first** if the
sample refactor is large (teach on a minimal graph, then migrate Marvel). Either
order is fine as long as both follow this design.

**Out of scope for the F-102 family:** hybrid targets (F-103), stub/IDE sync
(F-104), KMP, Plugin Portal, matrix rewrites, engine DSL.

### Definition of done (implement slices)

**F-111**

- [ ] Navigator / destinations (or equivalent) with **no** androidx.navigation in
      feature contracts used by `impl`/VM
- [ ] Adapter at root (or root-owned module) owns `NavController` + Safe Args
- [ ] Feature modules no longer depend on `core/navigation/res` unless justified
      in PROGRESS
- [ ] Comments/TODOs for GH #46 in touched files resolved or narrowed
- [ ] `application/` `./gradlew :binary:assembleDebug` (or full `build`) green on
      real host — never invent green

**F-112**

- [ ] Example builds; README explains ports vs adapter vs `navigationRes`
- [ ] `PROGRESSIVE-EXAMPLES.md` matrix row + examples README link
- [ ] No `.withPlugin`; no `androidLibrary`

---

## 7. References

| Doc / code | Why |
|------------|-----|
| GH **#46** | Original “abstract navigation layer” / strong Nav deps |
| [`VISION.md`](VISION.md) | Root principles |
| [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md) | Path B `navigationRes`, no call-site plugins |
| [`CONFIGURATION-PERFORMANCE.md`](CONFIGURATION-PERFORMANCE.md) | F-089 Safe Args task cache |
| [`SAMPLE-APP.md`](SAMPLE-APP.md) | Gold-standard layout; nav graphs under `core/navigation/res` |
| [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) | Closed edges — unchanged by this design |
| [`PROGRESSIVE-EXAMPLES.md`](PROGRESSIVE-EXAMPLES.md) | Ladder; step 12 reserved for F-112 |
| `application/core/navigation/res` | Live `navigationRes` call site |
| `CharactersListFragment` / `HomeViewModel` | Bleed examples + TODOs |

---

## 8. Summary

1. **Problem** = tooling/codegen bleed into features, not “missing Forma router.”
2. **Layer A** = app presentation ports; features stay Nav-free.
3. **Layer B** = keep **`navigationRes`** (+ F-089 cache discipline); no new engine DSL.
4. **v1** = Jetpack Navigation behind an adapter at the composition root; teach via
   progressive example + sample refactor (F-111 / F-112).
5. **Reject** plugin shopping, impl→impl nav graphs, dual happy paths, and
   Forma-owned forever navigation frameworks.
