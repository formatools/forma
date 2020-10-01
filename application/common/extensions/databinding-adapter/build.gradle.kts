dataBindingAdapters(
    packageName = "com.stepango.blockme.common.extensions.databinding.util",

    dependencies = deps(
        androidx.recyclerview,
        io.coil
    ),

    projectDependencies = deps(
        project(":common:recyclerview:widget")
    )
)