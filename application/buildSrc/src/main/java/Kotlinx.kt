object kotlinx {
    val coroutines_core = deps(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.jetbrains.coroutines}".dep,
        kotlin.stdlib()
    )
    val coroutines_android = deps(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.jetbrains.coroutines}".dep,
        coroutines_core
    )
}