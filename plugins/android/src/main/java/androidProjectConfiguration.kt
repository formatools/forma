import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.embeddedKotlinVersion
import org.gradle.kotlin.dsl.repositories
import org.gradle.plugin.use.PluginDependency
import tools.forma.android.utils.register
import tools.forma.config.AndroidProjectSettings
import tools.forma.config.FormaSettingsStore
import tools.forma.config.PluginInfoStore
import tools.forma.config.SettingsStore

// TODO: add docs for every fun param
/**
 * Configures common values for whole forma modules.
 *
 * @param minSdk is min android sdk
 * @param targetSdk is target android sdk
 * @param compileSdk SDK version used to compile Android App
 * @param repositories is a function that configures repositories for project
 * @param javaVersionCompatibility is a java version that will be used for targetCompatibility and
 *   sourceCompatibility versions
 * @param mandatoryOwners is a flag that enables mandatory owners for all modules
 * @param extraPlugins is a list of extra plugins that will be applied to project
 */
fun ScriptHandlerScope.androidProjectConfiguration(
    project: Project,
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
    kotlinVersion: String = embeddedKotlinVersion,
    agpVersion: String,
    repositories: RepositoryHandler.() -> Unit = {},
    compose: Boolean = false,
    javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8, // Java/Kotlin configuration
    mandatoryOwners: Boolean = false,
    vectorDrawablesUseSupportLibrary: Boolean = false,
    extraPlugins: List<Any> = emptyList()
) {
    buildScriptConfiguration(
        this, extraPlugins
                // Add Correct AGP version to build classpath
                + "com.android.tools.build:gradle:$agpVersion"
    )

    /** Default Android project clean task implementation */
    with(project) {
        tasks.register(
            "clean",
            Delete::class
        ) { delete(layout.buildDirectory) }
    }

    val configuration =
        AndroidProjectSettings(
            minSdk = minSdk,
            targetSdk = targetSdk,
            compileSdk = compileSdk,
            // we don't need check properties for exist, we read it successfully in forma
            // configuration
            kotlinVersion = kotlinVersion,
            agpVersion = agpVersion,
            repositories = repositories,
            javaVersionCompatibility = javaVersionCompatibility,
            mandatoryOwners = mandatoryOwners,
            compose = compose,
            vectorDrawablesUseSupportLibrary = vectorDrawablesUseSupportLibrary
        )

    Forma.store(configuration)
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
    compose: Boolean = false,
    vectorDrawablesUseSupportLibrary: Boolean = true,
) {

    /** Default Android project clean task implementation */
    tasks.register("clean", Delete::class) { delete(project.layout.buildDirectory) }

    val configuration =
        AndroidProjectSettings(
            minSdk = minSdk,
            targetSdk = targetSdk,
            compileSdk = compileSdk,
            // we don't need check properties for exist, we read it successfully in forma
            // configuration
            kotlinVersion = kotlinVersion,
            agpVersion = agpVersion,
            repositories = repositories,
            javaVersionCompatibility = javaVersionCompatibility,
            mandatoryOwners = mandatoryOwners,
            compose = compose,
            vectorDrawablesUseSupportLibrary = vectorDrawablesUseSupportLibrary
        )

    Forma.store(configuration)
}

val buildScriptConfiguration: ScriptHandlerScope.(List<Any>) -> Unit = { classpathDeps ->
    // TODO pass repositories configuration
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpathDeps.forEach {
            when (it) {
                is Provider<*> ->
                    (it.get() as? PluginDependency)?.let { plugin ->
                        classpath("${plugin.pluginId}:${plugin.version.strictVersion}")
                    }
                        ?: throw IllegalArgumentException("Only plugin providers are supported")
                else -> classpath(it)
            }
        }
    }
}

/** Singleton project configuration store TODO remove */
object Forma :
    SettingsStore<AndroidProjectSettings> by FormaSettingsStore,
    PluginInfoStore by FormaSettingsStore
