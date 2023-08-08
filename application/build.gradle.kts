buildscript {
    androidProjectConfiguration(
        project = project,
        minSdk = 21,
        targetSdk = 33,
        compileSdk = 33,
        agpVersion = "7.4.2",
        extraPlugins = listOf(
            "androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3",
            "com.google.firebase:firebase-crashlytics-gradle:2.9.4",
            libs.plugins.tools.forma.demo.dependencies.get().pluginId,
        )
    )
}

Forma.registerConfiguration("ksp", libs.plugins.devtools.ksp, libs.room.compiler)
