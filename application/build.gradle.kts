buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 21,
        targetSdk = 33,
        compileSdk = 33,
        agpVersion = "7.4.2",
        extraPlugins =
            listOf(
                libs.plugins.toolsFormaDemoDependencies,
                libs.plugins.devtoolsKspSymbolProcessing,
                libs.plugins.navigationSafeArgs,
                libs.plugins.firebaseCrashlytics
            )
    )
}
