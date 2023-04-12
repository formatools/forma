package tools.forma.android.feature

import androidJunitRunner
import com.android.build.gradle.LibraryExtension
import org.gradle.kotlin.dsl.get
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
    val viewBinding: Boolean = false,
    val dataBinding: Boolean = false,
    val selfValidator: Validator = validator(LibraryTargetTemplate)
)

fun androidLibraryFeatureDefinition(
    featureConfiguration: AndroidLibraryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = LibraryExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, feature, _, formaConfiguration ->
        with(extension) {
            namespace = feature.packageName
            compileSdk = formaConfiguration.compileSdk

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            sourceSets["main"].java.srcDirs("src/main/kotlin")
            sourceSets["test"].java.srcDirs("src/test/kotlin")
            sourceSets["androidTest"].java.srcDirs("src/androidTest/kotlin")

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            buildFeatures.dataBinding = feature.dataBinding
            buildFeatures.viewBinding = feature.viewBinding
        }
    }
)
