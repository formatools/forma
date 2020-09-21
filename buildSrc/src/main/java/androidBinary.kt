import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.stepango.forma.Binary
import com.stepango.forma.EmptyValidator
import com.stepango.forma.Validator
import com.stepango.forma.validator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * Application entry point. Manifest + minimal set of resources + root android project com.stepango.forma.internal.getDependency only.
 * No library dependencies, no source code.
 */
fun Project.androidBinary(
    packageName: String,
    projectDependencies: ProjectDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    validator: Validator = validator(Binary),
    dataBinding: Boolean = false
) {
    val binaryFeatureConfiguration = AndroidBinaryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        dataBinding = dataBinding
    )
    applyFeatures(
        androidBinaryFeatureDefinition(binaryFeatureConfiguration)
    )
    // TODO Replace this flow for call KotlinAndroid, KotlinKapt definitions into applyFeatures {...}
    if (dataBinding) {
        apply(plugin = "kotlin-android")
        apply(plugin = "kotlin-kapt")
    }
    applyDependencies(
        validator = binaryFeatureConfiguration.dependencyValidator,
        projectDependencies = projectDependencies,
        dataBinding = dataBinding
    )
    validator.validate(this)
}

data class AndroidBinaryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val dependencyValidator: Validator = EmptyValidator,
    val selfValidator: Validator = validator(Binary),
    val dataBinding: Boolean = false
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

            buildFeatures.dataBinding = featureConfiguration.dataBinding
        }
    }
)

