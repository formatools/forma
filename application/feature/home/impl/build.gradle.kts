impl(
    packageName = "com.stepango.blockme.feature.home.impl",
    dependencies = deps(
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation,
        google.material,
        google.dagger,
        jakewharton.timber

    ) + deps(
        project(":feature:home:api"),
        project(":feature:home:databinding"),

        project(":core:di:library"),
        project(":core:theme:android-util"),
        project(":core:mvvm:library"),
        project(":core:navigation:library"),

        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")
    )
)