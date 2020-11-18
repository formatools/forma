impl(
    packageName = "com.stepango.blockme.feature.characters.favorite.impl",
    dependencies = deps(
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        androidx.lifecycle_viewmodel_ktx,
        androidx.lifecycle_extensions,
        androidx.recyclerview,
        androidx.swiperefreshlayout,
        androidx.paging,
        androidx.room,
        google.material,
        google.dagger,
        jakewharton.timber,
        kotlinx.coroutines_core

    ) + deps(
        project(":feature:characters:core:api"),
        project(":feature:characters:favorite:api"),
        project(":feature:characters:favorite:databinding"),

        project(":core:di:library"),
        project(":core:theme:android-util"),
        project(":core:mvvm:library"),
        project(":core:network:library"),
        project(":core:navigation:library"),

        project(":common:util"),
        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter")
    )
)