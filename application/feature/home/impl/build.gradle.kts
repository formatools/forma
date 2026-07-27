impl(
    packageName = "tools.forma.sample.feature.home.impl",
    dependencies =
        deps(
            androidx.core_ktx,
            androidx.appcompat,
            androidx.constraintlayout,
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
                target(":core:di:android-util"),
                target(":core:theme:android-util"),
                target(":core:mvvm:ui-library"),
                target(":core:navigation:api"),
                target(":core:navigation:android-util"),
                target(":common:extensions:android-util"),
            )
)
