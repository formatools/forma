impl(
    packageName = "tools.forma.sample.feature.characters.list.impl",
    dependencies =
        deps(
            androidx.core_ktx,
            androidx.appcompat,
            androidx.constraintlayout,
            androidx.viewmodel,
            androidx.recyclerview,
            androidx.swiperefreshlayout,
            androidx.paging,
            google.material,
            google.dagger,
            kotlinx.coroutines_core,
            viewbinding.viewpropertydelegate,
        ) +
            deps(libs.jakewhartonTimber) +
            deps(
                target(":feature:characters:core:api"),
                target(":feature:characters:list:api"),
                target(":feature:characters:list:viewbinding"),
                target(":feature:characters:list:res"),
                target(":core:di:android-util"),
                target(":core:theme:android-util"),
                target(":core:mvvm:ui-library"),
                target(":core:network:library"),
                target(":core:navigation:api"),
                target(":common:util"),
                target(":common:extensions:android-util"),
                target(":common:recyclerview:widget")
            )
)
