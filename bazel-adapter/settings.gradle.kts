rootProject.name = "bazel-adapter"

// F-041 spike: pure Kotlin consumer of forma-core via composite.
// No AGP, no plugins platform. Only tools.forma:core for TargetType + RestrictionGraph + registry.

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    // Match Gradle 9.6.1 embedded Kotlin
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.3.21"
    }
}

includeBuild("../plugins") {
    dependencySubstitution {
        // Resolve tools.forma:core to the local :core project (avoids mavenLocal / version skew during dev)
        substitute(module("tools.forma:core")).using(project(":core"))
    }
}
