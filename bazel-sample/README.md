# bazel-sample (F-042)

**EXPERIMENTAL / NON-PRODUCTION**

This is a minimal, self-contained Bazel workspace that exercises the same
forma-core JVM dependency matrix concepts as `jvm-application/` and the F-041
`bazel-adapter` spike.

It demonstrates:

- `api` + `impl` feature boundaries (two features: greeter, calculator)
- `library` and `util` shared modules
- `binary` as the sole composition root (multiple impls meet only here)
- `tags = ["forma:type=..."]` on every target
- Label conventions: `//feature/greeter/impl:impl`, `//binary:binary`
- **No `impl` → `impl` dependencies allowed** (enforced by model + documented in BUILD authoring)

The BUILD files are hand-authored to match the output shape produced by
`JvmBazelAdapter.generate()` from F-041 (see `bazel-adapter/examples/` and
`bazel-adapter/src/main/kotlin/tools/forma/bazel/adapter/JvmBazelAdapter.kt`).

Cross-feature code goes **only through `api`** (see `Main.kt` and the impls).
An `impl` depending on another `impl` is rejected by the `RestrictionGraph`
used in the adapter (see illegal fixture test below).

---

## Prerequisites

- JDK 17+ (use the repo helper):
  ```bash
  source /path/to/forma/scripts/env-mac.sh
  java -version   # must be 17+
  ```
- bazelisk (recommended) or Bazel 7.x+
  ```bash
  brew install bazelisk
  ```
- Internet access on first run (rules_kotlin + toolchain downloads).

---

## Layout

```
bazel-sample/
├── .bazelversion          # 7.4.1
├── WORKSPACE              # http_archive pins (bzlmod disabled in .bazelrc)
├── .bazelrc
├── README.md
├── binary/
│   ├── BUILD.bazel
│   └── src/main/kotlin/.../Main.kt
├── common/
│   ├── library/...
│   └── util/...
└── feature/
    ├── greeter/{api,impl}/...
    └── calculator/{api,impl}/...
```

---

## Build and run

```bash
cd bazel-sample

# Recommended: bazelisk (uses .bazelversion)
bazelisk build //...

bazelisk run //binary:binary
```

Expected output (or very similar):

```
Hello, World! (2 + 3 = 5)
Bazel sample (forma concepts) build + run successful.
```

Using plain `bazel` (if you have Bazel 7+ installed and it matches):

```bash
bazel build //...
bazel run //binary:binary
```

---

## Dependency matrix exercised (JVM)

| Consumer type | Allowed dependency types          | Notes in this sample |
|---------------|-----------------------------------|----------------------|
| `jvm.api`     | api, library                      | greeter/calculator apis have no deps |
| `jvm.impl`    | api, library, util, test-util     | impls depend **only** on their api + commons. **Never another impl**. |
| `jvm.library` | util, test-util                   | library has none here |
| `jvm.util`    | util, library                     | util has none here |
| `jvm.binary`  | api, impl, library, util, test-util | sole place that depends on multiple impls |

See:
- `docs/DEPENDENCY-MATRIX.md`
- `docs/JVM-TARGETS.md`
- `plugins/jvm/.../JvmTargetRegistry.kt` (source of truth matrix)
- `bazel-adapter/...` for the core `RestrictionGraph` re-implementation used by the adapter

---

## Enforcement of `impl` ↛ `impl`

This rule is **core forma discipline**, not a Bazel visibility accident.

1. The `JvmBazelAdapter` (F-041) uses `RestrictionGraph.isAllowed(...)` (from `tools.forma:core`) and **never emits** an illegal edge in generated `deps`.
2. `check()` on an illegal model reports violations.

Run the adapter test that proves the illegal case is caught:

```bash
cd ../bazel-adapter
source ../scripts/env-mac.sh
./gradlew test --tests "*JvmBazelAdapterTest*illegal*"
# or simply
./gradlew test
```

Look at:
- `bazel-adapter/src/test/kotlin/tools/forma/bazel/JvmBazelAdapterTest.kt`
- `bazel-adapter/src/main/kotlin/tools/forma/bazel/sample/JvmApplicationFixture.kt` (`modelWithIllegalImplToImpl`)

In this sample tree there are **zero** `//.../impl:impl` references inside any `impl/` BUILD.

If you manually add an illegal dep to a BUILD and run the adapter check against a model, it surfaces as a violation (the same check the Gradle validators perform at configuration time).

---

## How the BUILD files were produced

- Labels and target names follow §3 of `docs/BAZEL-ADAPTER.md`
- Every target carries `tags = ["forma:type=jvm.<kind>"]`
- Visibility is narrow except for the public binary
- `kt_jvm_library` / `kt_jvm_binary` + `srcs = glob(["src/main/kotlin/**/*.kt"])`
- `main_class` on the binary
- `load("@rules_kotlin//kotlin:jvm.bzl", ...)` — note: the F-041 generator omits the load line; sample BUILDs must include it

See also the golden fragments in `bazel-adapter/examples/jvm-application-build/`.

---

## Limitations (experimental)

- No external Maven deps (no `maven_install` / bzlmod maven wiring).
- No tests (`kt_jvm_test` / test-util usage).
- No Android / `rules_android`.
- Toolchain is default (Java 11+ compatible with rules_kotlin).
- Not intended to replace `jvm-application/` Gradle build.
- Uses WORKSPACE + `--noenable_bzlmod` (bzlmod left for a later polish); pins may need bumps as Bazel/rules_kotlin evolve.

This sample exists to prove that the forma-core concepts (matrix, api contracts, composition root, no-impl-to-impl) are portable to Bazel with the mapping designed in F-040/F-041.

---

## Related

- [Bazel adapter design](../docs/BAZEL-ADAPTER.md)
- [JVM sample](../docs/JVM-SAMPLE.md)
- [forma-core API](../docs/forma-core-api.md)
- `bazel-adapter/` (generator + checker + tests)
