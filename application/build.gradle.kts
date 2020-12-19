buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.2")
    }
}

// Enjoy easiest way to configure your Android project
androidProjectDefaultConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = "1.4.20",
    agpVersion = "4.2.0-beta01",
    versionCode = 1,
    versionName = "1.0",
    dataBinding = true
)

// TODO: create example of usage
androidProjectConfiguration(
    configurationKey = TV,
    minSdk = 23,
    versionCode = 10,
    versionName = "2.0"
)