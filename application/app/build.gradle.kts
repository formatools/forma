androidBinary(
    packageName = "com.stepango.blockme",
    projectDependencies = deps(
        project(":root-library"),
        project(":mvvm:library"),
        project(":mvvm:databinding")
    )
)
