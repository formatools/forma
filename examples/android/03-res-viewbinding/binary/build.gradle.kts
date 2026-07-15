androidBinary(
    packageName = "tools.forma.examples.android.resvb",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = deps(
        target(":root-app"),
        target(":feature:hello:api"),
        target(":feature:hello:impl")
    )
)
