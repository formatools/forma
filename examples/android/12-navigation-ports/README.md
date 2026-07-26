# 12 — navigation-ports (Android)

**Goal:** Presentation-layer navigation ports (F-102 design / **F-112**). Feature
`impl` stays free of Jetpack Navigation; the composition root owns `NavController`
+ Safe Args. Graphs stay on Path B **`navigationRes`**.

## Features introduced

- Layer A: `Navigator` + sealed `AppDestination` in `navigation-api` (**zero**
  `androidx.navigation`)
- Feature `impl` emits destinations / `back()` only (no `findNavController`, no
  `*Directions`, no graph `R.id`)
- Layer B: existing **`navigationRes`** (type-owned safe-args) — same Path B as
  example **10**
- Adapter at **`root-app`**: `JetpackNavigator` maps ports → Safe Args

## Mental model

```text
feature impl ──► navigation-api (ports)     // no Nav library
root-app     ──► navigation-api
             ──► navigationRes + androidx.navigation + Safe Args Directions
             ──► feature impls
androidBinary ──► root-app + features
```

| Module | Role |
|--------|------|
| `navigation-api` | `Navigator`, `AppDestination`, `NavigatorProvider` |
| `feature/*/impl` + `viewbinding` | UI only; calls `navigator.navigate` / `back` |
| `navigation/res` | XML graph + `navigationRes` (safe-args on the **type**) |
| `root-app` | Host activity + `JetpackNavigator` (only Nav/Safe Args consumer) |
| `root-res` | Host layout with `NavHostFragment` |
| `binary` | APK composition root |

## Forbidden (same as design doc)

- `.withPlugin` / free-form plugin lists / restoring `androidLibrary`
- `impl` → `impl` for nav wiring
- Teaching raw `findNavController` / `*Directions` in feature `impl` as the happy path
- Forma engine router DSL / embedding Cicerone in plugins

## Build

```bash
source ../../../scripts/env-mac.sh
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :binary:assembleDebug
# Prove type-owned safe-args still applied on the graph module:
./gradlew :navigation-res:tasks --all | grep -i safeargs
```

## Docs

- Design: [`docs/NAVIGATION-ABSTRACTION.md`](../../../docs/NAVIGATION-ABSTRACTION.md)
- Path B plugins: [`docs/TARGET-PLUGINS.md`](../../../docs/TARGET-PLUGINS.md)
- Ladder: [`docs/PROGRESSIVE-EXAMPLES.md`](../../../docs/PROGRESSIVE-EXAMPLES.md)
- Sample migration (gold app): **F-111** — same ports pattern at Marvel scale

## Next

Apply the same ports + root adapter pattern to `application/` (**F-111**), then
hybrid-target teaching (**F-103**).
