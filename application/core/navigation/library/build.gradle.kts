androidLibrary(
    packageName = "com.stepango.blockme.core.navigation.library",
    dependencies = deps(
        androidx.navigation
    )
)
// TODO Need configurable plugin integration here
apply(plugin = "androidx.navigation.safeargs.kotlin")