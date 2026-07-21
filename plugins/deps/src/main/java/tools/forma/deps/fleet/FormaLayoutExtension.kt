package tools.forma.deps.fleet

import tools.forma.core.fleet.SourceLanguage

/**
 * Per-project layout metadata recorded from target DSLs that take `packageName` (F-088).
 *
 * Stored as a typed project extension (`formaLayout`) so check/generate tasks and
 * bulk root drivers share one source of truth.
 */
open class FormaLayoutExtension {
    /** Dotted package from the target DSL (e.g. `com.example.feature.api`). */
    var packageName: String = ""

    /** Source root language; defaults to Kotlin. */
    var language: SourceLanguage = SourceLanguage.KOTLIN

    /**
     * When false, [formaLayoutCheck] and [formaLayoutGenerate] are no-op success
     * (res / viewBinding / binary use `packageName` for AGP namespace / applicationId only).
     * Default true for code targets.
     */
    var requirePackageSourceDir: Boolean = true
}
