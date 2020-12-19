package tools.forma.android.dependencies

import Forma
import transitiveDeps
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

/**
 * Data Bindind dependencies declarations for internal use
 */
object dataBinding {

    fun viewBinding(configurationKey: FormaConfigurationKey) = transitiveDeps(
        "androidx.databinding:viewbinding:${Forma[configurationKey].agpVersion}"
    )

    fun runtime(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = transitiveDeps(
        "androidx.databinding:databinding-runtime:${Forma[configurationKey].agpVersion}"
    )

    fun common(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = transitiveDeps(
        "androidx.databinding:databinding-common:${Forma[configurationKey].agpVersion}"
    )

    fun adapters(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = transitiveDeps(
        "androidx.databinding:databinding-adapters:${Forma[configurationKey].agpVersion}"
    )
}