// Feature impl depends on ports + UI only — NOT navigationRes / androidx.navigation.
impl(
    packageName = "tools.forma.examples.android.navports.feature.list.impl",
    dependencies = transitiveDeps(
        "androidx.fragment:fragment-ktx:1.8.6",
    ) + deps(
        target(":navigation-api"),
        target(":feature:list:viewbinding"),
    ),
)
