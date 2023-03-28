dataBinding(
    packageName = "com.stepango.blockme.feature.home.databinding",
    dependencies = deps(
        google.dagger,
        google.material,
        androidx.navigation,
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":core:navigation:library"),
        target(":core:mvvm:library"),
        target(":core:theme:res"),
        target(":feature:home:res"),
        target(":common:extensions:databinding-adapter"),
        target(":common:extensions:android-util")
    )
)
