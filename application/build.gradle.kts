import org.gradle.api.JavaVersion

buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 23,
        targetSdk = 37,
        // F-087: compileSdk 37 for AndroidX core 1.19 AAR metadata (minCompileSdk=37)
        compileSdk = 37,
        agpVersion = "9.3.0",
        // Navigation 2.9 / modern AndroidX ship JVM 11 bytecode — cannot inline into 1.8
        javaVersionCompatibility = JavaVersion.VERSION_11,
        extraPlugins =
            listOf(
                libs.plugins.toolsFormaDemoDependencies,
                libs.plugins.devtoolsKspSymbolProcessing,
                libs.plugins.navigationSafeArgs,
                libs.plugins.firebaseCrashlytics
            )
    )
}
