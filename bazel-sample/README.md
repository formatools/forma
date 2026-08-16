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

The BUILD files are **Starlark call sites** (`jvm_api` / `jvm_impl` / `jvm_library` / `jvm_util` / `jvm_binary`) defined in [`forma/defs.bzl`](forma/defs.bzl). Each symbol is a Forma **TargetType**: it owns rule kind, `tags`, default `srcs`, and the closed matrix (`forma/matrix.bzl`). Call sites set **attributes only**.

This is the **v3 experiment**: implement types in Bazel Starlark instead of emitting raw `kt_jvm_*` from Kotlin string concat (F-041).

Cross-feature code goes **only through `api`** (see `Main.kt` and the impls).
An `impl` depending on another `impl` **fails analysis** in Starlark (`check_deps`) and is also rejected by the Kotlin `RestrictionGraph` in `bazel-adapter`.

---

## Prerequisites

- JDK 17+ (use the repo helper):
  ```bash
  source /path/to/forma/scripts/env-mac.sh
  java -version   # must be 17+
  ```
- bazelisk (recommended) or Bazel **8.7.0**
  ```bash
  brew install bazelisk
  ```
- Internet access on first run (rules_kotlin + toolchain downloads).

---

## Layout

```
bazel-sample/
├── .bazelversion          # 8.7.0
├── MODULE.bazel           # bzlmod: rules_kotlin 2.4.0 + bazel_skylib
├── WORKSPACE              # historical 7.x pin — not loaded on Bazel 8
├── .bazelrc
├── README.md
├── forma/                 # v3 Starlark types
│   ├── defs.bzl           # jvm_api / jvm_impl / jvm_library / jvm_util / jvm_binary
│   ├── matrix.bzl         # closed allow-list + check_deps
│   └── matrix_test.bzl    # bazel-skylib unittest
├── binary/
│   ├── BUILD.bazel        # jvm_binary(...) attrs only
│   └── src/main/kotlin/.../Main.kt
├── common/
│   ├── library/           # jvm_library(...)
│   └── util/              # jvm_util(...)
└── feature/
    ├── greeter/{api,impl}/...
    └── calculator/{api,impl}/...
```

---

## Build and run

```bash
cd bazel-sample

bazelisk build //...
bazelisk test //forma:forma_matrix_tests
bazelisk run //binary:binary
```

Expected output (or very similar):

```
Hello, World! (2 + 3 = 5)
Bazel sample (forma concepts) build + run successful.
```

Using plain `bazel` (if you have Bazel 8.7 installed and it matches):

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

1. **Starlark macros (v3, this tree):** `jvm_impl(deps = [other impl])` calls `check_deps` and `fail()`s at analysis with `Illegal Forma dependency: jvm.impl → jvm.impl`. Covered by `//forma:forma_matrix_tests` (injectable `_fail`). Scratch BUILD: `forma/illegal_impl_to_impl/BUILD.illegal.example`.
2. The `JvmBazelAdapter` (F-041 / v3 emit) uses `RestrictionGraph.isAllowed(...)` (from `tools.forma:core`) and **never emits** an illegal edge in generated `deps`.
3. `check()` on an illegal model reports violations.

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
- Call sites load `//forma:defs.bzl` and pass **attrs only** (`name`, `deps`, `visibility`, `main_class`)
- Type macros own `kt_jvm_*`, `tags = ["forma:type=..."]`, default `srcs` glob, and the matrix
- `main_class` on the binary
- Generator (`JvmBazelAdapter`) now emits the same macro call sites (v3)

See also the golden fragments in `bazel-adapter/examples/jvm-application-build/`.

---

## Limitations (experimental)

- No external Maven deps (no `maven_install` / bzlmod maven wiring).
- No tests (`kt_jvm_test` / test-util usage).
- No Android / `rules_android`.
- Toolchain is default (Java 11+ compatible with rules_kotlin).
- Not intended to replace `jvm-application/` Gradle build.
- Uses Bzlmod (`MODULE.bazel`) on Bazel **8.7.0**. `WORKSPACE` is leftover from the 7.4.1 experiment and is not loaded.

This sample exists to prove that the forma-core concepts (matrix, api contracts, composition root, no-impl-to-impl) are portable to Bazel with the mapping designed in F-040/F-041.

---

## Related

- [Bazel adapter design](../docs/BAZEL-ADAPTER.md)
- [JVM sample](../docs/JVM-SAMPLE.md)
- [forma-core API](../docs/forma-core-api.md)
- `bazel-adapter/` (generator + checker + tests)
