@file:Suppress("UnstableApiUsage")

rootProject.name = "build-settings"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("conventions")
