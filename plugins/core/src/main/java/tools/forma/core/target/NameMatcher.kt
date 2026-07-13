package tools.forma.core.target

/**
 * Strategy for matching a Gradle project name against a TargetType.
 * Default is suffix based to preserve existing sample naming (project == suffix or ends with -suffix).
 */
fun interface NameMatcher {
    fun matches(projectName: String, type: TargetType): Boolean
}

/**
 * Default name matcher: accepts exact suffix match or dash-suffixed (e.g. "feature-impl" matches "impl").
 * Matches the behavior in tools.forma.validation today.
 */
object SuffixNameMatcher : NameMatcher {
    override fun matches(projectName: String, type: TargetType): Boolean {
        val s = type.nameSuffix
        return projectName == s || projectName.endsWith("-$s", ignoreCase = false)
    }
}
