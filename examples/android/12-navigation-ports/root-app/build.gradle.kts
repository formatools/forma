// Composition root: owns NavController + Safe Args mapping (Layer A adapter).
androidApp(
    packageName = "tools.forma.examples.android.navports.root",
    dependencies = transitiveDeps(
        "androidx.fragment:fragment-ktx:1.8.6",
        "androidx.navigation:navigation-fragment-ktx:2.9.8",
        "androidx.navigation:navigation-runtime-ktx:2.9.8",
    ) + deps(
        target(":root-res"),
        target(":navigation-api"),
        target(":navigation:res"),
        target(":feature:list:impl"),
        target(":feature:detail:impl"),
    ),
)
