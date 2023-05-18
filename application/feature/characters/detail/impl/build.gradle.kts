impl(
    packageName = "com.stepango.blockme.feature.characters.detail.impl",
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
        kotlinx.coroutines_core,
        viewbinding.viewpropertydelegate
    ) + deps(
        libs.jakewharton.timber,
    ) + deps(
        target(":feature:characters:core:api"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:detail:api"),
        target(":feature:characters:detail:viewbinding"),

        target(":core:di:library"),
        target(":core:theme:android-util"),
        target(":core:mvvm:library"),
        target(":core:network:library"),
        target(":core:navigation:library"),

        target(":common:util"),
        target(":common:extensions:android-util"),
        target(":common:progressbar:viewbinding")
    )
)
