@file:Suppress("ObjectPropertyName")

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.repositories
import tools.forma.config.FormaConfiguration
import tools.forma.config.FormaConfigurationStore
import tools.forma.android.utils.register

// TODO: add docs for every fun param
/**
 * Configures common values for whole forma modules.
 * @param minSdk is min android sdk
 * @param targetSdk is target android sdk
 * @param compileSdk SDK version used to compile Android App
 * @param repositories is a function that configures repositories for project
 * @param dataBinding is a flag that enables databinding
 * @param javaVersionCompatibility is a java version that will be used for targetCompatibility and sourceCompatibility versions
 * @param mandatoryOwners is a flag that enables mandatory owners for all modules
 * @param extraPlugins is a list of extra plugins that will be applied to project
 */
fun ScriptHandlerScope.androidProjectConfiguration(
    project: Project,
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
    kotlinVersion: String,
    agpVersion: String,
    repositories: RepositoryHandler.() -> Unit = {},
    dataBinding: Boolean = false,
    javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8, // Java/Kotlin configuration
    mandatoryOwners: Boolean = false,
    extraPlugins: List<Any>
) {
    Forma.buildScriptConfiguration(this, extraPlugins)
    with(project) {

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
            // we don't need check properties for exist, we read it successfully in forma configuration
            kotlinVersion = kotlinVersion,
            agpVersion = agpVersion,
            repositories = repositories,
            dataBinding = dataBinding,
            javaVersionCompatibility = javaVersionCompatibility,
            mandatoryOwners = mandatoryOwners
        )

        Forma.store(configuration)

    }
}

@Deprecated("Old approach to configuration, use ScriptHandlerScope Extension")
fun Project.androidProjectConfiguration(
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
    kotlinVersion: String,
    agpVersion: String,
    repositories: RepositoryHandler.() -> Unit = {},
    dataBinding: Boolean = false,
    validateManifestPackages: Boolean = false,
    generateMissedManifests: Boolean = false,
    javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8, // Java/Kotlin configuration
    mandatoryOwners: Boolean = false,
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
        // we don't need check properties for exist, we read it successfully in forma configuration
        kotlinVersion = kotlinVersion,
        agpVersion = agpVersion,
        repositories = repositories,
        dataBinding = dataBinding,
        javaVersionCompatibility = javaVersionCompatibility,
        mandatoryOwners = mandatoryOwners
    )

    Forma.store(configuration)
}

/**
 * Singleton project configuration store
 */
object Forma {

    val configuration: FormaConfiguration get() = FormaConfigurationStore.configuration

    fun store(configuration: FormaConfiguration) = FormaConfigurationStore.store(configuration)

    val buildScriptConfiguration: ScriptHandlerScope.(List<Any>) -> Unit = { classpathDeps ->
        // TODO pass repositories configuration
        repositories {
            google()
            mavenCentral()
        }
        dependencies {
            classpathDeps.forEach { classpath(it) }
        }
    }
}
