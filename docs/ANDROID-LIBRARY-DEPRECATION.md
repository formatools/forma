# Deprecating `androidLibrary` (F-060+)

## Why

`androidLibrary` is a **generic AGP library** target. It was a temporary way to
host “shared Android code that isn’t a feature `impl`.” That works against
Forma’s goal: **keep structure flat and role-typed**.

Problems:

1. **Suffix collision** with JVM `library` (`library` name suffix / historical
   `LibraryTargetTemplate`) — validators resolve deps by suffix and cannot tell
   JVM vs Android library modules apart reliably.
2. **Mixed concerns** — sample historically stuffed DI scopes, MVVM/UI bases, and
   navigation XML into the same target kind.
3. **Weak teaching signal** — newcomers reach for `androidLibrary` instead of the
   target that matches the module’s job.

## Replacement map

| If the module is… | Use instead | Suffix |
|-------------------|-------------|--------|
| Pure JVM shared code | `library` | `library` |
| Small Android helpers, **no** `res/` | `androidUtil` | `android-util` |
| Shared UI bases for `impl` / `widget` | `uiLibrary` | `ui-library` |
| Resources only (`res/` tree) | `androidRes` | `res` |
| Layout XML only | `viewBinding` | `viewbinding` |
| Custom View | `widget` | `widget` |
| Compose UI component | `composeWidget` | `compose-widget` |
| Feature implementation | `impl` | `impl` |
| Feature contracts | `api` | `api` |

## Sample migration (F-061)

| Old path | Old DSL | New path | New DSL |
|----------|---------|----------|---------|
| `core/di/library` | `androidLibrary` | `core/di/android-util` | `androidUtil` |
| `core/mvvm/library` | `androidLibrary` | `core/mvvm/ui-library` | `uiLibrary` |
| `core/navigation/library` | `androidLibrary` | `core/navigation/res` | `androidRes` |

Kotlin **packages** may keep historical `.library` segments to limit churn;
**project path / suffix** is what Forma validates.

## Matrix tweaks paired with migration

- `viewBinding` may depend on `ui-library` (shared UI bases without generic library).
- `androidUtil` may depend on JVM `library` (Android helpers wrapping pure JVM code).

## Lifecycle

| Ticket | Intent |
|--------|--------|
| **F-060** | `@Deprecated` on DSL + docs/vision |
| **F-061** | `application/` sample migrated |
| **F-062** | Progressive examples + agent skills teach replacements |
| **F-063** | Hard-remove target type / registry entry when nothing in-repo calls it |

Until F-063, `androidLibrary { }` still configures modules but should not appear
in new code or tutorials.
