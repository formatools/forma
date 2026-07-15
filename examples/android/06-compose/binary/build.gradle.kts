androidBinary(
    packageName = "tools.forma.examples.android.compose",
    versionCode = 1,
    versionName = "0.1.0",
    compose = true,
    dependencies = deps(
        target(":root-app"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":common:greeting:compose-widget")
    )
)
