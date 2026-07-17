impl(
    packageName = "tools.forma.sample.feature.characters.core.impl",
    dependencies = deps(
        google.dagger,
        squareup.retrofit

    ) + deps(
        target(":feature:characters:core:api"),

        target(":core:di:android-util"),
        target(":core:theme:android-util"),
        target(":core:mvvm:ui-library"),
        target(":core:network:library"),

        target(":common:extensions:util"),
        target(":common:extensions:android-util"),
        target(":common:util")

    ),
    testDependencies = deps(
        test.unit,
        kotlinx.coroutines_test
    )
)
