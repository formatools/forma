# Forma progressive examples (F-050)

Hands-on ladders that introduce **every supported Forma feature** one step at a time.
Gold-standard product apps remain [`application/`](../application/) (Android) and
[`jvm-application/`](../jvm-application/) (JVM). These examples are **teaching-focused**.

## Curriculum

| Track | Path | Steps |
|-------|------|-------|
| JVM | [`jvm/`](jvm/) | 01 hello-binary → 05 test-util |
| Android | [`android/`](android/) | 01 hello-apk → 09 test-utils |
| Agent skills | [`agent-skills/`](agent-skills/) | SKILL docs for coding agents |
| User index | [`docs/PROGRESSIVE-EXAMPLES.md`](../docs/PROGRESSIVE-EXAMPLES.md) | narrative + feature coverage matrix |

### JVM (`tools.forma.jvm`)

| Step | Directory | Features |
|------|-----------|----------|
| 01 | `jvm/01-hello-binary` | `binary`, includer, composite plugins |
| 02 | `jvm/02-api-impl` | `api`, `impl`, composition-root listing |
| 03 | `jvm/03-library-util` | `library`, `util` |
| 04 | `jvm/04-multi-feature` | multi-feature; **impl ↛ impl** |
| 05 | `jvm/05-test-util` | `testUtil` + unit tests |

### Android (`tools.forma.android`)

| Step | Directory | Features |
|------|-----------|----------|
| 01 | `android/01-hello-apk` | `androidProjectConfiguration`, `androidBinary`, `androidApp`, `androidRes` |
| 02 | `android/02-feature-api-impl` | `api`, `impl` |
| 03 | `android/03-res-viewbinding` | feature `androidRes`, `viewBinding` |
| 04 | `android/04-shared-libs` | `library`, `util`, `androidUtil` (no deprecated `androidLibrary`) |
| 05 | `android/05-widget-ui` | `widget`, `uiLibrary` |
| 06 | `android/06-compose` | `compose` flags, `composeWidget` |
| 07 | `android/07-multi-feature` | two features at composition root |
| 08 | `android/08-deps-catalog` | `projectDependencies` / `library` / `bundle` / `plugin` |
| 09 | `android/09-test-utils` | `testUtil`, `androidTestUtil` (+ `androidNative` docs) |

Bazel is covered by agent skill + existing [`bazel-adapter/`](../bazel-adapter/) and [`bazel-sample/`](../bazel-sample/) (not duplicated here).

## Verify

```bash
source scripts/env-mac.sh

for d in examples/jvm/*/; do
  (cd "$d" && ./gradlew build --quiet && ./gradlew :binary:run --quiet)
done

printf 'sdk.dir=%s\n' "$ANDROID_HOME" > /tmp/forma-ex-local.properties
for d in examples/android/*/; do
  cp /tmp/forma-ex-local.properties "$d/local.properties"
  (cd "$d" && ./gradlew :binary:assembleDebug --quiet)
done
```

Each step has its own `README.md` with the delta vs the previous step.
