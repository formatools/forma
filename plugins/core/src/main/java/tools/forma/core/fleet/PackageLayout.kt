package tools.forma.core.fleet

/**
 * Pure helpers for JVM/Android package → directory layout (F-084).
 * No Gradle APIs — safe for forma-core consumers and offline tools.
 */
object PackageLayout {

    private val segmentPattern = Regex("^[A-Za-z_][A-Za-z0-9_]*$")

    /**
     * Split a dotted package name into path segments.
     * @throws IllegalArgumentException if empty or any segment is invalid
     */
    fun segments(packageName: String): List<String> {
        val trimmed = packageName.trim()
        require(trimmed.isNotEmpty()) { "packageName must not be empty" }
        val parts = trimmed.split('.')
        require(parts.none { it.isEmpty() }) {
            "packageName must not contain empty segments: '$packageName'"
        }
        parts.forEach { part ->
            require(segmentPattern.matches(part)) {
                "invalid package segment '$part' in '$packageName'"
            }
        }
        return parts
    }

    /**
     * Relative source directory for [packageName], e.g.
     * `src/main/kotlin/com/foo/bar` or `src/main/java/com/foo/bar`.
     */
    fun sourceDir(
        packageName: String,
        language: SourceLanguage = SourceLanguage.KOTLIN,
    ): String {
        val segs = segments(packageName)
        return listOf("src", "main", language.sourceSetDir)
            .plus(segs)
            .joinToString("/")
    }
}
