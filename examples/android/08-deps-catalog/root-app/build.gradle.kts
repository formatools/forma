androidApp(
    packageName = "tools.forma.examples.android.catalog.root",
    dependencies = deps(
        libs.jakewhartonTimber,
        libs.coroutines,
        libs.bundles.logging,
    ) + deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl")
    )
)
