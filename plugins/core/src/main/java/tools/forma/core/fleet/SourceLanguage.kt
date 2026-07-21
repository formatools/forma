package tools.forma.core.fleet

/**
 * Source root language under a module (GH #54 / F-084 layout tooling).
 * Maps to the conventional Android/JVM source set directory name.
 */
enum class SourceLanguage(val sourceSetDir: String) {
    JAVA("java"),
    KOTLIN("kotlin"),
}
