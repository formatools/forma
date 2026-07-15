---
name: forma-bazel
description: Bazel adapter generate/check and sample workspace pointers.
---

# Bazel (agent skill)

Not part of the progressive Gradle ladders. Use:

| Artifact | Path |
|----------|------|
| Design | `docs/BAZEL-ADAPTER.md` |
| Generate/check spike | `bazel-adapter/` (`./gradlew test`, `runSample`) |
| Runnable sample | `bazel-sample/` (`bazelisk build //...`, `run //binary:binary`) |

Concepts: target types → `kt_jvm_*`, restriction graph → visibility, tags `forma:type=…`, **impl ↛ impl**.
