package tools.forma.jvm.target

import tools.forma.core.target.TargetType
import tools.forma.core.target.targetType

/**
 * Stable TargetType instances for the pure JVM platform (F-030).
 *
 * These are the authoritative definitions for standalone JVM usage.
 * Ids are distinct where needed; "jvm.library" / "jvm.util" intentionally reuse the
 * ids that AndroidTargetTypes also declares for its pure-JVM rows (shared id strings ok;
 * each platform uses its own *TargetRegistry singleton).
 */
object JvmTargetTypes {
    /** Public API contracts (Dagger friendly). */
    val api: TargetType = targetType("jvm.api", "api")

    /** Feature implementation (pure JVM). Must not depend on other impl. */
    val impl: TargetType = targetType("jvm.impl", "impl")

    /** Shared pure JVM library code. */
    val library: TargetType = targetType("jvm.library", "library")

    /** Utility / extension modules (JVM). */
    val util: TargetType = targetType("jvm.util", "util")

    /** Test helpers (JVM). */
    val testUtil: TargetType = targetType("jvm.test-util", "test-util")

    /** JVM binary / composition root (runnable app entrypoint). */
    val binary: TargetType = targetType("jvm.binary", "binary")
}
