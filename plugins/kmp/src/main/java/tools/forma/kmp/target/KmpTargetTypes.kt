package tools.forma.kmp.target

import tools.forma.core.target.TargetType
import tools.forma.core.target.targetType

/**
 * Stable TargetType instances for the Kotlin Multiplatform platform (F-105 / F-106).
 *
 * Suffixes are **kmp-prefixed** so they never collide with Android/JVM `api` / `library` /
 * `util` rows under suffix-based name matching.
 *
 * See `docs/KMP-TARGETS.md`.
 */
object KmpTargetTypes {
    /** Shared public contracts (expect/actual-friendly). */
    val api: TargetType = targetType("kmp.api", "kmp-api")

    /** Shared multiplatform library (default workhorse). */
    val library: TargetType = targetType("kmp.library", "kmp-library")

    /** Shared utilities / extensions. */
    val util: TargetType = targetType("kmp.util", "kmp-util")

    /** Shared test helpers (commonTest + platform tests). */
    val testUtil: TargetType = targetType("kmp.test-util", "kmp-test-util")
}
