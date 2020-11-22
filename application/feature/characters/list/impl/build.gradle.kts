impl(
    packageName = "com.stepango.blockme.feature.characters.list.impl",
    dependencies = deps(
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation,
        androidx.viewmodel,
        androidx.recyclerview,
        androidx.swiperefreshlayout,
        androidx.paging,
        google.material,
        google.dagger,
        jakewharton.timber,
        kotlinx.coroutines_core

    ) + deps(
        project(":feature:characters:core:api"),
        project(":feature:characters:list:api"),
        project(":feature:characters:list:databinding"),

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