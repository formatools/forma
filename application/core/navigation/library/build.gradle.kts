androidLibrary(
    packageName = "com.stepango.blockme.core.navigation.library",
    dependencies = deps(
        androidx.navigation
    )
)
// TODO https://github.com/formatools/forma/issues/35
// Need configurable plugin integration here
apply(plugin = "androidx.navigation.safeargs.kotlin")