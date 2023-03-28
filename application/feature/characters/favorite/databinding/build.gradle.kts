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
        target(":feature:characters:core:api"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:favorite:res"),

        target(":core:navigation:library"),
        target(":core:mvvm:library"),
        target(":core:theme:res"),
        target(":common:extensions:databinding-adapter"),
        target(":common:extensions:android-util")
    )
)
