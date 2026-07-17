# `androidLibrary` removed (F-063)

`androidLibrary` (the generic AGP library DSL target with type id `android.library`
and `library` suffix) has been **hard-removed** in F-063.

It was a temporary escape hatch that worked against Forma’s core principle:
**keep the dependency graph flat and role-typed**.

## Why it existed and why it is gone

- Shared the `library` suffix with JVM `library()` (validator collision).
- Encouraged dumping unrelated concerns into one bucket.
- Weak teaching signal — people reached for the generic target instead of the
  role that matched the module.

## Replacements (use these)

| If the module is… | Use | Suffix |
|-------------------|-----|--------|
| Pure JVM shared code | `library` | `library` |
| Small Android helpers, **no** `res/` | `androidUtil` | `android-util` |
| Shared UI bases for `impl` / `widget` | `uiLibrary` | `ui-library` |
| Resources only (`res/` tree) | `androidRes` | `res` |
| Layout XML only | `viewBinding` | `viewbinding` |
| Custom View | `widget` | `widget` |
| Compose UI component | `composeWidget` | `compose-widget` |
| Feature implementation | `impl` | `impl` |
| Feature contracts | `api` | `api` |

## Historical migrations (F-061 / F-062)

| Old path | Old DSL | New path | New DSL |
|----------|---------|----------|---------|
| `core/di/library` | `androidLibrary` | `core/di/android-util` | `androidUtil` |
| `core/mvvm/library` | `androidLibrary` | `core/mvvm/ui-library` | `uiLibrary` |
| `core/navigation/library` | `androidLibrary` | `core/navigation/res` | `androidRes` |

Kotlin packages may retain historical `.library` segments; the **project name suffix**
is what the validators enforce.

## Lifecycle

| Ticket | Status |
|--------|--------|
| F-060 | Deprecated DSL + docs |
| F-061 | Migrated `application/` sample |
| F-062 | Migrated examples + agent skills |
| F-063 | Hard-removed DSL entry, type registration, restriction rules, and docs |

**`androidLibrary { }` no longer exists.** Use the role-specific targets above.
The internal `androidLibraryFeatureDefinition` helper (AGP `com.android.library`
wiring) remains for use by `impl`, `uiLibrary`, etc. — it is not a public DSL target.

See also [`DEPENDENCY-MATRIX.md`](DEPENDENCY-MATRIX.md) (collision note updated).
