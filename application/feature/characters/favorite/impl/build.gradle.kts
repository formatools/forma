impl(
    packageName = "com.stepango.blockme.feature.characters.favorite.impl",
    dependencies = deps(
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation,
        androidx.viewmodel,
        androidx.recyclerview,
        androidx.swiperefreshlayout,
        androidx.paging,
        androidx.room,
        google.material,
        google.dagger,
        jakewharton.timber,
        kotlinx.coroutines_core

    ) + deps(
        target(":feature:characters:core:api"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:favorite:databinding"),

        target(":core:di:library"),
        target(":core:theme:android-util"),
        target(":core:mvvm:library"),
        target(":core:network:library"),
        target(":core:navigation:library"),

        target(":common:util"),
        target(":common:extensions:android-util"),
        target(":common:extensions:databinding-adapter")
    )
)
