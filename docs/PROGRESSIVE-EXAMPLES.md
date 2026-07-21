# Progressive examples (F-050)

Forma’s **feature-by-feature teaching ladder**. Start here after (or instead of) the
narrative tutorials when you want a **minimal buildable project per concept**.

| Audience | Start |
|----------|-------|
| Humans learning Android Forma | [examples/android/01-hello-apk](../examples/android/01-hello-apk) then climb |
| Humans learning JVM Forma | [examples/jvm/01-hello-binary](../examples/jvm/01-hello-binary) |
| Coding agents | [examples/agent-skills/](../examples/agent-skills/) |
| Full product reference | [SAMPLE-APP.md](SAMPLE-APP.md) / [JVM-SAMPLE.md](JVM-SAMPLE.md) |
| Narrative tutorials | [GETTING-STARTED.md](GETTING-STARTED.md) / [JVM-GETTING-STARTED.md](JVM-GETTING-STARTED.md) |

## Feature coverage matrix

| Feature | Android step | JVM step | Agent skill |
|---------|--------------|----------|-------------|
| Includer + composite plugins | 01 | 01 | forma-includer-settings |
| `androidProjectConfiguration` | 01 | — | forma-android-targets |
| `androidBinary` / `androidApp` | 01 | — | forma-android-targets |
| `androidRes` | 01, 03 | — | forma-android-targets |
| `binary` (JVM) | — | 01 | forma-jvm-targets |
| `api` / `impl` | 02 | 02 | platform skills + matrix |
| `viewBinding` | 03 | — | forma-android-targets |
| `library` / `util` | 04 | 03 | platform skills |
| `androidUtil` (not removed `androidLibrary`) | 04 | — | forma-android-targets |
| `widget` / `uiLibrary` | 05 | — | forma-android-targets |
| Compose + `composeWidget` | 06 | — | forma-compose |
| Multi-feature composition | 07 | 04 | forma-project-layout |
| `projectDependencies` catalogs (**house style**) | 08 | — | forma-deps-catalog |
| `testUtil` | 09 | 05 | platform skills |
| `androidTestUtil` | 09 | — | forma-android-targets |
| Type-owned target plugins (`navigationRes` Path B) | 10 | — | forma-target-plugins |
| Fleet check/generate/migrate (`tools.forma.core.fleet`) | docs | docs | forma-fleet-tooling |
| `androidNative` | skill/docs | — | forma-android-targets |
| Bazel adapter / sample | skill | skill | forma-bazel |

## Verify

See [examples/README.md](../examples/README.md).

## Principle alignment

Teaching ladder + agent skills were audited against root principles in
[PRINCIPLE-AUDIT.md](PRINCIPLE-AUDIT.md) (F-085). Do not reintroduce chain APIs,
dual happy paths, or removed `androidLibrary` into examples or skills.

## Relationship to gold standards

Progressive examples are **minimal**. Patterns for real apps (navigation, Dagger,
network) live in `application/` and are documented in SAMPLE-APP / GETTING-STARTED.
Sample-scale typed `build-dependencies/` catalogs are **advanced**, not a second
default (see [DEPS-CATALOG.md](DEPS-CATALOG.md)).
