buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
    }
}

// Enjoy easiest way to configure your Android project
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = "1.4.21",
    agpVersion = "4.2.0-beta02",
    versionCode = 1,
    versionName = "1.0",
    dataBinding = true
)