package tools.forma.android.utils

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CompileOptions
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.LibraryDefaultConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import tools.forma.config.AndroidProjectSettings
import tools.forma.config.coreLibraryDesugaringDependencyOrNull

/**
 * Shared build-type configuration for Android library and application targets.
 *
 * **Signing configs are not here** (F-097): APK signing is owned by [androidBinary]
 * via dedicated `signingConfigs` / `buildTypeSigning` attrs so library DSLs cannot
 * grow a parallel shopping API. Build-type lambdas may still set minify, proguard,
 * buildConfigField, etc.
 */
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
            // AGP 9: libraries no longer expose targetSdk on LibraryDefaultConfig
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
    isCoreLibraryDesugaringEnabled = config.coreLibraryDesugaring
}

/**
 * When [AndroidProjectSettings.coreLibraryDesugaring] is true, add the desugar JDK libs
 * artifact to AGP's `coreLibraryDesugaring` configuration (F-098 / GH #103).
 * No-op when disabled — does not add a dependency.
 *
 * Call from every Android AGP feature definition after [CompileOptions.applyFrom].
 */
internal fun applyCoreLibraryDesugaring(project: Project, settings: AndroidProjectSettings) {
    val dependency = coreLibraryDesugaringDependencyOrNull(settings) ?: return
    project.dependencies.add("coreLibraryDesugaring", dependency)
}

@Suppress("UNCHECKED_CAST")
internal fun NamedDomainObjectContainer<*>.applyFrom(config: BuildConfiguration) {
    config.buildTypes.forEach { (name, lambda) ->
        val buildType = getByName(name) as BuildType
        lambda(buildType)
    }
}
