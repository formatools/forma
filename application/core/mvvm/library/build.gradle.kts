// TODO Change module type from `androidLibary` to `databinding`
androidLibrary(
    packageName = "tools.forma.sample.core.mvvm.library",

    dependencies = deps(
        google.material,
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation,
        androidx.viewmodel,
        androidx.paging,
        viewbinding.viewBinding,
        google.dagger,
    )+ deps(
        libs.jakewhartonTimber,
    ),

    testDependencies = deps(
        test.unit
    )
)
