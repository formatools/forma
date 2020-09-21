androidLibrary(
    packageName = "com.stepango.blockme.mvvm.library",

    dependencies = deps(
        google.material,
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        androidx.lifecycle_viewmodel,
        androidx.lifecycle_viewmodel_ktx,
        androidx.paging,
        javax.inject,
        jakewharton.timber
    ),

    testDependencies = deps(
        test.junit
    ),

    dataBinding = true
)