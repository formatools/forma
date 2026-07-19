import tools.forma.deps.catalog.bundle
import tools.forma.deps.catalog.library
import tools.forma.deps.catalog.plugin
import tools.forma.deps.catalog.projectDependencies
import tools.forma.deps.core.CustomConfiguration

pluginManagement {
    repositories { google() }
    apply(from = "buildScan-disableAutoApplyFix.settings.gradle.kts")
    apply(
        from =
            "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts"
    )
    includeBuild("../build-settings")
    includeBuild("../plugins")
    includeBuild("../includer")
}

buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        configurations.all {
            resolutionStrategy {
                force(
                    "com.android.tools.build:bundletool:1.18.3",
                    "com.google.guava:guava:33.3.1-jre",
                    "org.ow2.asm:asm:9.7.1",
                    "org.ow2.asm:asm-commons:9.7.1",
                    "org.ow2.asm:asm-util:9.7.1",
                    "org.ow2.asm:asm-tree:9.7.1",
                    "org.ow2.asm:asm-analysis:9.7.1",
                    "com.google.code.gson:gson:2.13.2",
                    "com.google.j2objc:j2objc-annotations:3.0.0",
                    "org.apache.httpcomponents:httpclient:4.5.14",
                    "org.apache.httpcomponents:httpcore:4.4.16",
                    "com.google.protobuf:protobuf-java:3.25.5",
                    "com.google.protobuf:protobuf-java-util:3.25.5",
                    "org.checkerframework:checker-qual:3.43.0",
                    "com.google.errorprone:error_prone_annotations:2.28.0",
                    "commons-codec:commons-codec:1.17.1",
                    "com.android.tools.build:aapt2-proto:8.13.2-14304508",
                    // Gradle 8.14.5 embeds Kotlin 2.0.21 — force stdlib family lockstep under failOnVersionConflict
                    "org.jetbrains.kotlin:kotlin-stdlib:$embeddedKotlinVersion",
                    "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$embeddedKotlinVersion",
                    "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$embeddedKotlinVersion",
                    "org.jetbrains.kotlin:kotlin-stdlib-common:$embeddedKotlinVersion",
                    "org.jetbrains.kotlin:kotlin-reflect:$embeddedKotlinVersion",
                    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2",
                    "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2",
                    "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2",
                    "com.android.tools.build:gradle:8.13.2",
                    "org.jetbrains.kotlin:kotlin-gradle-plugin:$embeddedKotlinVersion",
                    "com.squareup:javapoet:1.13.0"
                )
                failOnVersionConflict()
                failOnNonReproducibleResolution()
            }
        }
    }
}

plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.android")
    id("com.gradle.enterprise") version ("3.19.2")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includer { arbitraryBuildScriptNames = true }

rootProject.name = "application"

val coilVersion = "2.7.0"
val sqliteVersion = "2.5.1"
val roomVersion = "2.7.2"

val ksp = CustomConfiguration("ksp")

// Version catalog UX: bare GAV, library(name=…), bundle, plugin — see docs/DEPS-CATALOG.md
projectDependencies(
    "libs",
    "com.jakewharton.timber:timber:5.0.1",
    // Explicit short names (library()) keep accessors stable: libs.coil, libs.coilBase
    bundle(
        name = "coil",
        library("io.coil-kt:coil:$coilVersion", name = "coil"),
        library("io.coil-kt:coil-base:$coilVersion", name = "coilBase"),
    ),
    bundle(
        name = "room",
        "androidx.sqlite:sqlite:$sqliteVersion",
        "androidx.sqlite:sqlite-framework:$sqliteVersion",
        "androidx.room:room-runtime:$roomVersion",
        "androidx.room:room-ktx:$roomVersion",
        "androidx.room:room-common:$roomVersion",
    ),
    plugin("tools.forma.demo:dependencies", "0.0.1"),
    plugin("androidx.navigation:navigation-safe-args-gradle-plugin", "2.7.7"),
    plugin("com.google.firebase:firebase-crashlytics-gradle", "3.0.7"),
    plugin(
        id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
        // Gradle 8.14.5 embeds Kotlin 2.0.21; KSP must match
        version = "2.0.21-1.0.28",
        configuration = ksp,
        "androidx.room:room-compiler:$roomVersion"
    )
)

// refer to this issue https://github.com/gradle/gradle/issues/18536
// tools.forma.dependencies are applied in buildscript {} block
includeBuild("../build-dependencies")
