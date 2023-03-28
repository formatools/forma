dataBinding(
    packageName = "com.stepango.blockme.feature.characters.detail.databinding",
    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout,
        google.material
    ) + deps(

        target(":core:theme:res"),
        target(":feature:characters:core:api"),
        target(":feature:characters:detail:api"),
        target(":feature:characters:detail:res"),

        target(":common:extensions:android-util"),
        target(":common:extensions:databinding-adapter")
    )
)
