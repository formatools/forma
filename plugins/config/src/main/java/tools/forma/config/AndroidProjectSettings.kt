package tools.forma.config

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency
import tools.forma.config.FormaSettingsStore.configurations
import tools.forma.config.FormaSettingsStore.dependencies
import tools.forma.config.FormaSettingsStore.dependencyPlugins

/**
 * Limitations:
 * No BuildConfig Support
 */
// TODO use jacoco by default
// TODO publishing
// TODO owners
// TODO modules dependency restrictions
// TODO api for module dependencies
// TODO should we mark dependencies?
// TODO how we make sure dependency sources is correct?
// TODO Should we use extension class as the last arg for complex config?
// TODO Should we restrict build flavor per module?
// TODO Configuration override
// TODO 3rd party plugins
// TODO generalize and split into code configuration and android specific configuration
data class AndroidProjectSettings(
    val minSdk: Int,
    val targetSdk: Int,
    val compileSdk: Int,
    val kotlinVersion: String,
    val agpVersion: String,
    val repositories: RepositoryHandler.() -> Unit,
    // Databinding is Application level feature, android_binary will be inferring dataBinding flag, developers does not need to know about
    val dataBinding: Boolean,
    val compose: Boolean,
    val vectorDrawablesUseSupportLibrary: Boolean,
    val javaVersionCompatibility: JavaVersion, // Java/Kotlin configuration
    val mandatoryOwners: Boolean
)

/**
 * Singleton project configuration store, used by internal plugins
 */
object FormaSettingsStore : SettingsStore<AndroidProjectSettings>, PluginInfoStore {
    private lateinit var _settings: AndroidProjectSettings
    override val settings: AndroidProjectSettings get() = _settings

    override fun store(configuration: AndroidProjectSettings) {
        _settings = configuration
    }

    override val plugins: MutableMap<Provider<PluginDependency>, PluginConfiguration> = mutableMapOf()
    override val dependencyPlugins: MutableMap<Provider<out Dependency>, PluginConfiguration> = mutableMapOf()
    override val dependencies: MutableMap<Provider<out Dependency>, DependencyConfiguration> = mutableMapOf()
    override val configurations: MutableMap<String, Provider<PluginDependency>> = mutableMapOf()
}

interface SettingsStore<T : Any> {
    val settings: T
    fun store(configuration: T)
}

data class PluginConfiguration(val plugin: Provider<PluginDependency>, val configuration: String)
data class DependencyConfiguration(val dependency: Provider<out Dependency>, val configuration: String)

interface PluginInfoStore {
    val plugins: MutableMap<Provider<PluginDependency>, PluginConfiguration>
    val dependencyPlugins: MutableMap<Provider<out Dependency>, PluginConfiguration>
    val dependencies: MutableMap<Provider<out Dependency>, DependencyConfiguration>
    val configurations: MutableMap<String, Provider<PluginDependency>>

    fun registerConfiguration(configuration: String, plugin: Provider<PluginDependency>, vararg dependencies: Provider<out Dependency>) {
        registerPlugin(plugin, configuration)

        dependencies.forEach { dependency ->
            registerDependency(dependency, configuration)
        }
    }
    fun registerPlugin(plugin: Provider<PluginDependency>, configuration: String){
        configurations[configuration] = plugin
        plugins[plugin] = PluginConfiguration(plugin, configuration)
    }

    fun registerDependency(dependency: Provider<out Dependency>, configuration: String){
        if (configurations.containsKey(configuration)) {
            dependencyPlugins[dependency] = plugins[configurations[configuration]!!]!!
            dependencies[dependency] = DependencyConfiguration(dependency, configuration)
        } else {
            throw Exception("Configuration $configuration is not registered using registerPlugin")
        }
    }

    fun pluginFor(dependency: Provider<out Dependency>): PluginConfiguration? = dependencyPlugins[dependency]
}
