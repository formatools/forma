dataBindingAdapters(
    packageName = "com.stepango.blockme.common.extensions.databinding.adapter",

    dependencies = deps(
        androidx.recyclerview,
        io.coil
    ) + deps(
        target(":common:recyclerview:widget"),
        target(":common:placeholder:res")
    )

)
