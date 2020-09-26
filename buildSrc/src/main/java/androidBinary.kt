import com.stepango.forma.BinaryModule
import com.stepango.forma.Validator
import com.stepango.forma.feature.AndroidBinaryFeatureConfiguration
import com.stepango.forma.feature.androidBinaryFeatureDefinition
import com.stepango.forma.validator
import org.gradle.api.Project

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
    validator: Validator = validator(BinaryModule)
) {
    val binaryFeatureConfiguration = AndroidBinaryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )
    applyFeatures(
        androidBinaryFeatureDefinition(binaryFeatureConfiguration)
    )
    applyDependencies(
        validator = binaryFeatureConfiguration.dependencyValidator,
        projectDependencies = projectDependencies,
        kotlinStdLib = false
    )
    validator.validate(this)
}

