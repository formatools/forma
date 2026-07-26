// Host layout references @navigation/app_nav_graph from navigationRes.
// FragmentContainerView + navGraph attrs need fragment/navigation on this res module.
androidRes(
    packageName = "tools.forma.examples.android.navports.root.res",
    dependencies = transitiveDeps(
        "androidx.fragment:fragment-ktx:1.8.6",
        "androidx.navigation:navigation-fragment-ktx:2.9.8",
    ) + deps(
        target(":navigation:res"),
    ),
)
