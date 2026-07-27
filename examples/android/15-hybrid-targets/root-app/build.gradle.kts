androidApp(
    packageName = "tools.forma.examples.android.hybrid.root",
    dependencies = deps(
        deps(
            target(":root-res"),
            target(":feature:hello:api"),
            target(":feature:world:api"),
        ),
        featureImplementation(
            impl = target(":feature:hello:impl"),
            stub = target(":feature:hello:stub-impl"),
        ),
        featureImplementation(
            impl = target(":feature:world:impl"),
            stub = target(":feature:world:stub-impl"),
        ),
    )
)
