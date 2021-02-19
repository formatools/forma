buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.0")
    }
}

val properties = java.util.Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
val agpVersion: String? = properties.getProperty("agpVersion")
val kotlinVersion: String? = properties.getProperty("kotlinVersion")

// Enjoy easiest way to configure your Android project
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = kotlinVersion ?: "1.4.21",
    agpVersion = agpVersion ?: "4.2.0-beta02",
    versionCode = 1,
    versionName = "1.0",
    dataBinding = true
)