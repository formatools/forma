package tools.forma.android.utils

import com.android.build.gradle.internal.CompileOptions
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.DefaultConfig
import tools.forma.android.config.FormaConfiguration
import org.gradle.api.NamedDomainObjectContainer

data class BuildConfiguration(
    val buildTypes: Map<String, BuildType.() -> Unit> = emptyMap()
)

data class BuildTypeConfiguration(
    val name: String,
    val useDefaultMinificationRules: Boolean = false,
    val minificationRulesFileName: String = "proguard-rules.pro"
)

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

    vectorDrawables.useSupportLibrary = formaConfiguration.vectorDrawablesUseSupportLibrary
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
