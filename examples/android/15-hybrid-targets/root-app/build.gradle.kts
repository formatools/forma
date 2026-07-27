androidApp(
    packageName = "tools.forma.examples.android.hybrid.root",
    dependencies = deps(
        target(":root-res"),
        // Production path: api + impl. Swap impl → stub-impl manually to exercise stubs.
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":feature:world:api"),
        target(":feature:world:impl"),
        // Stub alternative (comment impl lines above; uncomment these):
        // target(":feature:hello:api"),
        // target(":feature:hello:stub-impl"),
        // target(":feature:world:api"),
        // target(":feature:world:stub-impl"),
    )
)
