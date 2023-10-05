api(
    packageName = "tools.forma.sample.common.extensions.util",
    owner = Teams.core,
    dependencies = deps(
        squareup.retrofit
    ) + deps(
        target(":core:network:library")
    )
)
