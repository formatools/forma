androidBinary(
    packageName = "com.stepango.blockme.app",
    dependencies = deps(
        project(":root-library"),
        project(":common:extensions:android-util"),
        project(":core:mvvm:library"),
        project(":core:di:library")
    )
)