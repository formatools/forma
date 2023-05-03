androidUtil(
    packageName = "com.stepango.blockme.common.extensions.android.util",
    owner = Teams.core,
    dependencies = deps(
        androidx.constraintlayout,
        androidx.core_ktx,
        androidx.constraintlayout,
        androidx.fragment,
        androidx.viewmodel,
        androidx.navigation,
        androidx.recyclerview,
        androidx.paging,
        google.material,
        io.coil
    ) + deps(
        target(":common:placeholder:res")
    )
)