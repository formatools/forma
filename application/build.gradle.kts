// Enjoy easiest way to configure your Android project
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = versions.jetbrains.kotlin,
    agpVersion = versions.agp,
    versionCode = 1,
    versionName = "1.0",
    repositories = {
        google()
        jcenter()
    },
    dataBinding = true,
    viewBinding = true
)
