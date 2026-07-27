viewBinding(
    packageName = "tools.forma.sample.feature.home.viewbinding",
    dependencies = deps(
        google.inject,
        google.material,
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":core:navigation:api"),
        target(":core:mvvm:ui-library"),
        target(":core:theme:res"),
        target(":feature:home:res"),
    )
)
