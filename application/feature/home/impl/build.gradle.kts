impl(
    packageName = "tools.forma.sample.feature.home.impl",
    dependencies =
        deps(
            androidx.core_ktx,
            androidx.appcompat,
            androidx.constraintlayout,
            androidx.navigation,
            google.material,
            google.dagger,
            viewbinding.viewpropertydelegate
        ) +
            deps(libs.jakewhartonTimber) +
            deps(
                target(":toggle-widget"),
                target(":feature:home:api"),
                target(":feature:home:res"),
                target(":feature:home:viewbinding"),
                target(":core:di:library"),
                target(":core:theme:android-util"),
                target(":core:mvvm:library"),
                target(":core:navigation:library"),
                target(":common:extensions:android-util"),
            )
)
