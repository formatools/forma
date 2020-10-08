androidLibrary(
    packageName = "com.stepango.blockme.core.network.library",

    dependencies = deps(
        squareup.retrofit,
        google.dagger
    ) + deps(
        project(":common:util")
    )
)