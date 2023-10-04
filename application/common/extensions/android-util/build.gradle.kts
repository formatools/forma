androidUtil(
    packageName = "tools.forma.sample.common.extensions.android.util",
    owner = Teams.core,
    dependencies =
        deps(
            androidx.constraintlayout,
            androidx.core_ktx,
            androidx.constraintlayout,
            androidx.fragment,
            androidx.viewmodel,
            androidx.navigation,
            androidx.recyclerview,
            androidx.paging,
            google.material,
        ) + deps(libs.bundles.coil) + deps(target(":common:placeholder:res"))
)
