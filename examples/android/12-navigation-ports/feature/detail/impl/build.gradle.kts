// Feature impl: plain Bundle arg key — not NavArgs / androidx.navigation.
impl(
    packageName = "tools.forma.examples.android.navports.feature.detail.impl",
    dependencies = transitiveDeps(
        "androidx.fragment:fragment-ktx:1.8.6",
    ) + deps(
        target(":navigation-api"),
        target(":feature:detail:viewbinding"),
    ),
)
