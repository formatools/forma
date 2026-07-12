package tools.forma.deps.catalog

import java.util.Locale
import org.gradle.configurationcache.extensions.capitalized

/**
 * Tokens dropped when turning Maven coordinates / plugin ids into Gradle version-catalog
 * accessor names (e.g. `com.jakewharton.timber:timber:5.0.1` → `jakewhartonTimber`).
 *
 * Keep this list conservative: filtering too aggressively yields empty or colliding names.
 */
val filteredTokens =
    listOf(
        "com",
        "io",
        "net",
        "org",
        "gradle",
        "android",
        "androidx",
        "kotlin",
        "kotlinx",
        "google",
        "plugin"
    )

/**
 * Generates a camelCase catalog name for a plugin id (version optional).
 *
 * Examples:
 * - `androidx.navigation:navigation-safe-args-gradle-plugin` → `navigationSafeArgs`
 * - `com.google.devtools.ksp:symbol-processing-gradle-plugin` → `devtoolsKspSymbolProcessing`
 */
fun pluginNameGenerator(groupArtifactVersion: String): String =
    generateName(
        tokens =
            groupArtifactVersion
                .split(":")
                .fold(emptyList()) { acc, s -> acc + s.split(".", "-") },
        source = groupArtifactVersion,
        kind = "plugin"
    )

/**
 * Generates a camelCase catalog name for a library coordinate (`group:artifact:version`).
 * The version segment is ignored for naming.
 *
 * Examples:
 * - `com.jakewharton.timber:timber:5.0.1` → `jakewhartonTimber`
 * - `io.coil-kt:coil-base:2.1.0` → `coilKtBase`
 */
fun defaultNameGenerator(groupArtifactVersion: String): String =
    generateName(
        tokens =
            groupArtifactVersion
                .split(":")
                .dropLast(1)
                .fold(emptyList()) { acc, s -> acc + s.split(".", "-") },
        source = groupArtifactVersion,
        kind = "library"
    )

internal fun generateName(
    tokens: List<String>,
    source: String,
    kind: String
): String {
    val name =
        tokens
            .filter { it.isNotBlank() && it !in filteredTokens }
            .distinct()
            .joinToString("") { it.capitalized() }
            .let { raw -> raw.replaceFirstChar { it.lowercase(Locale.getDefault()) } }

    require(name.isNotBlank()) {
        "Could not generate version-catalog $kind name for '$source' " +
            "(all path tokens were filtered). Pass an explicit name= instead."
    }
    return name
}

/**
 * Parse `group:artifact:version` into parts. Used by catalog builders so callers get a clear
 * error instead of a cryptic `IndexOutOfBoundsException`.
 */
fun parseGroupArtifactVersion(notation: String): Triple<String, String, String> {
    val parts = notation.split(":")
    require(parts.size == 3 && parts.all { it.isNotBlank() }) {
        "Invalid Maven coordinate '$notation'. Expected exactly group:artifact:version " +
            "(three non-blank segments separated by ':')."
    }
    return Triple(parts[0], parts[1], parts[2])
}
