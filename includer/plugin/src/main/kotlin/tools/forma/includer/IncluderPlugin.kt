package tools.forma.includer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import java.io.File
import kotlin.system.measureTimeMillis

val logger: Logger = Logging.getLogger(IncluderPlugin::class.java)

/**
 * Once applied, Includer will search for nested projects and automatically includes them in the
 * build.
 */
class IncluderPlugin : Plugin<Settings> {

    private class Options(
        val ignoredFolders: Set<String>,
        val arbitraryBuildFileNames: Boolean,
    )

    override fun apply(settings: Settings) {
        with(settings) {
            includeSubprojects(loadOptions())
        }
    }

    private fun Settings.loadOptions(): Options {
        val ext = extensions.extraProperties.properties

        // Comma separated list of folder names
        val ignoredFolders = ext["tools.forma.includer.ignoredFolders"]
            ?.toString()
            ?.split(',')
            ?.map { it.trim() }
            .orEmpty()

        // Default is "true"
        val arbitraryBuildFileNames = ext["tools.forma.includer.arbitraryBuildFileNames"]
            ?.toString() != "false"

        return Options(
            ignoredFolders = IGNORED_FOLDERS + ignoredFolders.toSet(),
            arbitraryBuildFileNames = arbitraryBuildFileNames,
        )
    }

    private fun Settings.includeSubprojects(options: Options) {
        val measuredTime = measureTimeMillis {
            runBlocking { rootDir.findBuildFiles(options) }
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
        logger.log(LogLevel.INFO, "Loaded in $measuredTime ms")
    }

    private suspend fun File.findBuildFiles(options: Options, root: Boolean = true): List<File> =
        coroutineScope {
            val (dirs, files) = listFiles().partition { it.isDirectory }

            // Completely ignore the project with the settings file and all its child directories
            if (!root && files.any { it.name in PROJECT_MARKER_FILES })
                return@coroutineScope emptyList()

            files.filterBuildFiles(options, root) +
                    dirs.filterNot { it.isHidden || it.name in options.ignoredFolders }
                        .map { dir ->
                            async(Dispatchers.IO) {
                                dir.findBuildFiles(options, root = false)
                            }
                        }
                        .awaitAll()
                        .flatten()
        }

    private fun List<File>.filterBuildFiles(options: Options, root: Boolean): List<File> {
        if (root) return emptyList()

        val buildFiles =
            if (options.arbitraryBuildFileNames) {
                filter { it.name.endsWith(".gradle.kts") || it.name.endsWith(".gradle") }
            } else {
                filter { it.name in BUILD_GRADLE_FILES }
            }

        // Make sure that we found the only build file in the directory or did not find it at all
        // Thus, we prevent the addition of the same module by several build files
        if (buildFiles.isEmpty() || buildFiles.size == 1) {
            return buildFiles
        } else {
            // If more than one build file is found, we inform the developer about the conflict
            val parentDir = buildFiles.first().parentFile
            error(
                buildString {
                    appendLine("Directory $parentDir has more than one gradle build file:")
                    buildFiles.forEach { appendLine("- ${it.name}") }
                    appendLine("Leave only one .gradle(.kts) file, or use the " +
                            "`tools.forma.includer.arbitraryBuildFileNames=false` setting " +
                            "to ignore any build files other than build.gradle(.kts).")
                }
            )
        }
    }

    companion object {

        private val BUILD_GRADLE_FILES = setOf("build.gradle.kts", "build.gradle")
        private val PROJECT_MARKER_FILES = setOf("settings.gradle.kts", "settings.gradle")
        private val IGNORED_FOLDERS = setOf("build", "src", "buildSrc")
    }
}
