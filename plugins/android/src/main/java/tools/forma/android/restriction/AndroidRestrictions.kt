package tools.forma.android.restriction

import tools.forma.android.target.AndroidTargetTypes
import tools.forma.core.restriction.EdgeKind
import tools.forma.core.restriction.MutableRestrictionGraph
import tools.forma.core.restriction.RestrictionGraph
import tools.forma.core.target.TargetType

/**
 * Android restriction kit (F-021).
 * Populates a MutableRestrictionGraph from the live matrix in docs/DEPENDENCY-MATRIX.md .
 * Pure graph lives in :core; this is the Android-specific wiring.
 *
 * Rules are keyed by TargetType (id) not suffix, satisfying §3.3 / §7.
 * Critical: `impl` does not allow `impl`.
 */
object AndroidRestrictionKit {

    /** All Android TargetTypes in registration order (convenience). */
    val all: List<TargetType> = listOf(
        AndroidTargetTypes.api,
        AndroidTargetTypes.impl,
        AndroidTargetTypes.library,
        AndroidTargetTypes.jvmLibrary,
        AndroidTargetTypes.uiLibrary,
        AndroidTargetTypes.native,
        AndroidTargetTypes.util,
        AndroidTargetTypes.testUtil,
        AndroidTargetTypes.androidTestUtil,
        AndroidTargetTypes.androidUtil,
        AndroidTargetTypes.viewBinding,
        AndroidTargetTypes.res,
        AndroidTargetTypes.widget,
        AndroidTargetTypes.composeWidget,
        AndroidTargetTypes.app,
        AndroidTargetTypes.binary
    )

    /**
     * Register the Android dependency matrix into the provided graph.
     * Call once during androidProjectConfiguration or plugin apply.
     */
    fun register(graph: MutableRestrictionGraph) {
        val t = AndroidTargetTypes

        // api: only api + library (JVM contract layer)
        graph.allow(t.api, t.api, t.library)

        // impl: api + shared + ui building blocks; NO impl (Dagger boundaries)
        graph.allow(
            t.impl,
            t.api, t.androidUtil, t.testUtil, t.util, t.library,
            t.uiLibrary, t.res, t.viewBinding, t.widget, t.composeWidget
        )

        // JVM `library` (pure Kotlin/JVM target): only util + test-util (no api/impl/res per matrix)
        graph.allow(t.jvmLibrary, t.util, t.testUtil)

        // androidLibrary: library + util + android-util + test-util + res + api (no impl, no widgets)
        // Distinct consumer id (android.library) from jvm.library prevents merge in graph.
        graph.allow(t.library, t.library, t.util, t.androidUtil, t.testUtil, t.res, t.api)

        // uiLibrary
        graph.allow(t.uiLibrary, t.widget, t.composeWidget, t.util, t.androidUtil, t.res)

        // util (JVM): util + library
        graph.allow(t.util, t.util, t.library)

        // androidUtil — may depend on pure JVM library (helpers wrapping shared code)
        graph.allow(t.androidUtil, t.androidUtil, t.testUtil, t.res, t.library)

        // testUtil
        graph.allow(t.testUtil, t.testUtil, t.util)

        // androidTestUtil
        graph.allow(t.androidTestUtil, t.androidTestUtil, t.testUtil)

        // androidRes
        graph.allow(t.res, t.res, t.widget, t.composeWidget)

        // widget
        graph.allow(t.widget, t.uiLibrary, t.widget, t.composeWidget, t.util, t.androidUtil, t.res)

        // composeWidget (symmetric with widget for coexistence)
        graph.allow(t.composeWidget, t.uiLibrary, t.composeWidget, t.widget, t.util, t.androidUtil, t.res)

        // viewBinding — ui-library allowed so shared UI bases are not stuffed into androidLibrary
        graph.allow(
            t.viewBinding,
            t.api, t.widget, t.composeWidget, t.res, t.library, t.androidUtil, t.uiLibrary
        )

        // androidApp (composition root)
        graph.allow(
            t.app,
            t.api, t.impl, t.library, t.util, t.androidUtil, t.testUtil, t.res,
            t.viewBinding, t.widget, t.composeWidget, t.uiLibrary
        )

        // androidBinary (composition root)
        graph.allow(
            t.binary,
            t.app, t.api, t.impl, t.library, t.util, t.androidUtil, t.testUtil, t.res,
            t.viewBinding, t.widget, t.composeWidget, t.uiLibrary
        )

        // native: no project dep validation currently (per matrix)
        // no allow() calls for native → empty = deny all project targets
    }

    /** Convenience: build a fresh graph pre-populated with Android rules. */
    fun build(): RestrictionGraph {
        val g = MutableRestrictionGraph()
        register(g)
        return g
    }
}
