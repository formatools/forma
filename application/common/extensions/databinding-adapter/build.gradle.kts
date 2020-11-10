dataBindingAdapters(
    packageName = "com.stepango.blockme.common.extensions.databinding.util",

    dependencies = deps(
        androidx.recyclerview,
        io.coil
    ) + deps(
        project(":common:recyclerview:widget"),
        project(":common:placeholder:res")
    )

)
