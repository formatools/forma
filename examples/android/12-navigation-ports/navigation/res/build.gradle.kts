// Layer B — Path B navigationRes (type-owned safe-args). Consumed by root adapter only.
// Use transitiveDeps: plain deps("gav") is non-transitive and drops NavArgs / annotation APIs.
navigationRes(
    packageName = "tools.forma.examples.android.navports.navigation.res",
    dependencies = transitiveDeps(
        "androidx.navigation:navigation-fragment-ktx:2.9.8",
        "androidx.navigation:navigation-ui-ktx:2.9.8",
        "androidx.navigation:navigation-runtime-ktx:2.9.8",
        "androidx.navigation:navigation-common-ktx:2.9.8",
    ),
)
