import com.stepango.forma.feature.AndroidBinaryFeatureConfiguration
import com.stepango.forma.feature.androidBinaryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.target.BinaryTarget
import com.stepango.forma.owner.Owner
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.EmptyValidator
import com.stepango.forma.validation.validate
import com.stepango.forma.owner.NoOwner
import org.gradle.api.Project

/**
 * Android Binary target - application entry point.
 *
 * Manifest + minimal set of resources + root android project dependency only.
 * No library dependencies, no source code.
 *
 * @param packageName - Application package name, used for publishing
 * @param owner - owner of the target, team responsible for maintenance
 * @param dependencies - list of external and project dependencies for the target
 * @param buildConfiguration - Android Gradle Plugin configuration DSL
 * @param testInstrumentationRunner - class name used for instrumentation tests execution
 * @param consumerMinificationFiles - Proguard/R8 minification files list
 * @param manifestPlaceholders - placeholders ot be injected in manifest
 */
fun Project.androidBinary(
    packageName: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    validate(BinaryTarget)
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
        dependencies = dependencies
    )
}

