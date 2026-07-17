package tools.forma.android.target

import tools.forma.core.target.DefaultTargetRegistry
import tools.forma.core.target.TargetRegistration
import tools.forma.core.target.TargetRegistry
import tools.forma.core.validation.NoResourcesUnderMain
import tools.forma.core.validation.OnlyLayoutResources
import tools.forma.core.validation.OnlyResourcesUnderMain

/**
 * Singleton registry for Android platform target types.
 * Initialized explicitly via [registerAndroidDefaults] from [androidProjectConfiguration].
 *
 * DSL entrypoints obtain self + dependency validators from here (no more hand-written allow-lists).
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
    val noRes = listOf(NoResourcesUnderMain)
    val onlyRes = listOf(OnlyResourcesUnderMain)
    val onlyLayouts = listOf(OnlyLayoutResources)

    // api: api + jvm library (JVM contract layer)
    registry.register(
        TargetRegistration(
            type = t.api,
            allowedDependencies = setOf(t.api, t.library),
            contentRules = noRes
        )
    )

    // impl: api + utils + libraries + ui blocks; **no impl**
    registry.register(
        TargetRegistration(
            type = t.impl,
            allowedDependencies = setOf(
                t.api, t.androidUtil, t.testUtil, t.util, t.library,
                t.uiLibrary, t.res, t.viewBinding, t.widget, t.composeWidget
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

    // androidLibrary consumer (distinct id from jvmLibrary)
    registry.register(
        TargetRegistration(
            type = t.library,
            allowedDependencies = setOf(t.library, t.util, t.androidUtil, t.testUtil, t.res, t.api)
        )
    )

    // uiLibrary
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
            allowedDependencies = setOf(t.util, t.library),
            contentRules = noRes
        )
    )

    // androidUtil — may wrap pure JVM libraries; still no res content
    registry.register(
        TargetRegistration(
            type = t.androidUtil,
            allowedDependencies = setOf(t.androidUtil, t.testUtil, t.res, t.library),
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

    // androidTestUtil
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

    // viewBinding — may use shared UI bases (ui-library) after androidLibrary deprecation
    registry.register(
        TargetRegistration(
            type = t.viewBinding,
            allowedDependencies = setOf(
                t.api, t.widget, t.composeWidget, t.res, t.library, t.androidUtil, t.uiLibrary
            ),
            contentRules = onlyLayouts
        )
    )

    // androidApp (composition root)
    registry.register(
        TargetRegistration(
            type = t.app,
            allowedDependencies = setOf(
                t.api, t.impl, t.library, t.util, t.androidUtil, t.testUtil, t.res,
                t.viewBinding, t.widget, t.composeWidget, t.uiLibrary
            ),
            contentRules = noRes
        )
    )

    // androidBinary (composition root)
    registry.register(
        TargetRegistration(
            type = t.binary,
            allowedDependencies = setOf(
                t.app, t.api, t.impl, t.library, t.util, t.androidUtil, t.testUtil, t.res,
                t.viewBinding, t.widget, t.composeWidget, t.uiLibrary
            ),
            contentRules = noRes
        )
    )

    // native: no project-dep validation today
    registry.register(
        TargetRegistration(
            type = t.native,
            allowedDependencies = emptySet(),
            contentRules = noRes
        )
    )
}
