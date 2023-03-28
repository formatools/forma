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
        target(":toggle-widget"),

        target(":feature:home:api"),
        target(":feature:home:databinding"),

        target(":core:di:library"),
        target(":core:theme:android-util"),
        target(":core:mvvm:library"),
        target(":core:navigation:library"),

        target(":common:extensions:android-util"),
        target(":common:extensions:databinding-adapter")
    )
)
