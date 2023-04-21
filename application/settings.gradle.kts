pluginManagement {
    apply(from = "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
    includeBuild("../build-settings")
    includeBuild("../plugins")
    includeBuild("../includer")
}

plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.android")
    id("tools.forma.deps")
}

rootProject.name = "application"

// refer to this issue https://github.com/gradle/gradle/issues/18536
// tools.forma.dependencies are applied in buildscript {} block
includeBuild("../build-dependencies")