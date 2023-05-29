package tools.forma.android.utils

import com.android.build.gradle.internal.CompileOptions
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.DefaultConfig
import tools.forma.config.AndroidProjectSettings
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
    androidProjectSettings: AndroidProjectSettings,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    minSdk = androidProjectSettings.minSdk
    targetSdk = androidProjectSettings.targetSdk

    testInstrumentationRunner = testInstrumentationRunnerClass
    consumerProguardFiles(*consumerMinificationFiles.toTypedArray())
    manifestPlaceholders(manifestPlaceholders)

    vectorDrawables.useSupportLibrary = androidProjectSettings.vectorDrawablesUseSupportLibrary
}

internal fun CompileOptions.applyFrom(config: AndroidProjectSettings) {
    sourceCompatibility = config.javaVersionCompatibility
    targetCompatibility = config.javaVersionCompatibility
}

internal fun NamedDomainObjectContainer<BuildType>.applyFrom(config: BuildConfiguration) {
    config.buildTypes.forEach { (name, lambda) ->
        lambda(getByName(name))
    }
}
