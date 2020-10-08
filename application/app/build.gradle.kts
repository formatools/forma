androidBinary(
    packageName = "com.stepango.blockme",
    dependencies = deps(
        androidx.appcompat,
        androidx.core_ktx,
        androidx.navigation_fragment_ktx,
        google.material,
        google.dagger
    ) + deps(
        project(":root-library"),
        project(":common:extensions:android-util"),
        project(":core:mvvm:library"),
        project(":core:di:library")
    )
)