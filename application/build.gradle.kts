import java.util.Properties

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.0")
    }
}

val properties = Properties()
val file: File = project.rootProject.file("local.properties")
if (file.exists()) file.inputStream().use { properties.load(it) }

// Enjoy easiest way to configure your Android project
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    dataBinding = true,
    validateManifestPackages = true,
    generateMissedManifests = true
)
