impl(
    packageName = "com.stepango.blockme.feature.characters.core.impl",
    dependencies = deps(
        google.dagger,
        squareup.retrofit

    ) + deps(
        target(":feature:characters:core:api"),

        target(":core:di:library"),
        target(":core:theme:android-util"),
        target(":core:mvvm:library"),
        target(":core:network:library"),

        target(":common:extensions:util"),
        target(":common:extensions:android-util"),
        target(":common:extensions:databinding-adapter"),
        target(":common:util")

    ),
    testDependencies = deps(
        test.unit,
        kotlinx.coroutines_test
    )
)
