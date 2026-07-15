androidApp(
    packageName = "tools.forma.examples.android.multi.root",
    dependencies = deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":feature:settings:api"),
        target(":feature:settings:impl")
    )
)
