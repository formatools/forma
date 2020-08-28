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
        androidx.navigation_ui_ktx
    ),
    projectDependencies = dependencies(
        project(":mylibrary")
    ),
    testDependencies = dependencies("junit:junit:4.12"),
    androidTestDependencies = dependencies(
        "androidx.test.ext:junit:1.1.1",
        "androidx.test.espresso:espresso-core:3.2.0"
    )
)