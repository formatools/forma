library(
    dependencies = deps(
        squareup.retrofit,
        google.dagger
    ) + deps(
        project(":common:util")
    ) + kapt(
        google.dagger_compiler
    )
)