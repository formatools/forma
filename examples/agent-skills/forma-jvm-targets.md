---
name: forma-jvm-targets
description: Pure JVM Forma DSL targets and progressive JVM ladder.
---

# JVM targets (agent skill)

Plugin: `tools.forma.jvm`. Imports: `tools.forma.jvm.api`, `.impl`, `.library`, `.util`, `.testUtil`, `.binary`.

| DSL | Suffix | Role | Step |
|-----|--------|------|------|
| `binary` | `binary` | composition root + `mainClass` | 01+ |
| `api` | `api` | contracts | 02+ |
| `impl` | `impl` | feature impl | 02+ |
| `library` | `library` | shared JVM | 03 |
| `util` | `util` | helpers | 03 |
| `testUtil` | `test-util` | test helpers | 05 |

## Matrix rules of thumb

- `impl` ↛ `impl`
- `api` → only `api`, `library`
- `library` → `util`, `test-util`
- `binary` may pull api + many impls + shared

## Skeleton

```kotlin
import tools.forma.jvm.binary
binary(
  packageName = "com.example.binary",
  mainClass = "com.example.binary.MainKt",
  dependencies = deps(target(":feature:x:api"), target(":feature:x:impl"))
)
```

Examples: `examples/jvm/01-hello-binary` … `05-test-util`. Full sample: `jvm-application/`.
