package tools.forma.config

/**
 * Project-global defaults for AGP [com.android.build.api.dsl.BuildFeatures] flags (F-091 / GH #88).
 *
 * All flags default to **false** so AGP features are opt-in at the Forma project level.
 * Configure once via root `androidProjectConfiguration(buildFeatures = FormaBuildFeatures(...))`.
 *
 * **Compose is not here.** The project-wide Compose default remains the top-level
 * `compose` / `composeCompilerVersion` parameters on `androidProjectConfiguration`
 * (one happy path — see [AndroidProjectSettings.compose]).
 *
 * **Layer ownership**
 * - Always-on for a role → target type (`viewBinding` target ⇒ viewBinding on; `composeWidget` ⇒ compose on)
 * - Project-wide default → this class + top-level `compose`
 * - Rare per-module override → minimal Boolean attrs only (`compose` on several DSLs; `viewBinding` on `impl`)
 *
 * Do **not** add a call-site Boolean for every flag — that would reintroduce fat call sites.
 * Rare fleet-wide needs (e.g. `buildConfig`) belong here; rare per-module needs should become
 * a dedicated derived type or a single documented attr later, not free-form shopping.
 *
 * **dataBinding** often pairs with view binding in app code; Forma does not auto-enable
 * one when the other is on — set both explicitly if both are required.
 */
data class FormaBuildFeatures(
    val aidl: Boolean = false,
    val buildConfig: Boolean = false,
    val dataBinding: Boolean = false,
    val prefab: Boolean = false,
    val resValues: Boolean = false,
    val shaders: Boolean = false,
    /**
     * Project-wide default for the `viewBinding` Boolean on `impl`.
     * The dedicated `viewBinding` target type always enables view binding regardless of this flag.
     */
    val viewBinding: Boolean = false,
)

/**
 * Final AGP BuildFeatures values after merging project defaults with type/call-site overrides.
 *
 * Pure data — safe to unit-test without AGP. Applied to the public AGP DSL by the Android plugin.
 */
data class ResolvedFormaBuildFeatures(
    val aidl: Boolean,
    val buildConfig: Boolean,
    val compose: Boolean,
    val dataBinding: Boolean,
    val prefab: Boolean,
    val resValues: Boolean,
    val shaders: Boolean,
    val viewBinding: Boolean,
)

/**
 * Merge project-global [FormaBuildFeatures] with per-target overrides.
 *
 * - Project defaults supply fleet-wide flags (`aidl`, `buildConfig`, …).
 * - [viewBinding] and [compose] are the **final** values already resolved by the target type
 *   or call-site attr (type-owned targets pass `true`; `impl` may default from
 *   [FormaBuildFeatures.viewBinding]; compose DSLs default from [AndroidProjectSettings.compose]).
 * - Type/call-site overrides win for viewBinding and compose; other flags stay project-global only.
 */
fun FormaBuildFeatures.resolveWith(
    viewBinding: Boolean,
    compose: Boolean,
): ResolvedFormaBuildFeatures =
    ResolvedFormaBuildFeatures(
        aidl = aidl,
        buildConfig = buildConfig,
        compose = compose,
        dataBinding = dataBinding,
        prefab = prefab,
        resValues = resValues,
        shaders = shaders,
        viewBinding = viewBinding,
    )
