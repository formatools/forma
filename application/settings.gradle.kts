import tools.forma.deps.catalog.bundle
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
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.4")
        configurations.all {
            resolutionStrategy {
                force(
                    "com.android.tools.build:bundletool:1.15.5",
                    "com.google.guava:guava:31.1-jre",
                    "org.ow2.asm:asm:9.2",
                    "org.ow2.asm:asm-commons:9.2",
                    "org.ow2.asm:asm-util:9.2",
                    "com.google.code.gson:gson:2.8.9",
                    "org.apache.httpcomponents:httpclient:4.5.13",
                    "com.google.protobuf:protobuf-java:3.19.2",
                    "com.google.protobuf:protobuf-java-util:3.19.2",
                    "org.checkerframework:checker-qual:3.12.0",
                    "com.google.errorprone:error_prone_annotations:2.11.0",
                    "commons-codec:commons-codec:1.15",
                    "com.android.tools.build:aapt2-proto:8.1.2-10154469",
                    "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$embeddedKotlinVersion",
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
    id("com.gradle.enterprise") version ("3.15")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includer { arbitraryBuildScriptNames = true }

rootProject.name = "application"

val coilVersion = "2.1.0"
val sqliteVersion = "2.2.0"
val roomVersion = "2.5.1"

val ksp = CustomConfiguration("ksp")

projectDependencies(
    "libs",
    "com.jakewharton.timber:timber:5.0.1",
    bundle(name = "coil", "io.coil-kt:coil:$coilVersion", "io.coil-kt:coil-base:$coilVersion"),
    bundle(
        name = "room",
        "androidx.sqlite:sqlite:$sqliteVersion",
        "androidx.sqlite:sqlite-framework:$sqliteVersion",
        "androidx.room:room-runtime:$roomVersion",
        "androidx.room:room-ktx:$roomVersion",
        "androidx.room:room-common:$roomVersion",
    ),
    plugin("tools.forma.demo:dependencies", "0.0.1"),
    plugin("androidx.navigation:navigation-safe-args-gradle-plugin", "2.7.4"),
    plugin("com.google.firebase:firebase-crashlytics-gradle", "2.9.9"),
    plugin(
        id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
        version = "$embeddedKotlinVersion-1.0.13",
        configuration = ksp,
        "androidx.room:room-compiler:$roomVersion"
    )
)

// refer to this issue https://github.com/gradle/gradle/issues/18536
// tools.forma.dependencies are applied in buildscript {} block
includeBuild("../build-dependencies")
