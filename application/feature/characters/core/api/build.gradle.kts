api(
    owner = Teams.core,
    dependencies = deps(
        squareup.retrofit
    ) + deps (
        project(":core:network:library")
    )
)