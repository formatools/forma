androidApp(
    packageName = "tools.forma.examples.android.testutils.root",
    dependencies = deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl")
    )
)
