androidLibrary(
    packageName = "com.stepango.blockme.core.navigation.library",
    dependencies = deps(
        androidx.navigation
    )
).withPlugin(pluginWrappers.navigationSafeArgs)