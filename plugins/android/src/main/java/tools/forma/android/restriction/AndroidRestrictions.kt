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
 *
 * Note: androidLibrary (android.library type) hard-removed F-063; only JVM library() remains for suffix "library".
 */
object AndroidRestrictionKit {

    /** All Android TargetTypes in registration order (convenience).
     * Note: `android.library` (historical androidLibrary target) removed in F-063.
     * Only `jvm.library` now owns the `library` suffix.
     */
    val all: List<TargetType> = listOf(
        AndroidTargetTypes.api,
        AndroidTargetTypes.impl,
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

        // api: only api + jvm library (JVM contract layer)
        graph.allow(t.api, t.api, t.jvmLibrary)

        // impl: api + shared + ui building blocks; NO impl (Dagger boundaries)
        graph.allow(
            t.impl,
            t.api, t.androidUtil, t.testUtil, t.util, t.jvmLibrary,
            t.uiLibrary, t.res, t.viewBinding, t.widget, t.composeWidget
        )

        // JVM `library` (pure Kotlin/JVM target): only util + test-util (no api/impl/res per matrix)
        graph.allow(t.jvmLibrary, t.util, t.testUtil)

        // uiLibrary
        graph.allow(t.uiLibrary, t.widget, t.composeWidget, t.util, t.androidUtil, t.res)

        // util (JVM): util + jvm library
        graph.allow(t.util, t.util, t.jvmLibrary)

        // androidUtil — may depend on pure JVM library (helpers wrapping shared code)
        graph.allow(t.androidUtil, t.androidUtil, t.testUtil, t.res, t.jvmLibrary)

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

        // viewBinding — ui-library allowed for shared UI bases (post androidLibrary removal)
        graph.allow(
            t.viewBinding,
            t.api, t.widget, t.composeWidget, t.res, t.jvmLibrary, t.androidUtil, t.uiLibrary
        )

        // androidApp (composition root)
        graph.allow(
            t.app,
            t.api, t.impl, t.jvmLibrary, t.util, t.androidUtil, t.testUtil, t.res,
            t.viewBinding, t.widget, t.composeWidget, t.uiLibrary
        )

        // androidBinary (composition root)
        graph.allow(
            t.binary,
            t.app, t.api, t.impl, t.jvmLibrary, t.util, t.androidUtil, t.testUtil, t.res,
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
