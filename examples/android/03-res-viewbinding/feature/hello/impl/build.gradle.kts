impl(
    packageName = "tools.forma.examples.android.resvb.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api"),
        target(":feature:hello:res"),
        target(":feature:hello:viewbinding")
    )
)
