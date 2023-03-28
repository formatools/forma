androidRes(
    packageName = "com.stepango.blockme.feature.home.res",
    dependencies = deps(
        google.material,
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":core:theme:res")
    )
)
