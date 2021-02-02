dataBinding(
    packageName = "com.stepango.blockme.common.progressbar.databinding",

    dependencies = deps(
        androidx.appcompat,
        androidx.constraintlayout
    ) + deps(
        target(":common:extensions:android-util"),
        target(":common:extensions:databinding-adapter"),
        target(":common:progressbar:res")
    )
)