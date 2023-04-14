plugins {
    id("com.gradle.plugin-publish") version "1.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.8.20")
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.8.20")

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }
    }
}

gradlePlugin {

    website.set("https://forma.tools/")
    vcsUrl.set("https://github.com/formatools/forma.git")

    plugins {
        create("Depgen") {
            id = "tools.forma.depgen"
            implementationClass = "tools.forma.depgen.DepgenPlugin"
        }
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}

@CacheableTask
open class GenerateTransitiveDependenciesTask : DefaultTask() {
    @get:Input
    var mavenDependencies: List<String> = listOf()

    @get:OutputFile
    val kotlinFile = File(project.buildDir, "generated/transitiveDeps.kt")

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
                        artifacts.map { it.file }
                            .firstOrNull { it.name.endsWith(".jar") }?.name
                    )
                }
            }
            .toSortedMap(compareBy { it.group })

// Check for newer versions
        resolvedDeps.forEach { (id, value) ->
            val (coordinates, _) = value
            project.dependencies.add(
                latestConfig.name,
                "${id.group}:${id.name}:[${id.version},)"
            )
        }

        val latestVersions =
            latestConfig.resolvedConfiguration.firstLevelModuleDependencies.map { dep ->
                "${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}"
            }

        resolvedDeps.forEach { (id, value) ->
            val (coordinates, _) = value
            val latestVersion =
                latestVersions.firstOrNull { it.startsWith("$id.group:$id.name:") }
            if (latestVersion != null && latestVersion != coordinates) {
                println("New version available: $coordinates -> $latestVersion")
            }
        }


        kotlinFile.parentFile.mkdirs()
        kotlinFile.writeText(
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

// Register the task
val generateTransitiveDeps by tasks.register(
    "generateTransitiveDeps",
    GenerateTransitiveDependenciesTask::class
) {
    group = "generation"
    description =
        "Generate a Kotlin file with all transitive dependencies of a list of Maven dependencies"
    mavenDependencies = listOf(
        "com.squareup.okhttp3:okhttp",
// Add more Maven dependencies here
    )
}
