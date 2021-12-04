androidLibrary(
    packageName = "com.stepango.blockme.feature.characters.core.library",
    dependencies = deps(
        google.dagger,
        squareup.retrofit
    ) + deps(
        project(":feature:characters:core:api"),

        project(":core:di:library"),
        project(":core:theme:android-util"),
        project(":core:mvvm:library"),
        project(":core:network:library"),

        project(":common:extensions:util"),
        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter"),
        project(":common:util")

    ),
    testDependencies = deps(
        test.unit,
        kotlinx.coroutines_test
    )
)