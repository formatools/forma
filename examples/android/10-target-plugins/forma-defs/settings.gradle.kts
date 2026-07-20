pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    apply(from = "../../../../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
    includeBuild("../../../../build-settings")
}

plugins {
    id("convention-dependencies")
}

// Resolve tools.forma:* from the monorepo plugins composite
includeBuild("../../../../plugins")

rootProject.name = "forma-defs"
