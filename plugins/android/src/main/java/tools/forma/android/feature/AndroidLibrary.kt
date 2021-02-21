package tools.forma.android.feature

import androidJunitRunner
import com.android.build.gradle.LibraryExtension
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.config.BuildConfiguration
import tools.forma.android.config.None
import tools.forma.android.utils.applyFrom
import tools.forma.validation.Validator
import tools.forma.validation.validator
import java.io.File
import org.gradle.api.Project

class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration = None(),
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val viewBinding: Boolean = false,
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
            maybeGenerateManifest(project, feature)

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

private fun LibraryExtension.maybeGenerateManifest(
    project: Project,
    feature: AndroidLibraryFeatureConfiguration
) {
    val manifestFile = manifestFile(project.buildDir, feature.packageName)
    populateManifest(manifestFile, feature.packageName)
    sourceSets {
        /**
         * Manifest file resolved during configuration,
         * so we need to create file before we get into plugin is configured
         */
        getByName("main").manifest.srcFile(manifestFile.path)
    }
}

fun manifestFile(buildDir: File, packageName: String) =
    File(buildDir, "tmp/manifest/${packageName}.xml")

/**
 * Manifest file generated and added it to sourceSet
 * @param buildDir - project's build dir
 * @param packageName - will be injected to manifest template
 * @return path to generated file
 */
private fun populateManifest(
    manifestFile: File,
    packageName: String
): String {
    /**
     * Some naive caching here, file name equals package name
     * We create new file only if package is changed or on first build
     */
    with(manifestFile) {
        if (!exists()) {
            parentFile.mkdirs()
            createNewFile()
            writeText(
                """<?xml version="1.0" encoding="utf-8"?>
                           <manifest package="$packageName"/>
                """.trimIndent()
            )
        }
    }
    return manifestFile.path
}