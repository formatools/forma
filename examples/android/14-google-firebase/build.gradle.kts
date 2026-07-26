buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 23,
        targetSdk = 37,
        compileSdk = 37,
        agpVersion = "9.3.0",
        extraPlugins = listOf(
            libs.plugins.toolsFormaExamplesDefs,
            libs.plugins.gmsServices,
            libs.plugins.firebaseCrashlytics,
        ),
    )
}
