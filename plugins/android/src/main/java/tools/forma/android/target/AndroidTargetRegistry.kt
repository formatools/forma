package tools.forma.android.target

import tools.forma.core.target.DefaultTargetRegistry
import tools.forma.core.target.TargetRegistration
import tools.forma.core.target.TargetRegistry
import tools.forma.core.target.TargetType
import tools.forma.core.validation.NoResourcesUnderMain
import tools.forma.core.validation.OnlyLayoutResources
import tools.forma.core.validation.OnlyResourcesUnderMain
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.TargetPluginSpec
import tools.forma.deps.core.deriveTargetType as deriveTargetTypeWithCoreRegistry
import tools.forma.deps.core.registerTargetPlugin as registerTargetPluginGlobal
import tools.forma.deps.core.targetPlugin as targetPluginFactory
import tools.forma.kmp.target.KmpTargetTypes

/**
 * Singleton registry for Android platform target types.
 * Initialized explicitly via [registerAndroidDefaults] from [androidProjectConfiguration].
 *
 * DSL entrypoints obtain self + dependency validators from here (no more hand-written allow-lists).
 * Note: the historical `androidLibrary` target (android.library) was hard-removed in F-063.
 * Only JVM `library` (jvm.library) now uses the `library` suffix.
 *
 * F-108 KMP consumer edges (docs/KMP-TARGETS.md §6.2) — Android → kmp only; never kmp → android:
 * - api            → kmp.api
 * - impl           → kmp.api, kmp.library, kmp.util
 * - androidUtil    → kmp.library, kmp.util
 * - app / binary   → kmp.api, kmp.library, kmp.util
 * UI leaves (widget/composeWidget/res/viewBinding/uiLibrary/androidTestUtil) have **no** kmp edges.
 */
object AndroidTargetRegistry : TargetRegistry by DefaultTargetRegistry()

/**
 * Register Android target types + restriction matrix once.
 * Replicates the allow lists from AndroidRestrictionKit + DEPENDENCY-MATRIX for fidelity.
 * Attaches ContentRules for targets that historically called disallowResources / only* helpers
 * (enforcement of content rules remains via the Gradle helpers in DSL bodies for this phase).
 *
 * Safe to call multiple times (re-register replaces).
 */
fun registerAndroidDefaults(registry: TargetRegistry = AndroidTargetRegistry) {
    val t = AndroidTargetTypes
    val k = KmpTargetTypes
    val noRes = listOf(NoResourcesUnderMain)
    val onlyRes = listOf(OnlyResourcesUnderMain)
    val onlyLayouts = listOf(OnlyLayoutResources)

    // api: api + jvm library + kmp.api contracts (no kmp.library — keep api thin)
    registry.register(
        TargetRegistration(
            type = t.api,
            allowedDependencies = setOf(t.api, t.jvmLibrary, k.api),
            contentRules = noRes
        )
    )

    // impl: api + utils + libraries + ui blocks + shared KMP; **no impl**
    registry.register(
        TargetRegistration(
            type = t.impl,
            allowedDependencies = setOf(
                t.api, t.androidUtil, t.testUtil, t.util, t.jvmLibrary,
                t.uiLibrary, t.res, t.viewBinding, t.widget, t.composeWidget,
                k.api, k.library, k.util,
            )
        )
    )

    // JVM library() consumer (distinct id)
    registry.register(
        TargetRegistration(
            type = t.jvmLibrary,
            allowedDependencies = setOf(t.util, t.testUtil)
        )
    )

    // uiLibrary — no KMP edges (design §6.2 UI leaves)
    registry.register(
        TargetRegistration(
            type = t.uiLibrary,
            allowedDependencies = setOf(t.widget, t.composeWidget, t.util, t.androidUtil, t.res)
        )
    )

    // util (JVM)
    registry.register(
        TargetRegistration(
            type = t.util,
            allowedDependencies = setOf(t.util, t.jvmLibrary),
            contentRules = noRes
        )
    )

    // androidUtil — may wrap pure JVM libraries + native (.so) + shared KMP library/util; still no res content
    registry.register(
        TargetRegistration(
            type = t.androidUtil,
            allowedDependencies = setOf(
                t.androidUtil, t.testUtil, t.res, t.jvmLibrary, t.native,
                k.library, k.util,
            ),
            contentRules = noRes
        )
    )

    // testUtil
    registry.register(
        TargetRegistration(
            type = t.testUtil,
            allowedDependencies = setOf(t.testUtil, t.util),
            contentRules = noRes
        )
    )

    // androidTestUtil — no KMP edges (v1 §6.2)
    registry.register(
        TargetRegistration(
            type = t.androidTestUtil,
            allowedDependencies = setOf(t.androidTestUtil, t.testUtil)
        )
    )

    // androidRes
    registry.register(
        TargetRegistration(
            type = t.res,
            allowedDependencies = setOf(t.res, t.widget, t.composeWidget),
            contentRules = onlyRes
        )
    )

    // widget
    registry.register(
        TargetRegistration(
            type = t.widget,
            allowedDependencies = setOf(t.uiLibrary, t.widget, t.composeWidget, t.util, t.androidUtil, t.res)
        )
    )

    // composeWidget (coexist with widget)
    registry.register(
        TargetRegistration(
            type = t.composeWidget,
            allowedDependencies = setOf(t.uiLibrary, t.composeWidget, t.widget, t.util, t.androidUtil, t.res)
        )
    )

    // viewBinding — may use shared UI bases (ui-library)
    registry.register(
        TargetRegistration(
            type = t.viewBinding,
            allowedDependencies = setOf(
                t.api, t.widget, t.composeWidget, t.res, t.jvmLibrary, t.androidUtil, t.uiLibrary
            ),
            contentRules = onlyLayouts
        )
    )

    // androidApp (composition root) + shared KMP stack + optional native packaging
    registry.register(
        TargetRegistration(
            type = t.app,
            allowedDependencies = setOf(
                t.api, t.impl, t.jvmLibrary, t.util, t.androidUtil, t.testUtil, t.res,
                t.viewBinding, t.widget, t.composeWidget, t.uiLibrary, t.native,
                k.api, k.library, k.util,
            ),
            contentRules = noRes
        )
    )

    // androidBinary (composition root) + shared KMP stack + optional native packaging
    registry.register(
        TargetRegistration(
            type = t.binary,
            allowedDependencies = setOf(
                t.app, t.api, t.impl, t.jvmLibrary, t.util, t.androidUtil, t.testUtil, t.res,
                t.viewBinding, t.widget, t.composeWidget, t.uiLibrary, t.native,
                k.api, k.library, k.util,
            ),
            contentRules = noRes
        )
    )

    // native: NDK leaf — no first-party project deps (C/C++ + system libs only)
    registry.register(
        TargetRegistration(
            type = t.native,
            allowedDependencies = emptySet(),
            contentRules = noRes
        )
    )
}

/**
 * Path A convenience for Android: register plugins against a pre-defined AndroidTargetType.
 * Delegates to the shared (global) plugin registry.
 */
fun registerTargetPlugin(targetType: TargetType, vararg plugins: TargetPluginSpec) {
    registerTargetPluginGlobal(targetType, *plugins)
}

/** Factory re-export for convenience in android scripts. */
fun targetPlugin(id: String, dependencies: FormaDependency = EmptyDependency): TargetPluginSpec =
    targetPluginFactory(id, dependencies)

/**
 * Path B for Android: derive a new TargetType from a base Android type.
 * Clones allowedDependencies + contentRules into AndroidTargetRegistry under new id.
 * Attaches plugins so applyTargetPlugins will pick them up for this derived kind.
 */
fun deriveTargetType(
    id: String,
    base: TargetType,
    nameSuffix: String = base.nameSuffix,
    plugins: List<TargetPluginSpec> = emptyList()
): TargetType = deriveTargetTypeWithCoreRegistry(
    id = id,
    base = base,
    nameSuffix = nameSuffix,
    plugins = plugins,
    coreRegistry = AndroidTargetRegistry
)

