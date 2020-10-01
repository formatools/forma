androidLibrary(
    packageName = "com.stepango.blockme.core.mvvm.library",

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
        dataBinding.runtime,
        javax.inject,
        jakewharton.timber
    ),

    testDependencies = deps(
        test.junit
    )
)
