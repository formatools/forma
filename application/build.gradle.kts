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
