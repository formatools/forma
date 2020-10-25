impl(
    packageName = "com.stepango.blockme.feature.character.core.impl",
    dependencies = deps(
        google.dagger,
        squareup.retrofit

    ) + deps(
        project(":feature:character:core:api")

    ) + deps(
        project(":core:di:library"),
        project(":core:theme:android-util"),
        project(":core:mvvm:library"),
        project(":core:network:library")

    ) + deps(
        project(":common:extensions:util"),
        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")

    ) + kapt(
        google.dagger_compiler,
        dataBinding.compiler
    ),

    dataBinding = true
)