package tools.forma.jvm.target

import tools.forma.core.target.DefaultTargetRegistry
import tools.forma.core.target.TargetRegistration
import tools.forma.core.target.TargetRegistry

/**
 * Singleton registry for pure JVM target types (F-030).
 * Separate from AndroidTargetRegistry so platforms can evolve matrices independently.
 * Use [registerJvmDefaults] to populate the restriction matrix.
 */
object JvmTargetRegistry : TargetRegistry by DefaultTargetRegistry()

/**
 * Register JVM target types + restriction matrix.
 *
 * Matrix (Dagger-friendly, pure JVM, no Android types):
 * - api      → api, library
 * - impl     → api, library, util, test-util   (NO impl — composition only at binary/app roots later)
 * - library  → util, test-util
 * - util     → util, library
 * - testUtil → test-util, util   (library allowed for test helpers that need prod contracts)
 * - binary   → api, impl, library, util, test-util   (composition root for wiring multiple impls)
 *
 * No ContentRules attached for pure JVM (no Android resources concept).
 * Safe to call multiple times (re-register replaces).
 */
fun registerJvmDefaults(registry: TargetRegistry = JvmTargetRegistry) {
    val t = JvmTargetTypes

    // api: contracts only — api + (pure jvm) library
    registry.register(
        TargetRegistration(
            type = t.api,
            allowedDependencies = setOf(t.api, t.library)
        )
    )

    // impl: impl may use contracts + shared + utils; **no other impl**
    registry.register(
        TargetRegistration(
            type = t.impl,
            allowedDependencies = setOf(t.api, t.library, t.util, t.testUtil)
        )
    )

    // library (pure JVM): only util + test-util (parity with existing android jvm.library row)
    registry.register(
        TargetRegistration(
            type = t.library,
            allowedDependencies = setOf(t.util, t.testUtil)
        )
    )

    // util: utilities may depend on other utils + libraries
    registry.register(
        TargetRegistration(
            type = t.util,
            allowedDependencies = setOf(t.util, t.library)
        )
    )

    // test-util: test helpers use other test utils + util (+ library for test code needing contracts)
    registry.register(
        TargetRegistration(
            type = t.testUtil,
            allowedDependencies = setOf(t.testUtil, t.util, t.library)
        )
    )

    // binary (composition root): wires api + multiple impls + shared library/util for a runnable JVM app
    registry.register(
        TargetRegistration(
            type = t.binary,
            allowedDependencies = setOf(t.api, t.impl, t.library, t.util, t.testUtil)
        )
    )
}
