viewBinding(
    packageName = "tools.forma.sample.feature.characters.favorite.viewbinding",
    dependencies = deps(
        google.material,
        androidx.appcompat,
        androidx.constraintlayout,
    ) + deps(
        target(":feature:characters:core:api"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:favorite:res"),

        target(":core:mvvm:ui-library"),
        target(":core:theme:res")
    )
)
