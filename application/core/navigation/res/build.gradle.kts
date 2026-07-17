// Navigation graphs only — resources target (flat structure, not androidLibrary).
androidRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(
        androidx.navigation
    )
).withPlugin(Plugins.navigationSafeArgs)
