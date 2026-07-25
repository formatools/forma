package tools.forma.jvm.target

import tools.forma.core.target.DefaultTargetRegistry
import tools.forma.core.target.TargetRegistration
import tools.forma.core.target.TargetRegistry
import tools.forma.kmp.target.KmpTargetTypes

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
 * - api      → api, library, kmp.api
 * - impl     → api, library, util, test-util, kmp.api, kmp.library, kmp.util
 *              (NO impl — composition only at binary/app roots)
 * - library  → util, test-util, kmp.library, kmp.util
 * - util     → util, library, kmp.library, kmp.util
 * - testUtil → test-util, util, library   (no kmp-test-util in v1 §6.2)
 * - binary   → api, impl, library, util, test-util, kmp.api, kmp.library, kmp.util
 *
 * F-108 KMP consumer edges (docs/KMP-TARGETS.md §6.2) — JVM → kmp only; never kmp → jvm.
 * No ContentRules attached for pure JVM (no Android resources concept).
 * Safe to call multiple times (re-register replaces).
 */
fun registerJvmDefaults(registry: TargetRegistry = JvmTargetRegistry) {
    val t = JvmTargetTypes
    val k = KmpTargetTypes

    // api: contracts only — api + (pure jvm) library + kmp.api
    registry.register(
        TargetRegistration(
            type = t.api,
            allowedDependencies = setOf(t.api, t.library, k.api)
        )
    )

    // impl: impl may use contracts + shared + utils + KMP stack; **no other impl**
    registry.register(
        TargetRegistration(
            type = t.impl,
            allowedDependencies = setOf(
                t.api, t.library, t.util, t.testUtil,
                k.api, k.library, k.util,
            )
        )
    )

    // library (pure JVM): util + test-util + shared KMP library/util
    registry.register(
        TargetRegistration(
            type = t.library,
            allowedDependencies = setOf(t.util, t.testUtil, k.library, k.util)
        )
    )

    // util: utilities may depend on other utils + libraries + shared KMP
    registry.register(
        TargetRegistration(
            type = t.util,
            allowedDependencies = setOf(t.util, t.library, k.library, k.util)
        )
    )

    // test-util: test helpers use other test utils + util (+ library for test code needing contracts)
    registry.register(
        TargetRegistration(
            type = t.testUtil,
            allowedDependencies = setOf(t.testUtil, t.util, t.library)
        )
    )

    // binary (composition root): wires api + multiple impls + shared library/util + KMP
    registry.register(
        TargetRegistration(
            type = t.binary,
            allowedDependencies = setOf(
                t.api, t.impl, t.library, t.util, t.testUtil,
                k.api, k.library, k.util,
            )
        )
    )
}
