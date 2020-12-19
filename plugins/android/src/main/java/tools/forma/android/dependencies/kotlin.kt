package tools.forma.android.dependencies

import dep
import deps
import Forma
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

object versions {

    object jetbrains {

        const val annotations = "20.0.0"

    }

}

object jetbrains {

    val annotations = "org.jetbrains:annotations:${versions.jetbrains.annotations}".dep

}

object kotlin {

    fun stdlib_common(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = "org.jetbrains.kotlin:kotlin-stdlib-common:${Forma[configurationKey].kotlinVersion}".dep

    fun stdlib(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = deps(
        "org.jetbrains.kotlin:kotlin-stdlib:${Forma[configurationKey].kotlinVersion}".dep,
        jetbrains.annotations,
        stdlib_common(configurationKey)
    )

    fun stdlib_jdk7(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = deps(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Forma[configurationKey].kotlinVersion}".dep,
        stdlib(configurationKey)
    )

    fun stdlib_jdk8(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = deps(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Forma[configurationKey].kotlinVersion}".dep,
        stdlib(configurationKey),
        stdlib_jdk7(configurationKey)
    )

    fun reflect(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = deps(
        "org.jetbrains.kotlin:kotlin-reflect:${Forma[configurationKey].kotlinVersion}".dep,
        stdlib(configurationKey)
    )

}
