buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 21,
        targetSdk = 33,
        // compileSdk 34 required by Compose transitive emoji2 1.4+ AAR metadata
        compileSdk = 34,
        agpVersion = "8.1.2",
        extraPlugins =
            listOf(
                libs.plugins.toolsFormaDemoDependencies,
                libs.plugins.devtoolsKspSymbolProcessing,
                libs.plugins.navigationSafeArgs,
                libs.plugins.firebaseCrashlytics
            )
    )
}
