androidBinary(
    packageName = "tools.forma.examples.android.targetplugins",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = deps(
        // Bring navigation attrs into the APK merge (startDestination, etc.)
        "androidx.navigation:navigation-runtime:2.7.7",
        "androidx.navigation:navigation-common:2.7.7",
        "androidx.navigation:navigation-fragment:2.7.7",
    ) + deps(
        target(":root-app"),
        target(":feature:hello:api"),
        target(":feature:hello:impl")
    )
)
