package tools.forma.core.validation

/**
 * Pure predicate for directory content validation (no Gradle APIs).
 * Implementations receive relative file/dir names under the inspected root (e.g. "src/main" or "src/main/res").
 * @return null if content is acceptable, otherwise a human-readable failure reason.
 */
fun interface ContentRule {
    fun check(filesUnderRoot: List<String>): String?
}

/** Rejects any "res" directory under the inspected root (e.g. src/main). */
object NoResourcesUnderMain : ContentRule {
    override fun check(filesUnderRoot: List<String>): String? =
        if (filesUnderRoot.any { it == "res" }) {
            "Please make sure this does not contain `res` directory"
        } else {
            null
        }
}

/** Requires the inspected root to contain exactly one "res" directory and nothing else. */
object OnlyResourcesUnderMain : ContentRule {
    override fun check(filesUnderRoot: List<String>): String? {
        val dirs = filesUnderRoot
        return if (dirs.size == 1 && dirs.first() == "res") {
            null
        } else {
            "Please make sure this target only contains `res` folder in `src/main`"
        }
    }
}

/**
 * For res/ inspection: every top-level entry under res must start with "layout".
 * Empty list is OK (matches historical `list.all { startsWith("layout") }` on empty).
 */
object OnlyLayoutResources : ContentRule {
    override fun check(filesUnderRoot: List<String>): String? {
        val allLayouts = filesUnderRoot.all { it.startsWith("layout") }
        return if (allLayouts) {
            null
        } else {
            "Please make sure this target only contains `layout.*` folders in `src/main/res`"
        }
    }
}
