object google {
    val material = deps(
        "com.google.android.material:material:${versions.google.material}".dep,
        androidx.appcompat,
        androidx.cardview,
        androidx.core,
        androidx.annotation,
        androidx.legacy_ui,
        androidx.legacy_utils,
        androidx.recyclerview,
        androidx.transition
    )

    val dagger = deps(
        "javax.inject:javax.inject:1".dep,
        "com.google.dagger:dagger:${versions.google.dagger}".dep,
        "com.google.dagger:dagger-compiler:${versions.google.dagger}".kapt
    )

    val play = deps(
        "com.google.android.play:core:${versions.google.play_core}".dep
    )

    val gson = deps(
        "com.google.code.gson:gson:${versions.google.gson}".dep
    )

    val firebase = transitivePlatform("com.google.firebase:firebase-bom:26.1.0")

}