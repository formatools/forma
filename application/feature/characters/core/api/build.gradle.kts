api(
    packageName = "com.stepango.blockme.common.extensions.util",
    ""
    dependencies = deps(
        squareup.retrofit
    ) + deps (
        target(":core:network:library")
    )
)
