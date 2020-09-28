dataBinding(
    packageName = "com.stepango.blockme.common.progressbar.databinding",

    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout
    ),

    projectDependencies = deps(
        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")
    )
)