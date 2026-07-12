# Forma dependency matrix (code truth)

Source of truth for **F-010**. Every rule below is taken from the live
`applyDependencies(validator = …)` / content validators in
`plugins/android/src/main/java/*.kt` (and
`tools/forma/android/validation/commonValidators.kt`).

When validators change, update **this file first**, then the README summary
matrix. Do not invent rules from aspirational docs.

Last verified: **2026-07-11** against `v2` tip + F-013 Compose support.

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

## Target templates (suffixes)

From `plugins/android/.../AndroidTargets.kt`:

| DSL entrypoint | Template object | Name suffix |
|----------------|-----------------|-------------|
| `androidBinary` | `BinaryTargetTemplate` | `binary` |
| `androidApp` | `ApplicationTargetTemplate` | `app` |
| `androidLibrary` | `LibraryTargetTemplate` | `library` |
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

**Collision note:** JVM `library` and Android `androidLibrary` share the same
suffix `library` / `LibraryTargetTemplate`. The DSL functions differ (Kotlin
JVM features vs AGP library features), but project-name validation cannot
tell them apart. Follow-up: F-020 (forma-core registry design).

README historically said `androidWidget` / `androidUtils` / `testUtils` /
`utils` / `androidTestUtils` — the **code** entrypoints are `widget`,
`androidUtil`, `testUtil`, `util`, `androidTestUtil`.

---

## Allowed project dependencies (consumer → allowed dependency suffixes)

Read as: **consumer DSL** may depend on project targets whose names match
these suffixes (or are unrestricted if `EmptyValidator`).

| Consumer DSL | Source file | Project-dep validator | Allowed dependency suffixes |
|--------------|-------------|------------------------|-----------------------------|
| `api` | `api.kt` | `validator(api, library)` | `api`, `library` |
| `impl` | `impl.kt` | `validator(api, android-util, test-util, util, library, ui-library, res, viewbinding, widget, compose-widget)` | `api`, `android-util`, `test-util`, `util`, `library`, `ui-library`, `res`, `viewbinding`, `widget`, `compose-widget` |
| `library` (JVM) | `library.kt` | `validator(util, test-util)` | `util`, `test-util` |
| `androidLibrary` | `androidLibrary.kt` | `validator(library, util, android-util, test-util, res, api)` | `library`, `util`, `android-util`, `test-util`, `res`, `api` |
| `uiLibrary` | `uiLibrary.kt` | `validator(widget, compose-widget, util, android-util, res)` | `widget`, `compose-widget`, `util`, `android-util`, `res` |
| `util` | `util.kt` | `validator(util, library)` | `util`, `library` |
| `androidUtil` | `androidUtil.kt` | `validator(android-util, test-util, res)` | `android-util`, `test-util`, `res` |
| `testUtil` | `testUtil.kt` | `validator(test-util, util)` | `test-util`, `util` |
| `androidTestUtil` | `androidTestUtil.kt` | `validator(android-test-util, test-util)` | `android-test-util`, `test-util` |
| `androidRes` | `androidRes.kt` | `validator(res, widget, compose-widget)` | `res`, `widget`, `compose-widget` |
| `widget` | `widget.kt` | `validator(ui-library, widget, compose-widget, util, android-util, res)` | `ui-library`, `widget`, `compose-widget`, `util`, `android-util`, `res` |
| `composeWidget` | `composeWidget.kt` | `validator(ui-library, compose-widget, widget, util, android-util, res)` | `ui-library`, `compose-widget`, `widget`, `util`, `android-util`, `res` |
| `viewBinding` | `viewBinding.kt` | `validator(api, widget, compose-widget, res, library, android-util)` | `api`, `widget`, `compose-widget`, `res`, `library`, `android-util` |
| `androidApp` | `androidApp.kt` | `validator(api, impl, library, util, android-util, test-util, res, viewbinding, widget, compose-widget, ui-library)` | `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library` |
| `androidBinary` | `androidBinary.kt` | `validator(app, api, impl, library, util, android-util, test-util, res, viewbinding, widget, compose-widget, ui-library)` | `app`, `api`, `impl`, `library`, `util`, `android-util`, `test-util`, `res`, `viewbinding`, `widget`, `compose-widget`, `ui-library` |
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
- **`androidLibrary` cannot depend on `impl` / widgets / viewbinding** —
  shared libs may use `api` contracts only (F-011).
- **`androidApp` / `androidBinary` cannot depend on other `binary`** —
  single APK composition root (F-011; `app` may not depend on `app`/`binary`).
- **Circular-ish UI graph is intentional while experimental:** `uiLibrary`
  may depend on `widget` / `compose-widget`, and `widget` / `compose-widget`
  may depend on `ui-library` and on each other so View + Compose can coexist
  (F-013 / GH #96).

### Compose flags (not project-dep rules)

Separate from suffix validation: `impl`, `androidLibrary`, `androidUtil`,
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
| **androidLibrary** | Y | — | Y | — | Y | Y | Y | — | Y | — | — | — | — | — | — |
| **uiLibrary** | — | — | — | — | Y | — | Y | — | Y | — | Y | Y | — | — | — |
| **util** | — | — | Y | — | Y | — | — | — | — | — | — | — | — | — | — |
| **androidUtil** | — | — | — | — | — | Y | Y | — | Y | — | — | — | — | — | — |
| **testUtil** | — | — | — | — | Y | Y | — | — | — | — | — | — | — | — | — |
| **androidTestUtil** | — | — | — | — | — | Y | — | Y | — | — | — | — | — | — | — |
| **androidRes** | — | — | — | — | — | — | — | — | Y | — | Y | Y | — | — | — |
| **widget** | — | — | — | Y | Y | — | Y | — | Y | — | Y | Y | — | — | — |
| **composeWidget** | — | — | — | Y | Y | — | Y | — | Y | — | Y | Y | — | — | — |
| **viewBinding** | Y | — | Y | — | — | — | Y | — | Y | — | Y | Y | — | — | — |
| **androidApp** | Y | Y | Y | Y | Y | Y | Y | — | Y | Y | Y | Y | — | — | — |
| **androidBinary** | Y | Y | Y | Y | Y | Y | Y | — | Y | Y | Y | Y | Y | — | — |
| **androidNative** | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a | n/a |

Self-deps: a type may depend on itself only if its own suffix is in its
allowed list (e.g. `api`→`api` **Y**, `impl`→`impl` **—**, `widget`→`widget`
**Y**).

---

## README matrix vs code (divergences)

The historical README table used columns as *consumers* and rows as
*dependencies*, mixed aspirational edges, and omitted several types
(`uiLibrary`, `viewBinding`, `library` JVM vs `androidLibrary`, `native`).

Notable mismatches fixed by treating **this document** as truth:

| Topic | Old README implication | Live code |
|-------|------------------------|-----------|
| `androidLibrary` project deps | selective ✅/❌ grid | **restricted** (no `impl`/widget/viewbinding) after F-011 |
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
(forma-core type registry / shared `library` suffix).
