import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.BuildType
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.getDefaultProguardFile(name: String) = the<BaseExtension>().getDefaultProguardFile(name)

data class BuildConfiguration(
    val buildTypes: Map<String, BuildType.() -> Unit> = emptyMap()
)

data class BuildTypeConfiguration(
    val name: String,
    val useDefaultMinificationRules: Boolean = false,
    val minificationRulesFileName: String = "proguard-rules.pro"
)

val androidJunitRunner = "androidx.test.runner.AndroidJUnitRunner"

@Suppress("UnstableApiUsage")
internal fun Project.applyConfiguration(
    configuration: Configuration,
    appId: String,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    the<BaseAppModuleExtension>().run {
        compileSdkVersion(configuration.compileSdk)

        defaultConfig {
            applicationId = appId
            minSdkVersion(configuration.minSdk)
            targetSdkVersion(configuration.targetSdk)
            versionCode = configuration.versionCode
            versionName = configuration.versionName

            testInstrumentationRunner = testInstrumentationRunnerClass
            consumerProguardFiles(*consumerMinificationFiles.toTypedArray())
            manifestPlaceholders(manifestPlaceholders)
        }

        buildTypes {
            buildConfiguration.buildTypes.forEach { (name, lambda) ->
                lambda(getByName(name))
            }
        }
        compileOptions {
            sourceCompatibility = configuration.javaVersionCompatibility
            targetCompatibility = configuration.javaVersionCompatibility
        }
    }
}