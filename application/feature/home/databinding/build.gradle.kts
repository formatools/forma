dataBinding(
    packageName = "com.stepango.blockme.feature.home.res",
    dependencies = deps(
        google.dagger,
        google.material,
        androidx.navigation,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx
    ) + deps(
        project(":core:navigation:library"),
        project(":core:mvvm:library"),
        project(":core:theme:res"),
        project(":common:extensions:databinding-adapter"),
        project(":common:extensions:android-util")
    )
)