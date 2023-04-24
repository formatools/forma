@file:Suppress("ClassName", "MemberVisibilityCanBePrivate")

import tools.forma.config.FormaConfigurationStore

object dataBinding {
    val viewBinding = transitiveDeps(
        "androidx.databinding:viewbinding:${FormaConfigurationStore.configuration.agpVersion}"
    )

    val runtime = transitiveDeps(
        "androidx.databinding:databinding-runtime:${FormaConfigurationStore.configuration.agpVersion}"
    )

    val common = transitiveDeps(
        "androidx.databinding:databinding-common:${FormaConfigurationStore.configuration.agpVersion}"
    )

    val adapters = transitiveDeps(
        "androidx.databinding:databinding-adapters:${FormaConfigurationStore.configuration.agpVersion}"
    )
}

