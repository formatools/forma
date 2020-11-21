androidRes(
    packageName = "com.stepango.blockme.feature.home.res",
    dependencies = deps(
        google.material,
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        project(":core:theme:res")
    )
)