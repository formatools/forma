@file:Suppress("ClassName", "MemberVisibilityCanBePrivate")

import tools.forma.config.FormaSettingsStore

val configuration = FormaSettingsStore.settings

object dataBinding {
    val viewBinding = transitiveDeps(
        "androidx.databinding:viewbinding:${configuration.agpVersion}"
    )

    val runtime = transitiveDeps(
        "androidx.databinding:databinding-runtime:${configuration.agpVersion}"
    )

    val common = transitiveDeps(
        "androidx.databinding:databinding-common:${configuration.agpVersion}"
    )

    val adapters = transitiveDeps(
        "androidx.databinding:databinding-adapters:${configuration.agpVersion}"
    )
}

