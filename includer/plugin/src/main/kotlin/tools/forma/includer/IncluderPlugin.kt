package tools.forma.includer

import org.gradle.api.Plugin
import org.gradle.api.file.FileCollection
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject
import kotlin.system.measureTimeMillis

val logger: Logger = Logging.getLogger(IncluderPlugin::class.java)

/**
 * Once applied, Includer will search for nested projects and automatically includes them in the
 * build.
 */
abstract class IncluderPlugin @Inject constructor(
    private val factory: ObjectFactory,
) : Plugin<Settings> {

    override fun apply(settings: Settings) {
        with(settings) {
            val extension = extensions.create("includer", IncluderExtension::class.java)

            gradle.settingsEvaluated { it.includeSubprojects(extension) }
        }
    }

    private fun Settings.includeSubprojects(extension: IncluderExtension) {
        val measuredTime = measureTimeMillis {
            // Nested projects is a projects with settings script in their directory
            val nestedProjects =
                factory.querySettingsGradleFiles(rootDir).map { it.parentFile }

            factory
                .queryBuildGradleFiles(
                    rootDir = rootDir,
                    nestedProjects = nestedProjects,
                    extension = extension,
                )
                .validateDuplicates(extension)
                .forEach { buildFile ->
                    val moduleDir = buildFile.parentFile
                    val relativePath = moduleDir.relativeTo(rootDir).path
                    // Avoid using `:` as separator for module names as it is used by gradle to mark
                    // intermittent nested projects, which created automatically. This behavior
                    // leads to increased configuration time
                    val moduleName = ":$relativePath".replace(File.separator, "-")

                    include(moduleName)
                    with(project(moduleName)) {
                        projectDir = moduleDir
                        buildFileName = buildFile.name
                    }
                }
        }
        logger.info("Loaded in $measuredTime ms")
    }

    private fun ObjectFactory.querySettingsGradleFiles(rootDir: File): FileCollection =
        fileTree()
            .from(rootDir)
            .apply { include(*SETTINGS_GRADLE_FILES) }
            .filter { it.parentFile != rootDir }

    private fun ObjectFactory.queryBuildGradleFiles(
        rootDir: File,
        nestedProjects: List<File>,
        extension: IncluderExtension,
    ): FileCollection =
        fileTree()
            .from(rootDir)
            .apply {
                if (extension.arbitraryBuildScriptNames) {
                    include(*ANY_GRADLE_FILES)
                    exclude(*SETTINGS_GRADLE_FILES)
                } else {
                    include(*BUILD_GRADLE_FILES)
                }
                val nestedProjectRelativePaths = nestedProjects.map { it.toRelativeString(rootDir) }
                excludes += nestedProjectRelativePaths.map { "$it/**" } +
                    extension.excludeFolders.map { "**/$it/**" }
            }
            .filter { it.parentFile != rootDir }

    /**
     * The Gradle FileTree API does not guarantee the order of elements,
     * so we find several build scripts in one directory by remembering previous elements.
     */
    private fun Iterable<File>.validateDuplicates(
        extension: IncluderExtension,
    ): Iterable<File> {
        if (!extension.arbitraryBuildScriptNames) return this

        val subprojectDirs = mutableSetOf<File>()
        return onEach { gradleScript ->
            val subprojectDir = gradleScript.parentFile
            if (subprojectDir !in subprojectDirs) {
                subprojectDirs += subprojectDir
            } else {
                error(
                    "Directory $subprojectDir has more than one gradle build file. " +
                        "Leave only one .gradle(.kts) file, or use the " +
                        "`arbitraryBuildScriptNames = false` setting " +
                        "to ignore any build files other than build.gradle(.kts)."
                )
            }
        }
    }

    companion object {
        private val ANY_GRADLE_FILES = arrayOf("**/*.gradle.kts", "**/*.gradle")
        private val BUILD_GRADLE_FILES = arrayOf("**/build.gradle.kts", "**/build.gradle")
        private val SETTINGS_GRADLE_FILES = arrayOf("**/settings.gradle.kts", "**/settings.gradle")
    }
}
