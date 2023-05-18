viewBinding(
    packageName = "com.stepango.blockme.common.progressbar.viewbinding",

    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":common:extensions:android-util"),
        target(":common:progressbar:res")
    )
)