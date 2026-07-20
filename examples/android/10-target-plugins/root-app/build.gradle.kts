androidApp(
    packageName = "tools.forma.examples.android.targetplugins.root",
    dependencies = deps(
        "androidx.navigation:navigation-runtime-ktx:2.7.7",
    ) + deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl")
    )
)
