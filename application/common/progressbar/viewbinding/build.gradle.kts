viewBinding(
    packageName = "tools.forma.sample.common.progressbar.viewbinding",

    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":common:extensions:android-util"),
        target(":common:progressbar:res")
    )
)