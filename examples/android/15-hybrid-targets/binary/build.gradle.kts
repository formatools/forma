androidBinary(
    packageName = "tools.forma.examples.android.hybrid",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = deps(
        target(":root-app"),
        // Production path: api + impl (not stub). Do not wire both impl and stub-impl.
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":feature:world:api"),
        target(":feature:world:impl"),
        // Manual stub swap preview (F-104 will automate via project-global flag):
        // target(":feature:hello:api"),
        // target(":feature:hello:stub-impl"),
        // target(":feature:world:api"),
        // target(":feature:world:stub-impl"),
    )
)
