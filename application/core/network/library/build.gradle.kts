androidLibrary(
    packageName = "com.stepango.blockme.core.network.library",

    dependencies = deps(
        squareup.retrofit,
        google.dagger
    ),

    projectDependencies = deps(
        project(":common:util")
    )
)