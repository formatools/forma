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
    formaConfiguration: FormaConfiguration,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    the<LibraryExtension>().run {
        compileSdkVersion(formaConfiguration.compileSdk)

        defaultConfig.applyFrom(
            formaConfiguration,
            testInstrumentationRunnerClass,
            consumerMinificationFiles,
            manifestPlaceholders
        )

        buildTypes.applyFrom(buildConfiguration)
        compileOptions.applyFrom(formaConfiguration)
    }
}

@Suppress("UnstableApiUsage")
internal fun Project.applyAppConfiguration(
    formaConfiguration: FormaConfiguration,
    appId: String,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    the<BaseAppModuleExtension>().run {
        compileSdkVersion(formaConfiguration.compileSdk)

        defaultConfig.applicationId = appId

        defaultConfig.applyFrom(
            formaConfiguration,
            testInstrumentationRunnerClass,
            consumerMinificationFiles,
            manifestPlaceholders
        )

        buildTypes.applyFrom(buildConfiguration)
        compileOptions.applyFrom(formaConfiguration)
    }
}

internal fun DefaultConfig.applyFrom(
    formaConfiguration: FormaConfiguration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    minSdkVersion(formaConfiguration.minSdk)
    targetSdkVersion(formaConfiguration.targetSdk)
    versionCode = formaConfiguration.versionCode
    versionName = formaConfiguration.versionName

    testInstrumentationRunner = testInstrumentationRunnerClass
    consumerProguardFiles(*consumerMinificationFiles.toTypedArray())
    manifestPlaceholders(manifestPlaceholders)
}

internal fun CompileOptions.applyFrom(config: FormaConfiguration) {
    sourceCompatibility = config.javaVersionCompatibility
    targetCompatibility = config.javaVersionCompatibility
}

internal fun NamedDomainObjectContainer<BuildType>.applyFrom(config: BuildConfiguration) {
    config.buildTypes.forEach { (name, lambda) ->
        lambda(getByName(name))
    }
}