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
    // Databinding is Application level feature, android_binary will be infering dataBinding flag, developers does not need to know about
    val dataBinding: Boolean = false,
    val compose: Boolean = false,
    val vectorDrawablesUseSupportLibrary: Boolean = true,
    val javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8, // Java/Kotlin configuration
    val mandatoryOwners: Boolean
)

/**
 * Singleton project configuration store
 */
object FormaConfigurationStore {

    private lateinit var _configuration: FormaConfiguration
    val configuration: FormaConfiguration get() = _configuration

    fun store(configuration: FormaConfiguration) {
        _configuration = configuration
    }
}
