package tools.forma.android.utils

import com.android.build.gradle.internal.CompileOptions
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.DefaultConfig
import tools.forma.android.config.FormaConfiguration
import org.gradle.api.NamedDomainObjectContainer

data class BuildConfiguration(
    val type: BuildTypeConfiguration = BuildTypeConfiguration("debug"),
    val signingConfig: SigningConfiguration = SigningConfiguration("keystore"),
    val fieldConfig: List<Pair<String, String>> = emptyList()
)

data class BuildTypeConfiguration(
    val name: String,
    val appIdSuffix: String = "",
    val minifyEnabled: Boolean = false,
    val defaultProguardRules: String = "proguard-android-optimize.txt",
    val otherProguardRules: Set<String> = setOf("proguard-rules.pro"),
    val debuggable: Boolean = true,
    val testCoverageEnabled: Boolean = false
)

data class SigningConfiguration(
    val keystore: String
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
//    config.buildTypes.forEach { (name, lambda) ->
//        lambda(getByName(name))
//    }
}
