viewBinding(
    packageName = "com.stepango.blockme.feature.home.viewbinding",
    dependencies = deps(
        google.inject,
        google.material,
        androidx.navigation,
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":core:navigation:library"),
        target(":core:mvvm:library"),
        target(":core:theme:res"),
        target(":feature:home:res"),
    )
)