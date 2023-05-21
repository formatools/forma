package tools.forma.config

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.RepositoryHandler

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
data class FormaConfiguration(
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
object FormaConfigurationStore : ConfigurationStore<FormaConfiguration> {
    private lateinit var _configuration: FormaConfiguration
    override val configuration: FormaConfiguration get() = _configuration

    override fun store(configuration: FormaConfiguration) {
        _configuration = configuration
    }
}

interface ConfigurationStore<T : Any> {
    val configuration: T
    fun store(configuration: T)
}
