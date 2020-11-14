dataBinding(
    packageName = "com.stepango.blockme.feature.characters.list.databinding",
    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.paging,
        google.material
    ) + deps(

        project(":core:theme:res"),
        project(":core:network:library"),
        project(":core:mvvm:library"),
        project(":feature:characters:core:api"),
        project(":feature:characters:list:api"),

        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")
    )
)