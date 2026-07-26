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

rootProject.name = "12-navigation-ports"

// Classpath-only plugin coordinates (NOT per-module apply). Safe-args + Path B defs.
projectDependencies(
    "libs",
    plugin("tools.forma.examples:forma-defs", "0.0.1"),
    plugin("androidx.navigation:navigation-safe-args-gradle-plugin", "2.9.8"),
)

// Local Path B registration (navigationRes DSL) — mirrors application includeBuild(build-dependencies)
includeBuild("forma-defs")
