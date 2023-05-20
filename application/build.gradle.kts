buildscript {
    androidProjectConfiguration(
        project = project,
        minSdk = 21,
        targetSdk = 31,
        compileSdk = 31,
        agpVersion = "7.4.2",
        dataBinding = true,
        extraPlugins = listOf(
            "androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3",
            "com.google.firebase:firebase-crashlytics-gradle:2.9.4",
            libs.plugins.tools.forma.demo.dependencies.get().pluginId,
        )
    )
}
