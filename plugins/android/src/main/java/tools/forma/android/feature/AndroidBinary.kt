@file:Suppress("UnstableApiUsage")

package tools.forma.android.feature

import com.android.build.api.dsl.ApplicationExtension
import tools.forma.android.target.BinaryTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.applyFrom
import tools.forma.validation.Validator
import tools.forma.validation.validator

/**
 * AGP application feature config for [androidBinary].
 *
 * [versionCode] / [versionName] are **per-binary** (F-092) — never read from
 * project-global [tools.forma.config.AndroidProjectSettings].
 */
data class AndroidBinaryFeatureConfiguration(
    val packageName: String,
    val versionCode: Int,
    val versionName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
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

            buildTypes.applyFrom(configuration.buildConfiguration)
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
