enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "forma-demo-app"

pluginManagement {
    apply(from = "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
    includeBuild("../build-settings")
    includeBuild("../build-dependencies")
    includeBuild("../includer")
}

plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.android")
    id("tools.forma.deps")
}

rootProject.name = "application"
