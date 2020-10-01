@file:Suppress("ObjectPropertyName")

import com.stepango.forma.config.FormaConfiguration
import com.stepango.forma.utils.register
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete

fun Project.androidProjectConfiguration(
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
    kotlinVersion: String,
    agpVersion: String,
    versionCode: Int,
    versionName: String,
    repositories: RepositoryHandler.() -> Unit,
    dataBinding: Boolean = false,
    viewBinding: Boolean = false,
    javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8 // Java/Kotlin configuration
) {

    tasks.register("clean", Delete::class) {
        delete(project.buildDir)
    }

    Forma._configuration = FormaConfiguration(
        minSdk = minSdk,
        targetSdk = targetSdk,
        compileSdk = compileSdk,
        kotlinVersion = kotlinVersion,
        agpVersion = agpVersion,
        versionCode = versionCode,
        versionName = versionName,
        repositories = repositories,
        dataBinding = dataBinding,
        viewBinding = viewBinding,
        javaVersionCompatibility = javaVersionCompatibility
    )

}

object Forma {
    internal lateinit var _configuration: FormaConfiguration
    val configuration: FormaConfiguration get() = _configuration
}
