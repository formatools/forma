androidLibrary(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(
        androidx.navigation
    )
).withPlugin(Plugins.navigationSafeArgs)