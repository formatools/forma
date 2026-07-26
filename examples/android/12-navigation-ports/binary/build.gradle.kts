androidBinary(
    packageName = "tools.forma.examples.android.navports",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = transitiveDeps(
        "androidx.navigation:navigation-runtime:2.9.8",
        "androidx.navigation:navigation-common:2.9.8",
        "androidx.navigation:navigation-fragment:2.9.8",
    ) + deps(
        target(":root-app"),
        target(":feature:list:impl"),
        target(":feature:detail:impl"),
    ),
)
