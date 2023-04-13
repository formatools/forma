import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

pluginManagement {
    apply(from = "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
    includeBuild("../build-settings")
    includeBuild("../build-dependencies")
}

plugins {
    id("convention-dependencies")
    id("tools.forma.android")
    id("tools.forma.deps")
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        classpath("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

val counter = java.util.concurrent.atomic.AtomicInteger(0)

rootProject.name = "application"
measureTimeMillis {
    runBlocking {
        findGradleKtsFiles(rootDir)
            .toList().forEach {
                val moduleDir = it.parentFile
                val relativePath = rootDir.toPath().relativize(moduleDir.toPath()).toString()
                val moduleName = ":" + relativePath.replace(File.separator, "-")

                include(moduleName)

                val project = findProject(moduleName)!!
                project.projectDir = moduleDir
                project.buildFileName = it.name

            }
    }
}.let { println("Loaded in $it ms") }


suspend fun findGradleKtsFiles(
    root: File,
    ignoredFilenames: List<String> = listOf("settings.gradle.kts"),
    ignoredFolders: List<String> = listOf("build", "src", "buildSrc")
): List<File> = coroutineScope {

    val children = root.listFiles() ?: emptyArray()

    val (dirs, files) = children.partition { it.isDirectory }

    val gradleKtsFiles = if (root == rootDir) emptyList() else files
        .filter { it.name.endsWith(".gradle.kts") }
        .filterNot { it.isHidden || it.name in ignoredFilenames }

    val dirJobs = dirs
        .filterNot { it.isHidden || it.name in ignoredFolders }
        .map { dir ->
            async(Dispatchers.IO) {
                findGradleKtsFiles(dir, ignoredFilenames, ignoredFolders)
            }
        }

    val nestedGradleKtsFiles = dirJobs.awaitAll().flatten()

    gradleKtsFiles + nestedGradleKtsFiles
}

