import tools.forma.deps.catalog.bundle
import tools.forma.deps.catalog.library
import tools.forma.deps.catalog.plugin
import tools.forma.deps.catalog.projectDependencies

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    apply(from = "../../../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
    includeBuild("../../../build-settings")
    includeBuild("../../../plugins")
    includeBuild("../../../includer")
}

plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.android")
}

includer { arbitraryBuildScriptNames = true }

rootProject.name = "08-deps-catalog"

projectDependencies(
    "libs",
    "com.jakewharton.timber:timber:5.0.1",
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3", name = "coroutines"),
    bundle(
        name = "logging",
        "com.jakewharton.timber:timber:5.0.1",
    ),
    plugin("androidx.navigation:navigation-safe-args-gradle-plugin", "2.7.4"),
)
