library(
    packageName = "tools.forma.sample.core.network.library",
    dependencies = deps(
        squareup.retrofit,
        google.gson,
        google.dagger
    ) + deps(
        target(":common:util")
    )
)
