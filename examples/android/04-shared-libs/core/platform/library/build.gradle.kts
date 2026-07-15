androidLibrary(
    packageName = "tools.forma.examples.android.shared.core.platform.library",
    dependencies = deps(
        target(":common:library"),
        target(":common:android-util")
    )
)
