import com.stepango.forma.feature.AndroidBinaryFeatureConfiguration
import com.stepango.forma.feature.androidBinaryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.module.BinaryModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.EmptyValidator
import com.stepango.forma.validation.validate
import org.gradle.api.Project

/**
 * Application entry point. Manifest + minimal set of resources + root android project dependency only.
 * No library dependencies, no source code.
 */
fun Project.androidBinary(
    packageName: String,
    projectDependencies: ProjectDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    validate(BinaryModule)
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
        validator = EmptyValidator,
        projectDependencies = projectDependencies
    )
}

