package tools.forma.includer

import java.io.File
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

val logger: Logger = Logging.getLogger(IncluderPlugin::class.java)

/**
 * Once applied, Includer will search for nested projects and automatically includes them in the
 * build.
 */
abstract class IncluderPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        with(settings) {
            val extension = extensions.create("includer", IncluderExtension::class.java)

            gradle.settingsEvaluated { it.includeSubprojects(extension) }
        }
    }

    private fun Settings.includeSubprojects(extension: IncluderExtension) {
        val measuredTime = measureTimeMillis {
            runBlocking { rootDir.findBuildFiles(extension) }
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

    private suspend fun File.findBuildFiles(
        extension: IncluderExtension,
        root: Boolean = true,
    ): List<File> = coroutineScope {
        val (dirs, files) =
            listFiles()?.partition { it.isDirectory } ?: Pair(emptyList(), emptyList())

        // Completely ignore the project with the settings file and all its child directories
        if (!root && files.any { it.name in PROJECT_MARKER_FILES })
            return@coroutineScope emptyList()

        files.filterBuildFiles(extension, root) +
            dirs
                .filterNot { it.isHidden || it.name in extension.excludeFolders }
                .map { dir ->
                    async(Dispatchers.IO) { dir.findBuildFiles(extension, root = false) }
                }
                .awaitAll()
                .flatten()
    }

    private fun List<File>.filterBuildFiles(
        extension: IncluderExtension,
        root: Boolean,
    ): List<File> {
        if (root) return emptyList()

        val buildFiles =
            if (extension.arbitraryBuildScriptNames) {
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
                    appendLine(
                        "Leave only one .gradle(.kts) file, or use the " +
                            "`arbitraryBuildScriptNames = false` setting " +
                            "to ignore any build files other than build.gradle(.kts)."
                    )
                }
            )
        }
    }

    companion object {
        private val BUILD_GRADLE_FILES = setOf("build.gradle.kts", "build.gradle")
        private val PROJECT_MARKER_FILES = setOf("settings.gradle.kts", "settings.gradle")
    }
}
