rootProject.name = "bazel-adapter"

// F-041 spike: pure Kotlin consumer of forma-core via composite.
// No AGP, no plugins platform. Only tools.forma:core for TargetType + RestrictionGraph + registry.

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    // Use same Kotlin as the main build (embedded in Gradle 8.3 env)
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.0"
    }
}

includeBuild("../plugins") {
    dependencySubstitution {
        // Resolve tools.forma:core to the local :core project (avoids mavenLocal / version skew during dev)
        substitute(module("tools.forma:core")).using(project(":core"))
    }
}
