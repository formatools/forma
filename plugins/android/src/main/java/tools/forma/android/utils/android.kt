package tools.forma.android.utils

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CompileOptions
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.LibraryDefaultConfig
import org.gradle.api.NamedDomainObjectContainer
import tools.forma.config.AndroidProjectSettings

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
    when (this) {
        is ApplicationDefaultConfig -> targetSdk = androidProjectSettings.targetSdk
        is LibraryDefaultConfig -> {
            @Suppress("DEPRECATION")
            targetSdk = androidProjectSettings.targetSdk
            if (consumerMinificationFiles.isNotEmpty()) {
                consumerProguardFiles(*consumerMinificationFiles.toTypedArray())
            }
        }
    }

    testInstrumentationRunner = testInstrumentationRunnerClass
    addManifestPlaceholders(manifestPlaceholders)

    vectorDrawables.useSupportLibrary = androidProjectSettings.vectorDrawablesUseSupportLibrary
}

internal fun CompileOptions.applyFrom(config: AndroidProjectSettings) {
    sourceCompatibility = config.javaVersionCompatibility
    targetCompatibility = config.javaVersionCompatibility
}

@Suppress("UNCHECKED_CAST")
internal fun NamedDomainObjectContainer<*>.applyFrom(config: BuildConfiguration) {
    config.buildTypes.forEach { (name, lambda) ->
        val buildType = getByName(name) as BuildType
        lambda(buildType)
    }
}
