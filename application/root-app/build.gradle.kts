/**
 * This project meant to be self containing root library
 */
androidApp(
    packageName = "com.stepango.blockme.root.library",
    owner = Teams.core,
    dependencies = deps(
        androidx.core_ktx,
        androidx.appcompat,
        androidx.navigation,
        androidx.vectordrawable,
        google.material,
        // Kapt enabled based on provided dep
        google.dagger,
        google.play,
        jakewharton.timber

    ) + deps(
        project(":root-res"),

        project(":feature:characters:core:api"),
        project(":feature:characters:core:impl"),
        project(":feature:characters:favorite:api"),
        project(":feature:characters:favorite:impl"),

        project(":core:di:library"),
        project(":core:navigation:library"),
        project(":core:theme:android-util"),
        project(":core:network:library"),
        project(":common:util")
    ),
    testDependencies = deps(
        test.unit
    ),
    androidTestDependencies = deps(
        test.ui
    )
)