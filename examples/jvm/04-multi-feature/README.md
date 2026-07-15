# 04 — multi-feature (JVM)

**Goal:** Two independent features; binary is single composition root. Proves `impl` does not (and may not) depend on other `impl`.

## Features introduced
- Second feature slice
- Explicit composition at `binary` of multiple (api+impl) pairs
- Dependency matrix enforcement: impl ↛ impl (would fail validation at config time)

## Tree
```
feature/
  greeter/{api,impl}
  calc/{api,impl}
binary/
```

## Build & run
```bash
source ../../../scripts/env-mac.sh
./gradlew build
./gradlew :binary:run
```

## Rule reminder
From DEPENDENCY-MATRIX: `impl` validator allows only `api`, `library`, `util`, `test-util` (JVM). No `impl`.

## Next
05 adds `testUtil`.
