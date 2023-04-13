import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

pluginManagement {
    apply(from = "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
    includeBuild("../build-settings")
    includeBuild("../build-dependencies")
}

plugins {
    id("tools.forma.includer") version "0.1.1"
    id("convention-dependencies")
    id("tools.forma.android")
    id("tools.forma.deps")
}

rootProject.name = "application"
