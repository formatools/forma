# Forma Strategic Vision

## Product goal

Make **Forma** a working product for **Android** first, then adapt the same approach for **JVM applications**, then for **Bazel**.

## Core product principle — keep structure flat

Forma exists to **protect a flat, role-typed module graph**. Each target type is a
**role** (`api`, `impl`, `androidRes`, `viewBinding`, `widget`, `androidUtil`,
`uiLibrary`, composition roots, …), not a vague “Android library” bucket.

- **Prefer the most specific target** that matches the module’s job.
- **Composition of features** happens only at `androidApp` / `androidBinary`
  (and JVM `binary`) — never by stacking feature `impl`s on each other.
- **Generic escape hatches fight the product.** `androidLibrary` is **deprecated**
  (F-060+): it collides with JVM `library` on the `library` suffix and invites
  mixed DI/UI/res dumps that the matrix cannot reason about. Migrate to
  role-specific targets (see [`GETTING-STARTED.md`](GETTING-STARTED.md) cheat
  sheet and F-060 notes in `PROGRESS.md`).

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
   patterns. (`androidLibrary` deprecated — do not teach it as the default.)
2. **JVM** — pure JVM targets and samples reusing the same restriction model.
3. **Bazel** — later adapter that reuses forma-core concepts (types + restrictions) rather than forking the rules engine.

## Sequencing

1. **Android productization** — modern toolchain, green CI/sample, strict types, publishable plugin.
2. **Extract forma-core** — peel framework concerns out of the Android plugin while Android keeps working.
3. **JVM implementation** — prove core is portable.
4. **Bazel** — design + implement adapter once core is stable (see [`docs/BAZEL-ADAPTER.md`](BAZEL-ADAPTER.md) for the F-040 mapping design).
5. **Flat-structure hardening** — deprecate/remove generic targets that undermine role typing (P6 / F-060+).

## Working principles

- Prefer small, reviewable PRs against `formatools/forma` (fork/PR flow as needed).
- Keep the sample `application/` building as the product gold standard.
- Document user-facing changes in `README.md`.
- Track work only via `TICKETS.md` + `docs/PROGRESS.md` (and GitHub issues/PRs).
- Do not invent unrelated features; stay on the prioritized ticket list.
