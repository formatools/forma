androidRes(
    packageName = "tools.forma.sample.feature.home.res",
    dependencies = deps(
        google.material,
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":core:theme:res")
    )
)
