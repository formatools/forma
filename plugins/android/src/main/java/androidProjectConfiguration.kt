@file:Suppress("ObjectPropertyName")

import tools.forma.android.config.FormaConfiguration
import tools.forma.android.utils.register
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

fun Project.androidProjectDefaultConfiguration(
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
    kotlinVersion: String,
    agpVersion: String,
    versionCode: Int,
    versionName: String,
    repositories: RepositoryHandler.() -> Unit = {},
    dataBinding: Boolean = false,
    javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8, // Java/Kotlin configuration
    mandatoryOwners: Boolean = false
) {

    /**
     * Default Android project clean task implementation
     */
    tasks.register("clean", Delete::class) {
        delete(project.buildDir)
    }

    val configuration = FormaConfiguration(
        minSdk = minSdk,
        targetSdk = targetSdk,
        compileSdk = compileSdk,
        kotlinVersion = kotlinVersion,
        agpVersion = agpVersion,
        versionCode = versionCode,
        versionName = versionName,
        repositories = repositories,
        dataBinding = dataBinding,
        javaVersionCompatibility = javaVersionCompatibility,
        mandatoryOwners = mandatoryOwners
    )

    Forma.defaultConfiguration = configuration

}

/**
 * Add configuration with specific [FormaConfigurationKey].
 * If configuration argument is null value set in [androidProjectDefaultConfiguration] will be taken.
 * Also [repositories] will be added to repositories setted in [androidProjectDefaultConfiguration]
 */
fun androidProjectConfiguration(
    configurationKey: FormaConfigurationKey,
    minSdk: Int? = null,
    targetSdk: Int? = null,
    compileSdk: Int? = null,
    kotlinVersion: String? = null,
    agpVersion: String? = null,
    versionCode: Int? = null,
    versionName: String? = null,
    repositories: (RepositoryHandler.() -> Unit)? = null,
    dataBinding: Boolean? = null,
    javaVersionCompatibility: JavaVersion? = null, // Java/Kotlin configuration
    mandatoryOwners: Boolean? = null
) {
    // TODO: should not happens because DefaultConfigurationKey is internal
    require(configurationKey != DefaultConfigurationKey)

    val defaultConfiguration = Forma.defaultConfiguration

    val combinedRepositories: RepositoryHandler.() -> Unit = {
        defaultConfiguration.repositories(this)
        repositories?.let { it(this) }
    }

    val configuration = defaultConfiguration.copy(
        minSdk = minSdk ?: defaultConfiguration.minSdk,
        targetSdk = targetSdk ?: defaultConfiguration.targetSdk,
        compileSdk = compileSdk ?: defaultConfiguration.compileSdk,
        kotlinVersion = kotlinVersion ?: defaultConfiguration.kotlinVersion,
        agpVersion = agpVersion ?: defaultConfiguration.agpVersion,
        versionCode = versionCode ?: defaultConfiguration.versionCode,
        versionName = versionName ?: defaultConfiguration.versionName,
        repositories = combinedRepositories,
        dataBinding = dataBinding ?: defaultConfiguration.dataBinding,
        javaVersionCompatibility = javaVersionCompatibility ?: defaultConfiguration.javaVersionCompatibility,
        mandatoryOwners = mandatoryOwners ?: defaultConfiguration.mandatoryOwners
    )

    Forma[configurationKey] = configuration

}

/**
 * Singleton project configuration store
 */
object Forma {

    private val configurations = mutableMapOf<Any, FormaConfiguration>()

    var defaultConfiguration: FormaConfiguration
        get() = configurations[DefaultConfigurationKey]
            ?: error("Trying to access to any project configuration, but there is no one configuration")
        set(value) {
            require(configurations[DefaultConfigurationKey] == null) { "You already have set default configuration. You must do it once." }
            configurations[DefaultConfigurationKey] = value
        }

    operator fun get(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = configurations[configurationKey]
        ?: error("Trying to access to project configuration with key $configurationKey, but it was not initialize")

    operator fun set(configurationKey: FormaConfigurationKey = DefaultConfigurationKey, configuration: FormaConfiguration) {
        configurations[configurationKey] = configuration
    }

}
