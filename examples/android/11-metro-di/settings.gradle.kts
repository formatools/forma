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

rootProject.name = "11-metro-di"

// Classpath-only plugin coordinates (NOT per-module apply). Metro + Path B defs.
projectDependencies(
    "libs",
    plugin("tools.forma.examples:forma-defs", "0.0.1"),
    // Marker/GAV for Metro Gradle plugin (compiler plugin DI — no KSP).
    plugin("dev.zacsweers.metro:gradle-plugin", "1.3.2"),
)

// Local Path B registration (metroImpl / metroApp DSL)
includeBuild("forma-defs")
