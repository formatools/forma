@file:Suppress("UnstableApiUsage")
package tools.forma.android.feature

import androidJunitRunner
import com.android.build.gradle.LibraryExtension
import tools.forma.android.config.*
import tools.forma.android.target.NativeTarget
import tools.forma.android.utils.applyFrom
import tools.forma.validation.Validator
import tools.forma.validation.validator

data class AndroidNativeConfiguration(
    val packageName: String,
    val buildSystem: NdkBuildSystem,
    val abi: Set<NdkAbi> = emptySet(),
    val selfValidator: Validator = validator(NativeTarget)
)

fun androidNativeDefinition(
    configuration: AndroidNativeConfiguration
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = LibraryExtension::class,
    featureConfiguration = configuration,
    configuration = { extension, feature, _, formaConfiguration ->
        with(extension) {
            compileSdkVersion(formaConfiguration.compileSdk)

            defaultConfig.applyFrom(
                formaConfiguration,
                androidJunitRunner,
                emptySet(),
                emptyMap()
            )

            val buildSystem = configuration.buildSystem

            defaultConfig.applyFrom(configuration.abi)

            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            when (buildSystem) {
                is CMake -> {
                    externalNativeBuild.applyFrom(buildSystem)
                    defaultConfig.externalNativeBuild.applyFrom(buildSystem)
                }
                is NdkBuild -> {
                    externalNativeBuild.applyFrom(buildSystem)
                    defaultConfig.externalNativeBuild.applyFrom(buildSystem)
                }
                else -> throw IllegalStateException("")
            }

            compileOptions.applyFrom(formaConfiguration)
        }
    }
)
