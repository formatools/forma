dataBinding(
    packageName = "com.stepango.blockme.feature.characters.detail.databinding",
    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout,
        google.material
    ) + deps(

        project(":core:theme:res"),
        project(":feature:characters:core:api"),
        project(":feature:characters:detail:api"),
        project(":feature:characters:detail:res"),

        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")
    )
)