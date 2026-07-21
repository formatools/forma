package tools.forma.core.fleet

/**
 * Filesystem-relative module path rename (no IO) — F-084 migrate spike.
 *
 * Does **not** rewrite build script ASTs; emits suggested string replacements for
 * Gradle project paths and Forma `target("…")` forms so fleet tools / humans can apply.
 */
data class PathRenamePlan(
    /** e.g. `feature/home/impl` */
    val fromModuleRelative: String,
    /** e.g. `feature/home/impl2` or `feature/dashboard/impl` */
    val toModuleRelative: String,
)

data class MigrationPlan(
    val filesystemMoves: List<Pair<String, String>>,
    /** Suggested textual replacements (old → new) for refs in scripts/docs. */
    val referenceRewrites: List<Pair<String, String>>,
    val notes: List<String>,
)

object MigratePlanner {

    fun planRename(plan: PathRenamePlan): MigrationPlan {
        val from = normalize(plan.fromModuleRelative)
        val to = normalize(plan.toModuleRelative)
        require(from.isNotEmpty()) { "fromModuleRelative must not be empty" }
        require(to.isNotEmpty()) { "toModuleRelative must not be empty" }
        require(from != to) { "from and to paths must differ" }

        val fromGradle = ProjectPathForms.gradleProjectPath(from)
        val toGradle = ProjectPathForms.gradleProjectPath(to)
        val fromForma = ProjectPathForms.formaTargetPath(from)
        val toForma = ProjectPathForms.formaTargetPath(to)

        val rewrites =
            listOf(
                fromGradle to toGradle,
                fromForma to toForma,
                // common target() call spellings
                "target(\"$fromForma\")" to "target(\"$toForma\")",
                "project(\"$fromGradle\")" to "project(\"$toGradle\")",
            )

        val notes =
            listOf(
                "Filesystem move is module-dir only; update packageName / sources separately if the package changes.",
                "fromGradleProjectPath is ambiguous when segments contain '-'; prefer relative dirs as source of truth.",
                "Apply referenceRewrites with review — string replace is not AST-safe.",
                "Run LayoutChecker / Forma validators after the move.",
            )

        return MigrationPlan(
            filesystemMoves = listOf(from to to),
            referenceRewrites = rewrites,
            notes = notes,
        )
    }

    private fun normalize(relativeDir: String): String {
        var s = relativeDir.trim().replace('\\', '/')
        while (s.startsWith("./")) s = s.removePrefix("./")
        while (s.startsWith("/")) s = s.removePrefix("/")
        while (s.endsWith("/")) s = s.removeSuffix("/")
        while (s.contains("//")) s = s.replace("//", "/")
        return s
    }
}
