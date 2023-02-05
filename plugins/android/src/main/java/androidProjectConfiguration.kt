@file:Suppress("ObjectPropertyName")

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.repositories
import tools.forma.android.config.FormaConfiguration
import tools.forma.android.utils.register

// TODO: add docs for every fun param
/**
 * Configures common values for whole forma modules.
 * @param minSdk is min android sdk
 * @param targetSdk is target android sdk
 * @param compileSdk SDK version used to compile Android App
 * @param versionCode is version code of the App
 * @param validateManifestPackages enabling validation of manifests during configuration
 * @param generateMissedManifests enabling generation missing manifests during configuration
 */
fun ScriptHandlerScope.androidProjectConfiguration(
    project: Project,
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
    repositories: RepositoryHandler.() -> Unit = {},
    dataBinding: Boolean = false,
    validateManifestPackages: Boolean = false,
    generateMissedManifests: Boolean = false,
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
            kotlinVersion = properties["forma.kotlinVersion"]!!.toString(),
            agpVersion = properties["forma.agpVersion"]!!.toString(),
            repositories = repositories,
            dataBinding = dataBinding,
            generateMissedManifests = generateMissedManifests,
            validateManifestPackages = validateManifestPackages,
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
    repositories: RepositoryHandler.() -> Unit = {},
    dataBinding: Boolean = false,
    validateManifestPackages: Boolean = false,
    generateMissedManifests: Boolean = false,
    javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8, // Java/Kotlin configuration
    mandatoryOwners: Boolean = false,
) {
//    Forma.buildScriptConfiguration(this, extraPlugins)
//    with(project) {

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
            kotlinVersion = properties["forma.kotlinVersion"]!!.toString(),
            agpVersion = properties["forma.agpVersion"]!!.toString(),
            repositories = repositories,
            dataBinding = dataBinding,
            generateMissedManifests = generateMissedManifests,
            validateManifestPackages = validateManifestPackages,
            javaVersionCompatibility = javaVersionCompatibility,
            mandatoryOwners = mandatoryOwners
        )

        Forma.store(configuration)

//    }
}

/**
 * Singleton project configuration store
 */
object Forma {

    private lateinit var _configuration: FormaConfiguration
    val configuration: FormaConfiguration get() = _configuration

    fun store(configuration: FormaConfiguration) {
        _configuration = configuration
    }

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
