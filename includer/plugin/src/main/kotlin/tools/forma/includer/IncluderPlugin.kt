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

    override fun apply(settings: Settings) {
        settings.includeSubprojects()
    }

    private fun Settings.includeSubprojects() {
        val measuredTime = measureTimeMillis {
            runBlocking { rootDir.findBuildGradleFiles(root = true) }
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

    private suspend fun File.findBuildGradleFiles(root: Boolean = true): List<File> =
        coroutineScope {
            val (dirs, files) = listFiles().partition { it.isDirectory }

            if (!root && files.any { it.name in PROJECT_MARKER_FILES })
                return@coroutineScope emptyList()

            files.filter { !root && it.name in BUILD_GRADLE_FILES } +
                    dirs.filterNot { it.isHidden || it.name in IGNORED_FOLDERS }
                        .map { dir ->
                            async(Dispatchers.IO) {
                                dir.findBuildGradleFiles(root = false)
                            }
                        }
                        .awaitAll()
                        .flatten()
        }

    companion object {

        private val BUILD_GRADLE_FILES = setOf("build.gradle.kts", "build.gradle")
        private val PROJECT_MARKER_FILES = setOf("settings.gradle.kts", "settings.gradle")
        private val IGNORED_FOLDERS = setOf("build", "src", "buildSrc", ".gradle")
    }
}
