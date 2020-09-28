@file:Suppress("UnstableApiUsage")
package com.stepango.forma.feature

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.stepango.forma.module.BinaryModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyFrom
import com.stepango.forma.validation.Validator
import com.stepango.forma.validation.validator

data class AndroidBinaryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val selfValidator: Validator = validator(BinaryModule)
)

fun androidBinaryFeatureDefinition(
    featureConfiguration: AndroidBinaryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.application",
    pluginExtension = BaseAppModuleExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, feature, _, formaConfiguration ->
        with(extension) {
            compileSdkVersion(formaConfiguration.compileSdk)

            defaultConfig.applicationId = feature.packageName

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            /**
             * DataBindings needs to be enabled for root project
             */
            buildFeatures.dataBinding = formaConfiguration.dataBinding
            buildFeatures.viewBinding = formaConfiguration.viewBinding

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