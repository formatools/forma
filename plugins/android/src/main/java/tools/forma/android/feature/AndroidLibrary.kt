package tools.forma.android.feature

import androidJunitRunner
import com.android.build.gradle.LibraryExtension
import java.io.File
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.applyFrom
import tools.forma.validation.Validator
import tools.forma.validation.validator

class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration = BuildConfiguration(),
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val dataBinding: Boolean = false,
    val generateManifest: Boolean = true,
    val selfValidator: Validator = validator(LibraryTargetTemplate)
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
            if (feature.generateManifest) {
                /**
                 * Some naive caching here, file name equals package name
                 * We create new file only if package is changed or on first build
                 */
                with(File(project.buildDir, "tmp/manifest/${feature.packageName}.xml")) {
                    if (!exists()) {
                        parentFile.mkdirs()
                        createNewFile()
                        writeText(
                            """<?xml version="1.0" encoding="utf-8"?>
                           <manifest package="${feature.packageName}"/>
                        """.trimIndent()
                        )
                    }
                    sourceSets {
                        /**
                         * Manifest file resolved during configuration,
                         * so we need to create file before we get into plugin is configured
                         */
                        getByName("main").manifest.srcFile(path)
                    }
                }
            }

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            buildFeatures.dataBinding = feature.dataBinding
        }
    }
)