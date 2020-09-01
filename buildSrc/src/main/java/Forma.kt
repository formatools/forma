import com.stepango.forma.FormaConfiguration
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.RepositoryHandler

object Forma {
    private lateinit var _Forma_configuration: FormaConfiguration
    val configuration: FormaConfiguration get() = _Forma_configuration

    fun configure(
        minSdk: Int,
        targetSdk: Int,
        compileSdk: Int,
        kotlinVersion: String,
        agpVersion: String,
        versionCode: Int,
        versionName: String,
        repositories: RepositoryHandler.() -> Unit,
        javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8 // Java/Kotlin configuration
    ) {
        _Forma_configuration = FormaConfiguration(
            minSdk = minSdk,
            targetSdk = targetSdk,
            compileSdk = compileSdk,
            kotlinVersion = kotlinVersion,
            agpVersion = agpVersion,
            versionCode = versionCode,
            versionName = versionName,
            repositories = repositories,
            javaVersionCompatibility = javaVersionCompatibility
        )
    }
}

