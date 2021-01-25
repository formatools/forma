dataBinding(
    packageName = "com.stepango.blockme.feature.home.databinding",
    dependencies = deps(
        google.dagger,
        google.material,
        androidx.navigation,
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        project(":core:navigation:library"),
        project(":core:mvvm:library"),
        project(":core:theme:res"),
        project(":feature:home:res"),
        project(":common:extensions:databinding-adapter"),
        project(":common:extensions:android-util")
    )
)