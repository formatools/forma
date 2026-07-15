impl(
    packageName = "tools.forma.examples.android.catalog.feature.hello.impl",
    dependencies = deps(
        libs.jakewhartonTimber,
    ) + deps(
        target(":feature:hello:api")
    )
)
