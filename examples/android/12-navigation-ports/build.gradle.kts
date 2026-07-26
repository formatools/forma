buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 23,
        targetSdk = 37,
        compileSdk = 37,
        agpVersion = "9.3.0",
        // extraPlugins = classpath only (buildscript). Type-owned plugins auto-apply per target.
        extraPlugins = listOf(
            libs.plugins.toolsFormaExamplesDefs,
            libs.plugins.navigationSafeArgs,
        ),
    )
}
