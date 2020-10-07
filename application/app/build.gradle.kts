androidBinary(
    packageName = "com.stepango.blockme",
    projectDependencies = deps(
        project(":root-library"),
        project(":common:extensions:android-util"),
        project(":core:mvvm:library"),
        project(":core:di:library")
    )
)
