pluginManagement {
    includeBuild("../build-settings")
}

plugins {
    id("convention-dependencies")
}

includeRecursive(rootDir)

fun includeRecursive(source: File) {
    source.walkTopDown().maxDepth(1).forEach { childProject ->
        if (isModule(childProject)) {
            include(":${childProject.name}")
        }
    }
}

fun isModule(dir: File): Boolean {
    return dir.resolve("src").exists() &&
            (dir.resolve("build.gradle").exists() ||
                    dir.resolve("build.gradle.kts").exists())
}