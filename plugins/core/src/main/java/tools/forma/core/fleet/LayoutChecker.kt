package tools.forma.core.fleet

import java.nio.file.Files
import java.nio.file.Path

data class LayoutCheckViolation(
    val path: String,
    val message: String,
)

/**
 * Check mode for package source layout (F-084). Pure relative to a module directory.
 * Complements runtime [tools.forma.core.validation.ContentRule] checks.
 */
object LayoutChecker {

    /**
     * Verify that `src/main/{java|kotlin}/<package>` exists under [moduleDir].
     */
    fun checkPackageSourceDir(
        moduleDir: Path,
        packageName: String,
        language: SourceLanguage = SourceLanguage.KOTLIN,
    ): List<LayoutCheckViolation> {
        val violations = mutableListOf<LayoutCheckViolation>()
        val rel =
            try {
                PackageLayout.sourceDir(packageName, language)
            } catch (e: IllegalArgumentException) {
                return listOf(
                    LayoutCheckViolation(
                        path = packageName,
                        message = e.message ?: "invalid packageName",
                    ),
                )
            }
        val dir = moduleDir.resolve(rel)
        if (!Files.isDirectory(dir)) {
            violations +=
                LayoutCheckViolation(
                    path = rel,
                    message =
                        "missing package source directory for '$packageName' " +
                            "(expected $rel under module)",
                )
        }
        return violations
    }
}
