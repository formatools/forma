@file:Suppress("UnstableApiUsage")

package tools.forma.android.feature

import com.android.build.api.dsl.ApplicationExtension
import tools.forma.android.target.BinaryTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.FormaSigningConfig
import tools.forma.android.utils.applyBuildTypeSigning
import tools.forma.android.utils.applyFrom
import tools.forma.android.utils.applySigningConfigs
import tools.forma.validation.Validator
import tools.forma.validation.validator

/**
 * AGP application feature config for [androidBinary].
 *
 * [versionCode] / [versionName] are **per-binary** (F-092) — never read from
 * project-global [tools.forma.config.AndroidProjectSettings].
 *
 * [signingConfigs] / [buildTypeSigning] are **per-binary** (F-097 / GH #51) —
 * APK signing identity lives only on the composition root, not on library shells.
 */
data class AndroidBinaryFeatureConfiguration(
    val packageName: String,
    val versionCode: Int,
    val versionName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    /** Named signing configs applied to AGP `ApplicationExtension.signingConfigs`. */
    val signingConfigs: Map<String, FormaSigningConfig> = emptyMap(),
    /**
     * Build type name → signing config name. Applied after both containers exist
     * so call sites do not need AGP `SigningConfig` references inside build-type lambdas.
     */
    val buildTypeSigning: Map<String, String> = emptyMap(),
    /** Enable Jetpack Compose for the application (APK) target. */
    val compose: Boolean = false,
    val selfValidator: Validator = validator(BinaryTargetTemplate)
)

fun androidBinaryFeatureDefinition(
    featureConfiguration: AndroidBinaryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.application",
    pluginExtension = ApplicationExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, configuration, project, formaConfiguration ->
        with(extension) {
            namespace = configuration.packageName
            compileSdk = formaConfiguration.compileSdk

            defaultConfig.applicationId = configuration.packageName
            defaultConfig.versionCode = configuration.versionCode
            defaultConfig.versionName = configuration.versionName

            defaultConfig.applyFrom(
                formaConfiguration,
                configuration.testInstrumentationRunnerClass,
                configuration.consumerMinificationFiles,
                configuration.manifestPlaceholders
            )

            // Signing first so build types can rely on registered names; bridge map
            // assigns ApplicationBuildType.signingConfig after buildTypes.applyFrom.
            signingConfigs.applySigningConfigs(configuration.signingConfigs)
            buildTypes.applyFrom(configuration.buildConfiguration)
            applyBuildTypeSigning(
                buildTypes = buildTypes,
                signingConfigs = signingConfigs,
                buildTypeSigning = configuration.buildTypeSigning,
            )
            compileOptions.applyFrom(formaConfiguration)

            applyFormaBuildFeatures(
                project = project,
                defaults = formaConfiguration.buildFeatures,
                composeCompilerVersion = formaConfiguration.composeCompilerVersion,
                // No binary call-site attr — fleet-wide project default only (F-091).
                viewBinding = formaConfiguration.buildFeatures.viewBinding,
                compose = configuration.compose,
            )
        }
    }
)
