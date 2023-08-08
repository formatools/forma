@file:Suppress("UnstableApiUsage")

package tools.forma.android.feature

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import tools.forma.android.target.BinaryTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.applyFrom
import tools.forma.validation.Validator
import tools.forma.validation.validator

data class AndroidBinaryFeatureConfiguration(
    val packageName: String,
    val versionCode: Int,
    val versionName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val selfValidator: Validator = validator(BinaryTargetTemplate)
)

fun androidBinaryFeatureDefinition(
    featureConfiguration: AndroidBinaryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.application",
    pluginExtension = BaseAppModuleExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, configuration, _, formaConfiguration ->
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

            /**
             * Workaround for compilation time issue with duplicate names for META-INF files
             * https://stackoverflow.com/questions/44342455/more-than-one-file-was-found-with-os-independent-path-meta-inf-license
             */
            packagingOptions {
                exclude("META-INF/DEPENDENCIES")
                exclude("META-INF/LICENSE")
                exclude("META-INF/LICENSE.txt")
                exclude("META-INF/license.txt")
                exclude("META-INF/NOTICE")
                exclude("META-INF/NOTICE.txt")
                exclude("META-INF/notice.txt")
                exclude("META-INF/ASL2.0")
                exclude("META-INF/*.kotlin_module")
            }
        }
    }
)