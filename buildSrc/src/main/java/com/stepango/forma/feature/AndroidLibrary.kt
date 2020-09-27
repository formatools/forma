package com.stepango.forma.feature

import androidJunitRunner
import com.android.build.gradle.LibraryExtension
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyFrom
import com.stepango.forma.validation.EmptyValidator
import com.stepango.forma.validation.Validator
import com.stepango.forma.validation.validator
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

data class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration = BuildConfiguration(),
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val dependencyValidator: Validator = EmptyValidator,
    val selfValidator: Validator = validator(LibraryModule),
    val dataBinding: Boolean = false
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

            buildFeatures.dataBinding = feature.dataBinding
        }
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            kotlinOptions.jvmTarget = formaConfiguration.javaVersionCompatibility.toString()
        }
    }
)