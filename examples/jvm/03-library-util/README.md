# 03 — library-util (JVM)

**Goal:** Introduce shared `library` (contracts/helpers) and `util` (utilities).

## Features introduced
- `library` target
- `util` target
- `api` may depend on `library`
- `impl` may depend on `api` + `util` + `library`

## Tree highlights
```
common/
  library/   # pure shared (Ids)
  util/      # string helpers
feature/greeter/{api,impl}
binary/
```

## Build & run
```bash
source ../../../scripts/env-mac.sh
./gradlew build
./gradlew :binary:run
```

## Key wiring
- impl → api, util, library (allowed per matrix)
- No impl→impl
- api → library (allowed)

## Next
04 demonstrates two features with impl→impl forbidden.
