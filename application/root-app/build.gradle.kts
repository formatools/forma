/** This project meant to be self containing root library */
androidApp(
    packageName = "com.stepango.blockme.root.library",
    owner = Teams.core,
    dependencies =
        deps(
            androidx.core_ktx,
            androidx.appcompat,
            androidx.navigation,
            androidx.vectordrawable,
            google.material,
            // Kapt enabled based on provided dep
            google.dagger,
            google.play,
        ) +
            deps(libs.jakewhartonTimber) +
            deps(
                target(":root-res"),
                target(":feature:characters:core:api"),
                target(":feature:characters:core:impl"),
                target(":feature:characters:favorite:api"),
                target(":feature:characters:favorite:impl"),
                target(":core:di:library"),
                target(":core:navigation:library"),
                target(":core:theme:android-util"),
                target(":core:network:library"),
                target(":common:util")
            ),
    testDependencies = deps(test.unit),
    androidTestDependencies = deps(test.ui)
)
