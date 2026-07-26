# Google first-party libraries — coverage matrix (F-114)

Forma does **not** re-implement AndroidX/Firebase. It must still **teach** how those
stacks enter a Forma graph: catalog clusters, type-owned plugins, and **call-site
usage** (not classpath-only comments).

**Gold product sample:** [`application/`](../application/) (typed catalogs in
`build-dependencies/` + house-style `projectDependencies` for Room/Coil/Timber).

**Progressive ladder:** [`examples/android/`](../examples/android/) + this matrix.

## Status legend

| Status | Meaning |
|--------|---------|
| **used** | Declared **and** exercised in Kotlin/XML (imports / DI / UI) |
| **wired** | Dep/plugin on a module that builds, but no app-level call (rare) |
| **catalog-only** | In BOM/catalog or buildscript classpath; **no** apply/usage |
| **out** | Not in sample/examples yet (document, do not invent tickets lightly) |

## Architecture components & Jetpack (AndroidX / Google)

| Stack | Catalog / coords | Usage proof | Notes |
|-------|------------------|-------------|-------|
| **Lifecycle / ViewModel / LiveData** | `androidx.viewmodel` (+ private lifecycle graph) | `application/core/mvvm/ui-library`, feature VMs (`HomeViewModel`, list/favorite) | Sample architecture core |
| **Fragment / Activity** | via `androidx.appcompat` / `fragment` | `SampleMainActivity`, feature Fragments | |
| **AppCompat** | `androidx.appcompat` | root-app + feature viewbindings | |
| **Navigation (Jetpack)** | `androidx.navigation` + safe-args plugin | `core/navigation/res` (`navigationRes`), HomeFragment | Path B type-owned safe-args; design ports → F-111/F-112 |
| **Paging 2.x** | `androidx.paging` | characters list `PageKeyedDataSource` / adapters | Stays on **2.x** APIs until deliberate 3.x rewrite |
| **RecyclerView** | `androidx.recyclerview` | list/favorite UI + `common/recyclerview/widget` | |
| **Room** | `libs.bundles.room` + KSP `room-compiler` | `feature/characters/favorite/impl` (`MarvelDatabase`, DAO, entity) | House-style catalog (not typed `androidx.room` yet) |
| **Compose** | `androidx.compose` | `common/greeting/compose-widget` + example `06-compose` | F-013 |
| **Core KTX** | `androidx.core_ktx` | root-app deps | |
| **ConstraintLayout / Material** | via `google.material` cluster | layouts + Material components | Material pulls cardview/constraintlayout/recyclerview |
| **SwipeRefreshLayout** | `androidx.swiperefreshlayout` | transitive via material/legacy_ui | |

## Google libraries (non-AndroidX)

| Stack | Catalog / coords | Usage proof | Notes |
|-------|------------------|-------------|-------|
| **Material Design** | `google.material` | feature layouts / themes | |
| **Dagger** | `google.dagger` + KSP | root + feature components/modules | Compiler plugin via KSP, not type-owned Gradle plugin |
| **Play Core** | `google.play` | `SampleApp` extends `SplitCompatApplication` | Dynamic feature readiness shell |
| **Gson** | `google.gson` | Retrofit `GsonConverterFactory` in network module | |
| **Firebase BOM** | `google.firebase` | **was catalog-only** | |
| **Firebase Crashlytics + Analytics** | BOM + SDKs + GMS/Crashlytics plugins | **`examples/android/14-google-firebase`** — `FirebaseApp` / Crashlytics / Analytics in `Application` + Activity; Path A `firebaseBinary` | Dummy `google-services.json` for CI; sample `application/binary` still classpath TODO |
| **javax/jakarta.inject** | `google.inject` / `jakartaInject` | Dagger graph | |

## Progressive examples (Google-facing)

| Step | Google stacks shown |
|------|---------------------|
| 01–05 | AppCompat/Material/core via plain GAVs as needed |
| 06 | Jetpack **Compose** |
| 08 | Catalog / bundles pattern (mirrors Room/Coil house style) |
| 09 | AndroidX Test (`androidx.test.ext:junit`, runner) |
| 10 | Navigation **safe-args** plugin (type-owned) |
| 11 | Metro (not Google) |
| 13 | NDK (not Google) |
| **14** | **Firebase** Crashlytics + Analytics + GMS plugin |

## Explicitly out of gold sample (for now)

| Stack | Why |
|-------|-----|
| WorkManager | No sample job; add when a ticket needs background work |
| DataStore / Preferences | Sample uses simpler stores; add with a feature need |
| CameraX / Media3 / Maps / Ads | Product-specific; not Forma meta-build teaching defaults |
| Hilt | Sample teaches **Dagger + KSP**; Metro is alternate DI example |
| Firebase Auth / Firestore / Remote Config / … | Crashlytics+Analytics establish the **plugin + BOM** pattern; expand per product need |

## Forma rules when adding Google stacks

1. **Structure over shopping** — prefer type-owned plugins (`navigationRes`, `firebaseBinary`) over `.withPlugin` / free-form ids.
2. **House style external deps** — `projectDependencies` → `libs.*` for new teaching examples; sample may keep typed `androidx.*` / `google.*` clusters at monorepo scale ([DEPS-CATALOG.md](DEPS-CATALOG.md)).
3. **Usage required** — a catalog entry or buildscript classpath line is **not** coverage. Need a call site (Kotlin/XML) or an applied type-owned plugin with SDK API use.
4. **BOM + artifacts** — `PlatformDependency + NamedDependency` must preserve platforms (fixed F-114). Prefer BOM for Firebase version alignment.

## Related docs

- [SAMPLE-APP.md](SAMPLE-APP.md) — multi-feature gold structure
- [TARGET-PLUGINS.md](TARGET-PLUGINS.md) — Path A/B, firebaseBinary sketch
- [COMPOSE.md](COMPOSE.md) — Compose flags / composeWidget
- [DEPS-CATALOG.md](DEPS-CATALOG.md) — catalogs, Room bundle example
- [PROGRESSIVE-EXAMPLES.md](PROGRESSIVE-EXAMPLES.md) — ladder matrix
