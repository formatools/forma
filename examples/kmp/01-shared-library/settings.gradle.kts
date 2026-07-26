pluginManagement {
    repositories {
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
    id("tools.forma.jvm")
    id("tools.forma.kmp")
}

includer { arbitraryBuildScriptNames = true }

rootProject.name = "01-shared-library"
