package com.stepango.forma.feature

import androidJunitRunner
import com.android.build.gradle.LibraryExtension
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyFrom
import com.stepango.forma.validation.Validator
import com.stepango.forma.validation.validator

data class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration = BuildConfiguration(),
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val selfValidator: Validator = validator(LibraryModule),
    val dataBinding: Boolean = false,
    val viewBinding: Boolean = false
)

fun androidLibraryFeatureDefinition(
    featureConfiguration: AndroidLibraryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = LibraryExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, feature, project, formaConfiguration ->
        with(extension) {
            compileSdkVersion(formaConfiguration.compileSdk)

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            buildFeatures.dataBinding = feature.dataBinding
            buildFeatures.viewBinding = feature.viewBinding
        }
    }
)