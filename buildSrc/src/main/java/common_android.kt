import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.CompileOptions
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

data class BuildConfiguration(
    val buildTypes: Map<String, BuildType.() -> Unit> = emptyMap()
)

data class BuildTypeConfiguration(
    val name: String,
    val useDefaultMinificationRules: Boolean = false,
    val minificationRulesFileName: String = "proguard-rules.pro"
)

@Suppress("UnstableApiUsage")
internal fun Project.applyLibraryConfiguration(
    configuration: Configuration,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    the<LibraryExtension>().run {
        compileSdkVersion(configuration.compileSdk)

        defaultConfig.applyFrom(
            configuration,
            testInstrumentationRunnerClass,
            consumerMinificationFiles,
            manifestPlaceholders
        )

        buildTypes.applyFrom(buildConfiguration)
        compileOptions.applyFrom(configuration)
    }
}

@Suppress("UnstableApiUsage")
internal fun Project.applyAppConfiguration(
    configuration: Configuration,
    appId: String,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    the<BaseAppModuleExtension>().run {
        compileSdkVersion(configuration.compileSdk)

        defaultConfig.applicationId = appId

        defaultConfig.applyFrom(
            configuration,
            testInstrumentationRunnerClass,
            consumerMinificationFiles,
            manifestPlaceholders
        )

        buildTypes.applyFrom(buildConfiguration)
        compileOptions.applyFrom(configuration)
    }
}

internal fun DefaultConfig.applyFrom(
    configuration: Configuration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    minSdkVersion(configuration.minSdk)
    targetSdkVersion(configuration.targetSdk)
    versionCode = configuration.versionCode
    versionName = configuration.versionName

    testInstrumentationRunner = testInstrumentationRunnerClass
    consumerProguardFiles(*consumerMinificationFiles.toTypedArray())
    manifestPlaceholders(manifestPlaceholders)
}

internal fun CompileOptions.applyFrom(config: Configuration) {
    sourceCompatibility = config.javaVersionCompatibility
    targetCompatibility = config.javaVersionCompatibility
}

internal fun NamedDomainObjectContainer<BuildType>.applyFrom(config: BuildConfiguration) {
    config.buildTypes.forEach { (name, lambda) ->
        lambda(getByName(name))
    }
}