dataBinding(
    packageName = "com.stepango.blockme.feature.characters.list.databinding",
    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.paging,
        google.material
    ) + deps(

        target(":core:theme:res"),
        target(":core:network:library"),
        target(":core:mvvm:library"),
        target(":feature:characters:core:api"),
        target(":feature:characters:list:api"),
        target(":feature:characters:list:res"),

        target(":common:extensions:android-util"),
        target(":common:extensions:databinding-adapter")
    )
)
