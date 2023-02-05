import java.util.Properties

buildscript {
    androidProjectConfiguration(
        project = project,
        minSdk = 21,
        targetSdk = 31,
        compileSdk = 31,
        dataBinding = true,
        validateManifestPackages = true,
        generateMissedManifests = true,
        extraPlugins = listOf(
            "androidx.navigation:navigation-safe-args-gradle-plugin:2.4.2",
            "com.google.firebase:firebase-crashlytics-gradle:2.5.0",
        )
    )
}

/**
 * By design, Forma does not support Root Project plugins
 */
plugins {
    id("com.osacky.doctor") version "0.8.1"
}

val properties = Properties()
val file: File = project.rootProject.file("local.properties")
if (file.exists()) file.inputStream().use { properties.load(it) }
