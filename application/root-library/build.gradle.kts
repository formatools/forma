/**
 * This project meant to be self containing root library
 */
androidLibrary(
    packageName = "com.stepango.blockme.root.library",
    dependencies = deps(
        google.material,
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        androidx.vectordrawable,
        google.dagger,
        google.play_core,
        jakewharton.timber

    ) + deps(
        project(":core:di:library"),
        project(":core:theme:android-util")

    ) + kapt(
        google.dagger_compiler
    ),

    testDependencies = deps(
        test.junit
    ),

    androidTestDependencies = deps(
        test.junit_ext,
        test.espresso
    )
)