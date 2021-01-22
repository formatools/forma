package tools.forma.android.plugin

import kotlin.reflect.KClass

data class PluginConfiguration<TPluginExtension : Any>(
    val extensionClass: KClass<TPluginExtension>,
    val configuration: (TPluginExtension.() -> Unit)
)

inline fun <reified TPluginExtension : Any> pluginConfiguration(noinline configuration: (TPluginExtension.() -> Unit)) =
    PluginConfiguration(TPluginExtension::class, configuration)