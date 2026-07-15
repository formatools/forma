# 05 — test-util (JVM)

**Goal:** Share test helpers via `testUtil` target consumed only in `testDependencies`.

## Features introduced
- `testUtil` target
- `impl(..., testDependencies = deps(...))`
- Test code lives in `src/test/...` of consumer; testUtil sources are main of their target but only visible to tests.

## Tree
```
common/test-util/   # test helpers (fake impls etc)
feature/greeter/impl/src/test/...  # uses the test util
```

## Build & run
```bash
source ../../../scripts/env-mac.sh
./gradlew build   # includes test compilation + run (0 tests in this teaching slice)
./gradlew :binary:run
```

## Rule
`testUtil` allowed from impl tests (and other testUtils).

## Next
JVM ladder complete. Move to Android progressive examples.
