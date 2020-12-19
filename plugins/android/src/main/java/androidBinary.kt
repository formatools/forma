import tools.forma.android.feature.AndroidBinaryFeatureConfiguration
import tools.forma.android.feature.androidBinaryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.target.BinaryTarget
import tools.forma.android.owner.Owner
import tools.forma.android.utils.BuildConfiguration
import tools.forma.validation.EmptyValidator
import tools.forma.validation.validate
import tools.forma.android.owner.NoOwner
import tools.forma.android.validation.disallowResources
import tools.forma.deps.FormaDependency
import org.gradle.api.Project
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

import tools.forma.deps.applyDependencies

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
    configurationKey: FormaConfigurationKey = DefaultConfigurationKey,
    packageName: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {

    disallowResources()

    validate(BinaryTarget)

    val binaryFeatureConfiguration = AndroidBinaryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )
    applyFeatures(
        androidBinaryFeatureDefinition(configurationKey, binaryFeatureConfiguration)
    )

    applyDependencies(
        validator = EmptyValidator,
        dependencies = dependencies,
        repositoriesConfiguration = Forma[configurationKey].repositories
    )
}

