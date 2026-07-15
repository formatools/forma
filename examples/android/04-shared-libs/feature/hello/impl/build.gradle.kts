impl(
    packageName = "tools.forma.examples.android.shared.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api"),
        target(":common:library"),
        target(":common:util"),
        target(":common:android-util"),
        target(":core:platform:library")
    )
)
