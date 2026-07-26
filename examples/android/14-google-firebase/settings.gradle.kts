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

rootProject.name = "14-google-firebase"

// Classpath-only — type-owned plugins auto-apply on firebaseBinary (Path A on binary).
projectDependencies(
    "libs",
    plugin("tools.forma.examples:forma-defs", "0.0.1"),
    plugin("com.google.gms:google-services", "4.4.2"),
    plugin("com.google.firebase:firebase-crashlytics-gradle", "3.0.7"),
)

includeBuild("forma-defs")
