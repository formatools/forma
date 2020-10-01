androidBinary(
    packageName = "com.stepango.blockme",
    projectDependencies = deps(
        project(":root-library"),
        project(":core:mvvm:library"),
        project(":common:extensions:android-util")
    )
)
