androidApp(
    packageName = "tools.forma.examples.android.shared.root",
    dependencies = deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":common:library"),
        target(":core:platform:android-util")
    )
)
