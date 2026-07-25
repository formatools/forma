package tools.forma.kmp.target

import tools.forma.core.target.DefaultTargetRegistry
import tools.forma.core.target.TargetRegistration
import tools.forma.core.target.TargetRegistry

/**
 * Singleton registry for KMP target types (F-106).
 * Separate from AndroidTargetRegistry / JvmTargetRegistry.
 * Use [registerKmpDefaults] to populate the restriction matrix.
 */
object KmpTargetRegistry : TargetRegistry by DefaultTargetRegistry()

/**
 * Register KMP target types + restriction matrix (docs/KMP-TARGETS.md §6.1).
 *
 * - kmp-api       → kmp-api
 * - kmp-library   → kmp-api, kmp-library, kmp-util, kmp-test-util
 * - kmp-util      → kmp-util
 * - kmp-test-util → kmp-api, kmp-library, kmp-util, kmp-test-util
 *
 * No ContentRules in v1 (no Android res under common — enforced later if needed).
 * Safe to call multiple times (re-register replaces).
 */
fun registerKmpDefaults(registry: TargetRegistry = KmpTargetRegistry) {
    val t = KmpTargetTypes

    registry.register(
        TargetRegistration(
            type = t.api,
            allowedDependencies = setOf(t.api),
        )
    )

    registry.register(
        TargetRegistration(
            type = t.library,
            allowedDependencies = setOf(t.api, t.library, t.util, t.testUtil),
        )
    )

    registry.register(
        TargetRegistration(
            type = t.util,
            allowedDependencies = setOf(t.util),
        )
    )

    registry.register(
        TargetRegistration(
            type = t.testUtil,
            allowedDependencies = setOf(t.api, t.library, t.util, t.testUtil),
        )
    )
}
