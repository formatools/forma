viewBinding(
    packageName = "com.stepango.blockme.feature.favorite.viewbinding",
    dependencies = deps(
        google.material,
        androidx.navigation,
        androidx.appcompat,
        androidx.constraintlayout,
    ) + deps(
        target(":feature:characters:core:api"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:favorite:res"),

        target(":core:navigation:library"),
        target(":core:mvvm:library"),
        target(":core:theme:res")
    )
)