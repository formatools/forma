package depgen

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
open class GenerateTransitiveDependenciesTask : DefaultTask() {
    @get:Input
    var mavenDependencies: List<String> = listOf()

    @get:OutputFile
    val outputFile: File = File(project.buildDir, "generated/transitiveDeps.kt")

    @TaskAction
    fun generate() {
        val configuration = project.configurations.create("transitiveDeps")
        val latestConfig = project.configurations.create("latestVersions")
        mavenDependencies.forEach { dep ->
            val coordinates = if (dep.count { it == ':' } > 1) dep else "$dep:[0,)"
            project.dependencies.add(configuration.name, coordinates)
        }

        val resolvedDeps = configuration.resolvedConfiguration.resolvedArtifacts
            .groupBy { it.moduleVersion.id }
            .mapValues { (_, artifacts) ->
                artifacts.first().let { artifact ->
                    Pair(
                        "${artifact.moduleVersion.id.group}:${artifact.moduleVersion.id.name}:${artifact.moduleVersion.id.version}",
                        artifacts.map { it.file }.firstOrNull { it.name.endsWith(".jar") }?.name
                    )
                }
            }
            .toSortedMap(compareBy { it.group })

        // Check for newer versions
        resolvedDeps.forEach { (id, value) ->
            val (coordinates, _) = value
            project.dependencies.add(latestConfig.name, "${id.group}:${id.name}:[${id.version},)")
        }

        val latestVersions = latestConfig.resolvedConfiguration.firstLevelModuleDependencies.map { dep ->
            "${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}"
        }

        resolvedDeps.forEach { (id, value) ->
            val (coordinates, _) = value
            val latestVersion = latestVersions.firstOrNull { it.startsWith("$id.group:$id.name:") }
            if (latestVersion != null && latestVersion != coordinates) {
                println("New version available: $coordinates -> $latestVersion")
            }
        }

        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            buildString {
                appendLine("val transitiveDependencies = listOf(")
                resolvedDeps.forEach { (id, value) ->
                    val (coordinates, origin) = value
                    appendLine("    /* $origin */")
                    appendLine("    \"$coordinates\",")
                }
                appendLine(")")
            }
        )
    }
}
