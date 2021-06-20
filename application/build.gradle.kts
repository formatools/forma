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
    kotlinVersion = properties.getProperty("kotlinVersion", "1.4.21"),
    agpVersion = properties.getProperty("agpVersion", "7.0.0-beta04"),
    versionCode = 1,
    versionName = "1.0",
    dataBinding = true
)
