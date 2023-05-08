viewBinding(
    packageName = "com.stepango.blockme.feature.characters.list.viewbinding",
    dependencies = deps(
        google.material,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.paging,
    ) + deps(
        target(":feature:characters:core:api"),
        target(":feature:characters:list:api"),
        target(":feature:characters:list:res"),

        target(":core:theme:res"),
        target(":core:network:library"),
        target(":core:mvvm:library"),
    )
)