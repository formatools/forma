import java.util.Properties

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.0")
    }
}

val properties = Properties()
val file: File = project.rootProject.file("local.properties")
if (file.exists()) file.inputStream().use { properties.load(it) }

// Enjoy the easiest way to configure your Android project
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 31,
    compileSdk = 31,
    dataBinding = true,
    validateManifestPackages = true,
    generateMissedManifests = true
)
