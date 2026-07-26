package tools.forma.core.fleet

/**
 * Path form conversions matching tools.forma.includer conventions (F-084).
 *
 * - Filesystem relative dir: `feature/home/impl`
 * - Gradle project path (includer): `:feature-home-impl`
 * - Forma target ref style: `:feature:home:impl`
 */
object ProjectPathForms {

    /**
     * `feature/home/impl` → `:feature-home-impl`
     * Accepts optional leading `./` and normalizes separators to `/` then `-`.
     */
    fun gradleProjectPath(relativeDir: String): String {
        val normalized = normalizeRelative(relativeDir)
        require(normalized.isNotEmpty()) { "relativeDir must not be empty" }
        return ":${normalized.replace('/', '-')}"
    }

    /**
     * `feature/home/impl` → `:feature:home:impl`
     */
    fun formaTargetPath(relativeDir: String): String {
        val normalized = normalizeRelative(relativeDir)
        require(normalized.isNotEmpty()) { "relativeDir must not be empty" }
        return ":${normalized.replace('/', ':')}"
    }

    /**
     * Forma logical target path → Gradle/includer project path (F-101 / GH #56).
     *
     * Happy-path call sites use colon Forma paths in `target("…")`:
     * - `:feature:home:impl` → `:feature-home-impl`
     * - `:root-app` → `:root-app` (single segment; dashes already in the name stay)
     *
     * Leading `:` is **required**. Slash / Bazel-style paths are out of scope (discussion #57).
     * Behavior matches historic `":" + name.substring(1).replace(":", "-")` for valid inputs.
     */
    fun gradleProjectPathFromFormaTarget(formaPath: String): String {
        val trimmed = formaPath.trim()
        require(trimmed.isNotEmpty()) { "Forma target path must not be empty" }
        require(trimmed.startsWith(":")) {
            "Forma target path must start with ':': '$formaPath'"
        }
        val body = trimmed.substring(1)
        require(body.isNotEmpty()) { "Forma target path body must not be empty: '$formaPath'" }
        // Colons → dashes (includer project name). Already-dashed bodies are identity.
        return ":${body.replace(":", "-")}"
    }

    /**
     * Best-effort reverse of [gradleProjectPath]: `:feature-home-impl` → `feature/home/impl`.
     *
     * **Ambiguous** when path segments themselves contain `-` (includer joins with `-`).
     * Prefer keeping the filesystem relative path as source of truth when available.
     */
    fun fromGradleProjectPath(gradlePath: String): String {
        val trimmed = gradlePath.trim()
        require(trimmed.startsWith(":")) {
            "gradle project path must start with ':': '$gradlePath'"
        }
        val body = trimmed.removePrefix(":")
        require(body.isNotEmpty()) { "gradle project path body must not be empty" }
        require(!body.contains('/')) {
            "expected includer-style path without '/': '$gradlePath'"
        }
        return body.replace('-', '/')
    }

    private fun normalizeRelative(relativeDir: String): String {
        var s = relativeDir.trim().replace('\\', '/')
        while (s.startsWith("./")) s = s.removePrefix("./")
        while (s.startsWith("/")) s = s.removePrefix("/")
        while (s.endsWith("/")) s = s.removeSuffix("/")
        // collapse duplicate slashes
        while (s.contains("//")) s = s.replace("//", "/")
        return s
    }
}
