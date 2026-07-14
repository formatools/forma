# Forma Strategic Vision

## Product goal

Make **Forma** a working product for **Android** first, then adapt the same approach for **JVM applications**, then for **Bazel**.

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

1. **Android** — full target set (`api`, `impl`, `androidLibrary`, `androidBinary`, …) with strict dependency matrix based on the current Forma implementation; Dagger2-friendly `api`/`impl` conventions; external deps catalog patterns.
2. **JVM** — pure JVM targets and samples reusing the same restriction model.
3. **Bazel** — later adapter that reuses forma-core concepts (types + restrictions) rather than forking the rules engine.

## Sequencing

1. **Android productization** — modern toolchain, green CI/sample, strict types, publishable plugin.
2. **Extract forma-core** — peel framework concerns out of the Android plugin while Android keeps working.
3. **JVM implementation** — prove core is portable.
4. **Bazel** — design + implement adapter once core is stable (see [`docs/BAZEL-ADAPTER.md`](BAZEL-ADAPTER.md) for the F-040 mapping design).

## Working principles

- Prefer small, reviewable PRs against `formatools/forma` (fork/PR flow as needed).
- Keep the sample `application/` building as the product gold standard.
- Document user-facing changes in `README.md`.
- Track work only via `TICKETS.md` + `docs/PROGRESS.md` (and GitHub issues/PRs).
- Do not invent unrelated features; stay on the prioritized ticket list.
