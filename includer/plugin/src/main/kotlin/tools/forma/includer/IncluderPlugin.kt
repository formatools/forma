package tools.forma.includer

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.*
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logging
import java.io.File
import kotlin.system.measureTimeMillis

val logger = Logging.getLogger(IncluderPlugin::class.java)

/**
 * Once applied Includer will detect nested projects and automatically include it into the rootProject
 */
class IncluderPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        includeSubprojects(settings)
    }

    private fun includeSubprojects(settings: Settings) {
        measureTimeMillis {
            runBlocking {
                findGradleKtsFiles(settings.rootDir, settings.rootDir)
                    .forEach {
                        val moduleDir = it.parentFile
                        val relativePath = settings.rootDir.toPath().relativize(moduleDir.toPath()).toString()
                        val moduleName = ":" + relativePath.replace(File.separator, "-")

                        settings.include(moduleName)

                        val project = settings.findProject(moduleName)!!
                        project.projectDir = moduleDir
                        project.buildFileName = it.name

                    }
            }
        }.let { logger.log(LogLevel.INFO, "Loaded in $it ms") }
    }

    private suspend fun findGradleKtsFiles(
        rootDir: File,
        currentDir: File,
        ignoredFilenames: List<String> = emptyList(),
        ignoredFolders: List<String> = listOf("build", "src", "buildSrc"),
        nestedProjectMarkerFiles: List<String> = listOf("settings.gradle.kts", "settings.gradle"),
    ): List<File> = coroutineScope {

        val children = currentDir.listFiles() ?: emptyArray()

        val (dirs, files) = children.partition { it.isDirectory }

        val gradleKtsFiles = if (currentDir == rootDir) emptyList() else files
            .filter { it.name.endsWith(".gradle.kts") || it.name.endsWith(".gradle") }
            .filterNot { it.isHidden || it.name in ignoredFilenames }

        val skipNestedProject = currentDir != rootDir && files.any { it.name in nestedProjectMarkerFiles }

        fun dirJobs() = dirs
            .filterNot { it.isHidden || it.name in ignoredFolders }
            .map { dir ->
                async(Dispatchers.IO) {
                    findGradleKtsFiles(rootDir, dir, ignoredFilenames, ignoredFolders)
                }
            }

        val nestedGradleKtsFiles = if (!skipNestedProject) dirJobs().awaitAll().flatten() else emptyList()

        gradleKtsFiles + nestedGradleKtsFiles
    }
}
