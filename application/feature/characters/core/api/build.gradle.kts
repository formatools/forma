api(
    packageName = "com.stepango.blockme.common.extensions.util",
    owner = Teams.core,
    dependencies = deps(
        squareup.retrofit
    ) + deps (
        target(":core:network:library")
    )
)
