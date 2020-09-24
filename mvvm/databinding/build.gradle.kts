androidLibrary(
    packageName = "com.stepango.blockme.mvvm.databinding",
    dependencies = deps(
        androidx.recyclerview,
        io.coil
    ),
    projectDependencies = deps(
        project(":common:recyclerview:widget")
    ),
    dataBinding = true
)