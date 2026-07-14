package tools.forma.bazel.adapter

import tools.forma.bazel.model.FormaProjectModel

/**
 * Core adapter surface (F-041).
 * Pure; no Bazel runtime dep; consumes forma-core types only for graph checks.
 */
interface FormaToBazel {
    fun generate(model: FormaProjectModel): Map<String /* package dir */, String /* BUILD content */>

    fun check(model: FormaProjectModel, existingBuilds: Map<String, String> = emptyMap()): CheckReport
}

data class CheckReport(
    val violations: List<String>,
    val warnings: List<String>,
)
