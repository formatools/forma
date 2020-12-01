package tools.forma.android.dependencies

import Forma
import transitiveDeps

/**
 * Data Bindind dependencies declarations for internal use
 */
object dataBinding {
    val viewBinding = transitiveDeps(
        "androidx.databinding:viewbinding:${Forma.configuration.agpVersion}"
    )

    val runtime = transitiveDeps(
        "androidx.databinding:databinding-runtime:${Forma.configuration.agpVersion}"
    )

    val common = transitiveDeps(
        "androidx.databinding:databinding-common:${Forma.configuration.agpVersion}"
    )

    val adapters = transitiveDeps(
        "androidx.databinding:databinding-adapters:${Forma.configuration.agpVersion}"
    )
}
