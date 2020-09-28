@file:Suppress("ObjectPropertyName")

import com.stepango.forma.FormaConfiguration
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.RepositoryHandler

object Forma {
    private lateinit var _configuration: FormaConfiguration
    val configuration: FormaConfiguration get() = _configuration

    fun configure(
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
        _configuration = FormaConfiguration(
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
}

