impl(
    packageName = "com.stepango.blockme.feature.characters.core.impl",
    dependencies = deps(
        google.dagger,
        squareup.retrofit

    ) + deps(
        project(":feature:characters:core:api")

    ) + deps(
        project(":core:di:library"),
        project(":core:theme:android-util"),
        project(":core:mvvm:library"),
        project(":core:network:library")

    ) + deps(
        project(":common:extensions:util"),
        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")

    ),

    dataBinding = true
)