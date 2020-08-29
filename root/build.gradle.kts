/**
 * This project meant to be self containing root library
 */

kt_android_library(
    packageName = "com.stepango.example",
    dependencies = dependencies(
        google.material,
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        androidx.vectordrawable
    ),
    projectDependencies = dependencies(
        project(":mylibrary")
    ),
    testDependencies = dependencies(
        test.junit
    ),
    androidTestDependencies = dependencies(
        test.junit_ext,
        test.espresso
    )
)