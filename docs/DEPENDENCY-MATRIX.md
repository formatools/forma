# Forma dependency matrix (code truth)

Source of truth for **F-010**. Every rule below is taken from the live
`applyDependencies(validator = …)` / content validators in
`plugins/android/src/main/java/*.kt` (and
`tools/forma/android/validation/commonValidators.kt`).

When validators change, update **this file first**, then the README summary
matrix. Do not invent rules from aspirational docs.

Last verified: **2026-07-25** against `v2` + F-108 Android/JVM → KMP consumer edges
(registries in `AndroidTargetRegistry` / `JvmTargetRegistry`; KMP internal matrix in
[`KMP-TARGETS.md`](KMP-TARGETS.md) §6.1).

---

## How validation works

1. **Name / type of the consumer** — each DSL entry calls
   `target.validate(SomeTargetTemplate)`. The project name must equal the
   template `suffix` or end with `-$suffix` (see
   `plugins/validation/.../Validator.kt`).
2. **Allowed project dependencies** — `applyDependencies` runs a `Validator`
   on every **project** dependency. `validator(A, B, …)` accepts only
   dependency projects whose names match those templates’ suffixes.
   `EmptyValidator` accepts **any** project dependency (no type check).
3. **Content layout** — some targets call `disallowResources()`,
   `onlyAllowResources()`, or `onlyAllowLayouts()` before configuration.
4. **External / catalog deps** — named library deps are not filtered by the
   project-suffix validators; only `project(...)` edges are.

Validation is **suffix-based**, not Gradle configuration-based. A project
named `feature-home-api` is treated as type `api` because of the `-api`
suffix (`ApiTargetTemplate("api")`).

---

## Dependency validation exclusions (F-090 / GH #97)

Sometimes a monorepo vendors a **forked third-party tree** (ExoPlayer-class
cases) whose module names do **not** follow Forma suffixes. Those modules
must be dependable from Forma targets without weakening the default matrix
for first-party code.

**One global configuration path only** — set once on root
`androidProjectConfiguration`:

```kotlin
buildscript {
    androidProjectConfiguration(
        project = rootProject,
        // ...
        dependencyValidationExclusions = setOf(
            ":third-party:exoplayer:library-core", // Gradle project path
            "forked-media-engine",                  // or exact project name
        ),
    )
}
```

| Skips | Does **not** skip |
|-------|-------------------|
| Project-dependency **type/suffix** validation when the **dependency** project is listed | Self-type validation on Forma DSL modules (`target.validate(...)`) |
| | The default allow-list matrix for any **non-listed** dependency |
| | Content layout rules (`disallowResources`, etc.) |

**Matching:** exact Gradle project **path** (e.g. `:third-party:exoplayer:library-core`)
and/or exact project **name**. No prefix/glob matching.

**When to use:** forked-in libraries that live in the composite build but are
not Forma targets.

**When not to use:** never to bypass `impl`↛`impl` (or any other matrix edge)
between first-party Forma modules. Fix the graph or re-slice targets instead.
There is **no** per-call-site `skipValidation` flag.

Implementation: `FormaSettingsStore.isExcludedFromDependencyValidation` gated
inside `applyDependencies` `projectAction`. Default exclusions = empty (strict
matrix unchanged). See [`PROJECT-CONFIGURATION.md`](PROJECT-CONFIGURATION.md).

---

## Target templates (suffixes)

From `plugins/android/.../AndroidTargets.kt`:

| DSL entrypoint | Template object | Name suffix |
|----------------|-----------------|-------------|
| `androidBinary` | `BinaryTargetTemplate` | `binary` |
| `androidApp` | `ApplicationTargetTemplate` | `app` |
| `library` (JVM) | `LibraryTargetTemplate` | `library` |
| `uiLibrary` | `UiLibraryTargetTemplate` | `ui-library` |
| `androidNative` | `NativeTarget` | `native` |
| `util` | `UtilTargetTemplate` | `util` |
| `testUtil` | `TestUtilTargetTemplate` | `test-util` |
| `androidTestUtil` | `AndroidTestUtilTargetTemplate` | `android-test-util` |
| `androidUtil` | `AndroidUtilTargetTemplate` | `android-util` |
| `viewBinding` | `ViewBindingTargetTemplate` | `viewbinding` |
| `androidRes` | `ResourcesTargetTemplate` | `res` |
| `api` | `ApiTargetTemplate` | `api` |
| `impl` | `ImplTargetTemplate` | `impl` |
| `widget` | `WidgetTargetTemplate` | `widget` |
| `composeWidget` | `ComposeWidgetTargetTemplate` | `compose-widget` |

**Collision note:** Only JVM `library` uses the `library` suffix (F-063).
Historical `androidLibrary` (also using `library` suffix via `LibraryTargetTemplate`)
was removed after F-060–F-062 migration to role-specific targets (`androidUtil`,
`uiLibrary`, `androidRes`, `viewBinding`, `impl`). Validators still match by
suffix, so `*-library` JVM modules are accepted where `library` is allowed.
See [`forma-core-api.md`](forma-core-api.md) §7 for the engine design.

README historically said `androidWidget` / `androidUtils` / `testUtils` /
`utils` / `androidTestUtils` — the **code** entrypoints are `widget`,
`androidUtil`, `testUtil`, `util`, `androidTestUtil`.

---

## Allowed project dependencies (consumer → allowed dependency suffixes)

Read as: **consumer DSL** may depend on project targets whose names match
these suffixes (or are unrestricted if `EmptyValidator`).

| Consumer DSL | Source file | Project-dep validator | Allowed dependency suffixes |
|--------------|-------------|------------------------|-----------------------------|
| `api` | `api.kt` | registry: api, library, **kmp-api** | `api`, `library`, `kmp-api` |
| `impl` | `impl.kt` | registry + **kmp-api, kmp-library, kmp-util** | `api`, `android-util`, `test-util`, `util`, `library`, `ui-library`, `res`, `viewbinding`, `widget`, `compose-widget`, `kmp-api`, `kmp-library`, `kmp-util` |
| `library` (JVM) | `library.kt` | `validator(util, test-util)` | `util`, `test-util` |
| `uiLibrary` | `uiLibrary.kt` | `validator(widget, compose-widget, util, android-util, res)` | `widget`, `compose-widget`, `util`, `android-util`, `res` |
| `util` | `util.kt` | `validator(util, library)` | `util`, `library` |
| `androidUtil` | `androidUtil.kt` | registry + **kmp-library, kmp-util** | `android-util`, `test-util`, `res`, `library`, `kmp-library`, `kmp-util` |
| `testUtil` | `testUtil.kt` | `validator(test-util, util)` | `test-util`, `util` |
| `androidTestUtil` | `androidTestUtil.kt` | `validator(android-test-util, test-util)` | `android-test-util`, `test-util` |
| `androidRes` | `androidRes.kt` | `validator(res, widget, compose-widget)` | `res`, `widget`, `compose-widget` |
| `widget` | `widget.kt` | `validator(ui-library, widget, compose-widget, util, android-util, res)` | `ui-library`, `widget`, `compose-widget`, `util`, `android-util`, `res` |
| `composeWidget` | `composeWidget.kt` | `validator(ui-library, compose-widget, widget, util, android-util, res)` | `ui-library`, `compose-widget`, `widget`, `util`, `android-util`, `res` |
| `viewBinding` | `viewBinding.kt` | `validator(api, widget, compose-widget, res, library, android-util, ui-library)` | `api`, `widget`, `compose-widget`, `res`, `library`, `android-util`, `ui-library` |
| `androidApp` | `androidApp.kt` | registry + **kmp-api, kmp-library, kmp-util** | `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library`, `kmp-api`, `kmp-library`, `kmp-util` |
| `androidBinary` | `androidBinary.kt` | registry + **kmp-api, kmp-library, kmp-util** | `app`, `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library`, `kmp-api`, `kmp-library`, `kmp-util` |
| `androidNative` | `androidNative.kt` | *(no `applyDependencies`)* | *no project-dep validation* |

### Explicit non-edges (important product rules)

These follow from the table (not from separate deny-lists):

- **`impl` cannot depend on `impl`** — only listed suffixes; Dagger-style feature
  boundaries (implementations compose only at app/binary).
- **`api` cannot depend on `impl` / widgets / res / viewbinding** — JVM API
  surface only (`api` + `library`).
- **`library` (JVM) cannot depend on `api` / `impl`** — comment in code:
  “Can't depend on api\impl”.
- **`util` cannot depend on `api` / `impl`** — same intent as library.
- **`viewBinding` may depend on `ui-library`** — shared UI bases without generic library (F-061).
- **`androidUtil` may depend on JVM `library`** — Android helpers wrapping pure JVM code (F-061).
- **`androidApp` / `androidBinary` cannot depend on other `binary`** —
  single APK composition root (F-011; `app` may not depend on `app`/`binary`).
- **Circular-ish UI graph is intentional while experimental:** `uiLibrary`
  may depend on `widget` / `compose-widget`, and `widget` / `compose-widget`
  may depend on `ui-library` and on each other so View + Compose can coexist
  (F-013 / GH #96).
- **No KMP → Android / JVM** — `KmpTargetRegistry` does not list android/jvm types
  (F-105/F-108 cycle rule). Direction is Android/JVM → KMP only.
- **No kmp edges on UI leaves** — widget / composeWidget / res / viewBinding /
  uiLibrary / androidTestUtil do not gain kmp.* (design §6.2).
- **No `kmp-test-util` on Android/JVM consumers in v1** — reserved for KMP test
  helpers; may land later with an explicit ticket.

### Compose flags (not project-dep rules)

Separate from suffix validation: `impl`, `androidUtil`,
`androidApp`, `uiLibrary`, and `androidBinary` accept `compose: Boolean`
(default = project `androidProjectConfiguration(compose=…)`).
`composeWidget` **always** enables Compose. See [`COMPOSE.md`](COMPOSE.md).

---

## Content rules (src layout)

| Consumer DSL | Helper | Rule |
|--------------|--------|------|
| `api` | `disallowResources()` | no `res/` under `src/main` |
| `util` | `disallowResources()` | no `res/` under `src/main` |
| `androidUtil` | `disallowResources()` | no `res/` under `src/main` |
| `testUtil` | `disallowResources()` | no `res/` under `src/main` |
| `androidApp` | `disallowResources()` | no `res/` under `src/main` |
| `androidBinary` | `disallowResources()` | no `res/` under `src/main` |
| `androidNative` | `disallowResources()` | no `res/` under `src/main` |
| `androidRes` | `onlyAllowResources()` | **only** `res/` directory under `src/main` |
| `viewBinding` | `onlyAllowLayouts()` | under `src/main/res`, only `layout*` folders |
| others | — | no extra content validator in current code |

Helpers live in
`plugins/android/src/main/java/tools/forma/android/validation/commonValidators.kt`.

---

## Full matrix (consumer rows × dependency columns)

Legend:

- **Y** — allowed by non-empty `validator(...)` list
- **\*** — `EmptyValidator` / no project-dep check (anything goes)
- **—** — not allowed by the live validator list
- **n/a** — `androidNative` has no `applyDependencies`

Rows = **consumer**. Columns = **dependency type (suffix)**.

| Consumer ↓ \ Dep → | api | impl | library | ui-library | util | test-util | android-util | android-test-util | res | viewbinding | widget | compose-widget | app | binary | native |
|--------------------|-----|------|---------|------------|------|-----------|--------------|-------------------|-----|-------------|--------|----------------|-----|--------|--------|
| **api** | Y | — | Y | — | — | — | — | — | — | — | — | — | — | — | — |
| **impl** | Y | — | Y | Y | Y | Y | Y | — | Y | Y | Y | Y | — | — | — |
| **library** (JVM) | — | — | — | — | Y | Y | — | — | — | — | — | — | — | — | — |
| **uiLibrary** | — | — | — | — | Y | — | Y | — | Y | — | Y | Y | — | — | — |
| **util** | — | — | Y | — | Y | — | — | — | — | — | — | — | — | — | — |
| **androidUtil** | — | — | Y | — | — | Y | Y | — | Y | — | — | — | — | — | — |
| **testUtil** | — | — | — | — | Y | Y | — | — | — | — | — | — | — | — | — |
| **androidTestUtil** | — | — | — | — | — | Y | — | Y | — | — | — | — | — | — | — |
| **androidRes** | — | — | — | — | — | — | — | — | Y | — | Y | Y | — | — | — |
| **widget** | — | — | — | Y | Y | — | Y | — | Y | — | Y | Y | — | — | — |
| **composeWidget** | — | — | — | Y | Y | — | Y | — | Y | — | Y | Y | — | — | — |
| **viewBinding** | Y | — | Y | Y | — | — | Y | — | Y | — | Y | Y | — | — | — |
| **androidApp** | Y | Y | Y | Y | Y | Y | Y | — | Y | Y | Y | Y | — | — | — |
| **androidBinary** | Y | Y | Y | Y | Y | Y | Y | — | Y | Y | Y | Y | Y | — | — |
| **androidNative** | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a |

Self-deps: a type may depend on itself only if its own suffix is in its
allowed list (e.g. `api`→`api` **Y**, `impl`→`impl` **—**, `widget`→`widget`
**Y**).

---

## Android / JVM → KMP consumer edges (F-108)

Code truth: `AndroidTargetRegistry` + `JvmTargetRegistry` import
`KmpTargetTypes` (`implementation(project(":kmp"))`). Internal KMP→KMP matrix:
[`KMP-TARGETS.md`](KMP-TARGETS.md) §6.1 (`KmpTargetRegistry`).

| Consumer | May depend on KMP (type ids) |
|----------|------------------------------|
| `android.api` | `kmp.api` only |
| `android.impl` | `kmp.api`, `kmp.library`, `kmp.util` |
| `android.android-util` | `kmp.library`, `kmp.util` |
| `android.app` / `android.binary` | `kmp.api`, `kmp.library`, `kmp.util` |
| `jvm.api` | `kmp.api` |
| `jvm.impl` / `jvm.binary` | `kmp.api`, `kmp.library`, `kmp.util` |
| `jvm.library` / `jvm.util` | `kmp.library`, `kmp.util` |

**Not listed (no kmp edges):** Android UI leaves + `androidTestUtil` / `testUtil`;
JVM `test-util`. **`kmp-test-util`** is not on any Android/JVM consumer in v1.

**Plugin graph:** `:android` and `:jvm` may depend on `:kmp`. `:kmp` must **not**
`implementation(project(":android"))` or `:jvm` (cycle rule).

**Suffix overlap (known limitation):** `SuffixNameMatcher` matches
`project.endsWith("-$suffix")`. Names like `shared-kmp-library` also end with
`-library`, so a consumer that already allows `library` accepts them via the
unprefixed suffix even when the **graph** denies `kmp.library` (e.g. `api` →
`kmp.library` is `isAllowed=false`, but validator may still accept the name via
`library`). Authoritative design checks use restriction-graph type pairs; a
longest-suffix matcher is out of scope for F-108.

Compact KMP columns (Android consumers that gain edges):

| Consumer ↓ \ KMP → | kmp-api | kmp-library | kmp-util | kmp-test-util |
|--------------------|---------|-------------|----------|---------------|
| **api** | Y | — | — | — |
| **impl** | Y | Y | Y | — |
| **androidUtil** | — | Y | Y | — |
| **androidApp** | Y | Y | Y | — |
| **androidBinary** | Y | Y | Y | — |
| other Android rows | — | — | — | — |

| Consumer ↓ \ KMP → | kmp-api | kmp-library | kmp-util | kmp-test-util |
|--------------------|---------|-------------|----------|---------------|
| **jvm.api** | Y | — | — | — |
| **jvm.impl** | Y | Y | Y | — |
| **jvm.binary** | Y | Y | Y | — |
| **jvm.library** | — | Y | Y | — |
| **jvm.util** | — | Y | Y | — |
| **jvm.test-util** | — | — | — | — |

---

## README matrix vs code (divergences)

The historical README table used columns as *consumers* and rows as
*dependencies*, mixed aspirational edges, and omitted several types
(`uiLibrary`, `viewBinding`, `library` JVM, `native`).

Notable mismatches fixed by treating **this document** as truth:

| Topic | Old README implication | Live code |
|-------|------------------------|-----------|
| `androidApp` / `androidBinary` project deps | selective grid | **restricted** composition-root lists after F-011 |
| `impl` → `impl` | ❌ (agrees) | not in allowed list |
| `api` → `library` | not clearly shown | **allowed** |
| `impl` → `ui-library` / `viewbinding` / `widget` / `res` | partially missing | **allowed** |
| `widget` → `ui-library` | not shown | **allowed** |
| `viewBinding` | missing from matrix | full row above |
| `uiLibrary` | missing / confused with library | full row above |
| DSL names | `androidWidget`, `utils`, … | `widget`, `util`, … |

Tightening unrestricted entry targets landed in **F-011**.

---

## Sample app check (sanity)

`application/` multi-feature layout uses the pattern:

- `feature/*/api` → JVM `api`
- `feature/*/impl` → Android `impl` + viewbinding/res/widget/core libraries
- `binary` → `androidBinary` with explicit deps on root-app + feature api/impl
  (legal under composition-root allowlist; F-011)

**Gold-standard guide:** [`SAMPLE-APP.md`](SAMPLE-APP.md) (F-014). See also
`docs/ARCHITECTURE.md` §2.2 and §3 for the module map.

---

## Maintenance checklist

1. Change a `validator(...)` list or content helper in `plugins/android`.
2. Update the tables in **this file**.
3. Refresh the compact README matrix (link here as canonical).
4. Note the change in `docs/PROGRESS.md` / ticket notes when user-facing.

Related tickets: **F-013** (Compose), **F-012** (deps catalog UX), **F-020**
(forma-core type registry / shared `library` suffix), **F-108** (Android/JVM →
KMP consumer edges).
