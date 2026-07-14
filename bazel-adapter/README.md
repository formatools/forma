# bazel-adapter (F-041 spike)

JVM-first adapter that converts a portable `FormaProjectModel` (targets + declared edges) into Bazel `BUILD.bazel` fragments (or validates them) while preserving the forma-core `RestrictionGraph` discipline.

**Core invariant:** `impl` may never depend on another `impl`; composition happens only at `binary`. The adapter enforces this using `RestrictionGraph.isAllowed` from `tools.forma:core`.

## What it contains

- `tools.forma.bazel.model.*` — portable snapshot types (no Gradle APIs)
- `JvmBazelAdapter` implementing `FormaToBazel.generate()` + `check()`
- Hand-built fixture from `jvm-application/` (8 targets)
- Unit tests + committed example BUILD files under `examples/`

## Running

Requires JDK 17+ (Temurin recommended).

```bash
# From repo root
source scripts/env-mac.sh

cd bazel-adapter
./gradlew test
./gradlew runSample   # prints generated BUILD text + check report
```

You do **not** need Bazel installed for the spike.

## Limitations (spike)

- Only JVM 6-type matrix (`jvm.api` ... `jvm.binary`)
- Project edges only (no external catalog GAV translation)
- Visibility is computed from declared consumers in the model + graph (hybrid; good enough)
- No full BUILD parser — check validates the *model* edges (and lightly inspects generated text)
- No `kt_jvm_test` emission yet (testDependencies carried in model but ignored for rule emission)
- No content rules (pure JVM has none)

## Golden / committed artifacts

- `examples/jvm-application-build/` — representative generated BUILD.bazel files
- Tests assert key labels, absence of illegal edges, round-trip cleanliness

See `docs/BAZEL-ADAPTER.md` for the full mapping design and F-041 acceptance criteria.
