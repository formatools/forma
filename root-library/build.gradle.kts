/**
 * This project meant to be self containing root library
 */

//android_library()
//    .packageName("")
//    .dependencies("")
//    .projectDependencies()

//android_library(
//
//).dependencies(
//    depencies = deps(
//
//    ),
//    projecDependencies = deps(
//
//    )
//)

//dependencies = deps(
//    compileOnly(
//
//    ),
//    Flavor.debugPaidArm(
//        google.material,
//        androidx.core_ktx,
//        androidx.appcompat,
//        androidx.constraintlayout,
//        androidx.navigation_fragment_ktx,
//        androidx.navigation_ui_ktx,
//        androidx.vectordrawable
//    ),
//    Flavor.release(
//        google.material,
//        androidx.core_ktx,
//        androidx.appcompat,
//        androidx.constraintlayout,
//        androidx.navigation_fragment_ktx,
//        androidx.navigation_ui_ktx,
//        androidx.vectordrawable
//    ),
//),


android_library(
    packageName = "com.stepango.example",
    dependencies = deps(
        google.material,
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        androidx.vectordrawable
    ),
    projectDependencies = deps(
        project(":demo-library")
    ),
    testDependencies = deps(
        test.junit
    ),
    androidTestDependencies = deps(
        test.junit_ext,
        test.espresso
    )
)