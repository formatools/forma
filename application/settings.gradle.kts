dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "forma"

fileTree(".")
    .filter {
        it.isFile
            && it.name.endsWith(".gradle.kts")
            && !it.path.contains("buildSrc")
            && !it.name.startsWith("settings.gradle") }
    .forEach {
        val moduleDir = it.parentFile
        val relativePath = rootDir.toPath().relativize(moduleDir.toPath()).toString()
        val moduleName = ":" + relativePath.replace(File.separator, "-")

        include(moduleName)

        val project = findProject(moduleName)!!
        project.projectDir = moduleDir
        project.buildFileName = it.name
    }
