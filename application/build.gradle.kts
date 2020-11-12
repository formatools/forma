buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.1")
    }
}

// Enjoy easiest way to configure your Android project
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = "1.4.10",
    agpVersion = "4.1.1",
    versionCode = 1,
    versionName = "1.0",
    repositories = {
        google()
        jcenter()
    },
    dataBinding = true
)