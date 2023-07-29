buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 21,
        targetSdk = 33,
        compileSdk = 33,
        agpVersion = "7.4.2",
        extraPlugins =
            listOf(
                "androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3",
                "com.google.firebase:firebase-crashlytics-gradle:2.9.4",
                // TODO remove .get() call
                libs.plugins.tools.forma.demo.dependencies.get().pluginId,
                libs.plugins.devtools.ksp.get().let {
                    it.pluginId + ":" + it.version.strictVersion
                }
            )
    )
}
