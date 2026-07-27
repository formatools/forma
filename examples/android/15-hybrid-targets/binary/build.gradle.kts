androidBinary(
    packageName = "tools.forma.examples.android.hybrid",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = deps(
        deps(
            target(":root-app"),
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
