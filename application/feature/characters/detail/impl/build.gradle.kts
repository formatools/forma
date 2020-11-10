impl(
    packageName = "com.stepango.blockme.feature.characters.detail.impl",
    dependencies = deps(
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation,
        androidx.lifecycle_viewmodel_ktx,
        androidx.lifecycle_extensions,
        androidx.recyclerview,
        androidx.swiperefreshlayout,
        androidx.paging,
        google.material,
        google.dagger,
        jakewharton.timber,
        kotlinx.coroutines_core

    ) + deps(
        project(":feature:characters:core:api")

    ) + deps (
        project(":core:di:library"),
        project(":core:theme:android-util"),
        project(":core:mvvm:library"),
        project(":core:network:library"),
        project(":core:navigation:library")

    ) + deps(
        project(":common:util"),
        project(":common:extensions:android-util"),
        project(":common:extensions:databinding-adapter"),
        project(":common:progressbar:databinding")
    ),

    dataBinding = true
)