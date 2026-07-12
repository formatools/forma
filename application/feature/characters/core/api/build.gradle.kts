api(
    packageName = "tools.forma.sample.feature.characters.core.api",
    owner = Teams.core,
    dependencies = deps(
        squareup.retrofit
    ) + deps(
        target(":core:network:library")
    )
)
