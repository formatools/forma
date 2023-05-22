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
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

val logger: Logger = Logging.getLogger(IncluderPlugin::class.java)

/**
 * Once applied, Includer will search for nested projects and automatically includes them in the
 * build.
 */
class IncluderPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        includeSubprojects(settings)
    }
}

private fun includeSubprojects(settings: Settings) {
    val measuredTime = measureTimeMillis {
        runBlocking { findGradleKtsFiles(settings.rootDir, settings.rootDir) }
            .forEach { buildFile ->
                val moduleDir = buildFile.parentFile
                val relativePath =
                    settings.rootDir.toPath().relativize(moduleDir.toPath()).toString()
                // Avoid using `:` as separator for module names as it is used by gradle to
                // mark
                // intermittent nested projects, which created automatically. This behavior
                // leads to increased configuration time
                val moduleName = ":" + relativePath.replace(File.separator, "-")

                settings.include(moduleName)

                val project = settings.findProject(moduleName)!!
                project.projectDir = moduleDir
                project.buildFileName = buildFile.name
            }
    }
    logger.log(LogLevel.INFO, "Loaded in $measuredTime ms")
}

/**
 * Recursively finds all gradle files in the given directory, excluding the hidden files, given
 * filenames and folders.
 *
 * Ignores nested projects if the current directory contains a file from the `projectMarkerFiles`
 * list.
 *
 * @param rootDir root directory of the project
 * @param currentDir current directory to search
 * @param ignoredFilenames list of filenames to ignore
 * @param ignoredFolders list of folder names to ignore
 * @param projectMarkerFiles filenames that indicate that the directory as a gradle project
 */
private suspend fun findGradleKtsFiles(
    rootDir: File,
    currentDir: File,
    ignoredFilenames: List<String> = emptyList(),
    ignoredFolders: List<String> = listOf("build", "src", "buildSrc"),
    projectMarkerFiles: List<String> = listOf("settings.gradle.kts", "settings.gradle"),
): List<File> = coroutineScope {
    val children = currentDir.listFiles() ?: emptyArray()

    val (dirs, files) = children.partition { it.isDirectory }

    val gradleKtsFiles =
        if (currentDir == rootDir) {
            // Root project's build.gradle(.kts) always included implicitly
            emptyList()
        } else {
            files
                // gradle files may have name that is different from `build.gradle(.kts)` so we
                // include files which follows `*.gradle(.kts)` pattern
                .filter { it.name.endsWith(".gradle.kts") || it.name.endsWith(".gradle") }
                .filterNot { it.isHidden || it.name in ignoredFilenames }
        }

    val skipNestedProject = currentDir != rootDir && files.any { it.name in projectMarkerFiles }

    if (skipNestedProject) {
        gradleKtsFiles
    } else {
        gradleKtsFiles +
            dirs
                .filterNot { it.isHidden || it.name in ignoredFolders }
                .map { dir ->
                    async(Dispatchers.IO) {
                        findGradleKtsFiles(rootDir, dir, ignoredFilenames, ignoredFolders)
                    }
                }
                .awaitAll()
                .flatten()
    }
}
