package com.stepango.forma.feature

import BuildConfiguration
import FeatureDefinition
import applyFrom
import com.android.build.gradle.LibraryExtension
import com.stepango.forma.EmptyValidator
import com.stepango.forma.LibraryModule
import com.stepango.forma.Validator
import com.stepango.forma.validator
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

data class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val dependencyValidator: Validator = EmptyValidator,
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

            if (!formaConfiguration.dataBinding && feature.dataBinding){
                //TODO better error msg
                throw IllegalArgumentException("Please enable dataBinding feature trough Forma.configura")
            }

            if (!formaConfiguration.viewBinding && feature.viewBinding){
                //TODO better error msg
                throw IllegalArgumentException("Please enable viewBinding feature trough Forma.configura")
            }

            buildFeatures.dataBinding = feature.dataBinding
            buildFeatures.viewBinding = feature.viewBinding
        }
        project.tasks.withType(KotlinCompile::class.java).all {
            kotlinOptions.jvmTarget = formaConfiguration.javaVersionCompatibility.toString()
        }
    }
)