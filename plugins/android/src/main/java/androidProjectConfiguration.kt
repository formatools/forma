@file:Suppress("ObjectPropertyName")

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import tools.forma.android.config.FormaConfiguration
import tools.forma.android.utils.register

fun Project.androidProjectConfiguration(
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
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
        // we don't need check properties for exist, we read it successfully in forma configuration
        kotlinVersion = properties["forma.kotlinVersion"]!!.toString(),
        agpVersion = properties["forma.agpVersion"]!!.toString(),
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

    private lateinit var _configuration: FormaConfiguration
    val configuration: FormaConfiguration get() = _configuration

    fun store(configuration: FormaConfiguration) {
        _configuration = configuration
    }
}
