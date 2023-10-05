viewBinding(
    packageName = "tools.forma.sample.feature.characters.detail.viewbinding",
    dependencies = deps(
        google.material,
        androidx.constraintlayout,
    ) + deps(
        target(":feature:characters:detail:res"),
        target(":core:theme:res"),
    )
)