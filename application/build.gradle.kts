buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 21,
        targetSdk = 33,
        compileSdk = 33,
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
