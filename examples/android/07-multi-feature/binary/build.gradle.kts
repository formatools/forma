androidBinary(
    packageName = "tools.forma.examples.android.multi",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = deps(
        target(":root-app"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":feature:settings:api"),
        target(":feature:settings:impl")
    )
)
