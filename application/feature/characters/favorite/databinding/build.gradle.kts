dataBinding(
    packageName = "com.stepango.blockme.feature.favorite.res",
    dependencies = deps(
        google.dagger,
        google.material,
        androidx.navigation,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation
    ) + deps(
        project(":feature:characters:favorite:api"),
        project(":feature:characters:favorite:res"),

        project(":core:navigation:library"),
        project(":core:mvvm:library"),
        project(":core:theme:res"),
        project(":common:extensions:databinding-adapter"),
        project(":common:extensions:android-util")
    )
)