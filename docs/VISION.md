# Forma Strategic Vision

## Product goal

Make **Forma** a working product for **Android** first, then adapt the same approach for **JVM applications**, then for **Bazel**.

## Root principles (product axioms)

These are the bar for every API and implementation change. Inferable from the
README meta-build pitch; stated here so they cannot drift.

1. **Bazel-like rules** — Configure behavior **once** on the target type (rule).
   Call sites use a **minimal set of static attributes** (package, deps, declared
   rule fields). Using a type always gets that type’s behavior (plugins, features,
   content rules, matrix). No per-module tool shopping.
2. **One global way** — Built to scale to very large orgs (thousands of developers).
   There is **one** supported way to express a given concern, **global for the
   entire project**. Extensibility means extending the shared type/config system,
   not inventing module-local Gradle dialects.
3. **Everything is explicit** — Structure and boundaries are declared, not implied
   by hidden convention soup. Tradeoff: more configuration at the **type / project**
   layer. Remedy: **tooling** so large-scale changes stay easy and reliable
   (check / generate / migrate — includer, adapters, [`FLEET-TOOLING.md`](FLEET-TOOLING.md)).

### Consequences (do not treat as separate products)

- **Flat, role-typed graph** — most specific role; no generic buckets (`androidLibrary` removed).
- **Composition only at roots** — `androidApp` / `androidBinary` / JVM `binary`; `impl` ↛ `impl`.
- **Closed dependency matrix** — allow-listed project edges only.
- **Portable forma-core** — types, restrictions, validation, registry; platforms adapt.
- **External plugins** — type-owned, auto-apply; see [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md).

Implementation debt vs these axioms is tracked under **P7–P8** in `TICKETS.md`.

## Core product principle — keep structure flat

Forma exists to **protect a flat, role-typed module graph**. Each target type is a
**role** (`api`, `impl`, `androidRes`, `viewBinding`, `widget`, `androidUtil`,
`uiLibrary`, composition roots, …), not a vague “Android library” bucket.

- **Prefer the most specific target** that matches the module’s job.
- **Composition of features** happens only at `androidApp` / `androidBinary`
  (and JVM `binary`) — never by stacking feature `impl`s on each other.
- **Generic escape hatches fight the product.** `androidLibrary` was **removed**
  (F-063): it collided with JVM `library` on the `library` suffix and invited
  mixed DI/UI/res dumps that the matrix cannot reason about. Use role-specific
  targets only (see [`GETTING-STARTED.md`](GETTING-STARTED.md) and
  [`ANDROID-LIBRARY-DEPRECATION.md`](ANDROID-LIBRARY-DEPRECATION.md)).

## Architectural split

### `forma-core` (framework)

The portable core for:

- Building **dependency types** and **restriction / visibility rules**
- Validation framework for project structure
- Tooling / utils (e.g. external dependency catalogs, dependency application helpers)
- Target-type registration API (pluggable implementations)

`forma-core` must not assume Android, AGP, or Dagger. It is the substrate other platforms stand on.

### Platform implementations (examples)

Concrete stacks built **on** forma-core:

1. **Android** — role-typed target set (`api`, `impl`, `androidUtil`, `uiLibrary`,
   `androidRes`, `viewBinding`, `androidBinary`, …) with a strict dependency
   matrix; Dagger2-friendly `api`/`impl` conventions; external deps catalog
   patterns. (`androidLibrary` removed in F-063 — do not teach or reintroduce it.)
2. **JVM** — pure JVM targets and samples reusing the same restriction model.
3. **Bazel** — later adapter that reuses forma-core concepts (types + restrictions) rather than forking the rules engine.

## Sequencing

1. **Android productization** — modern toolchain, green CI/sample, strict types, publishable plugin.
2. **Extract forma-core** — peel framework concerns out of the Android plugin while Android keeps working.
3. **JVM implementation** — prove core is portable.
4. **Bazel** — design + implement adapter once core is stable (see [`docs/BAZEL-ADAPTER.md`](BAZEL-ADAPTER.md) for the F-040 mapping design).
5. **Flat-structure hardening** — remove generic targets that undermine role typing (P6 / F-060–F-063; `androidLibrary` gone).
6. **Uniform target plugins** — Bazel-like: plugin on the **target type**, auto-apply on every call site; call sites are attributes-only; chain `withPlugin` **removed** (P7 / F-070–F-073 + F-081; [`TARGET-PLUGINS.md`](TARGET-PLUGINS.md)).
7. **Principle-alignment implementation** — close remaining gaps vs root principles (P8 / F-080–F-085; audit: [`PRINCIPLE-AUDIT.md`](PRINCIPLE-AUDIT.md)).

## Working principles

- Prefer small, reviewable PRs against `formatools/forma` (fork/PR flow as needed).
- Keep the sample `application/` building as the product gold standard.
- Document user-facing changes in `README.md`.
- Track work only via `TICKETS.md` + `docs/PROGRESS.md` (and GitHub issues/PRs).
- Do not invent unrelated features; stay on the prioritized ticket list.
