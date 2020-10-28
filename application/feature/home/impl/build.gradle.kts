impl(
    packageName = "com.stepango.blockme.feature.home.impl",
    dependencies = deps(
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        google.material,
        google.dagger,
        jakewharton.timber

    ) + deps(
        project(":feature:home:api")

    ) + deps (
        project(":core:di:library"),
        project(":core:theme:android-util"),
        project(":core:mvvm:library")

    ) + deps (
        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")
    ),

    dataBinding = true
)