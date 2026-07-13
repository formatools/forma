import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Shared Android library (not a feature [impl]).
 *
 * Dagger-friendly: may depend on other libraries/utils/res and feature [api]
 * contracts, but **not** on [impl] (implementations are composed only at
 * [androidApp] / [androidBinary]). Also disallows widgets/viewbinding as
 * project deps (use dedicated targets).
 */
fun Project.androidLibrary(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
): TargetBuilder {
    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.library).asValidator().validate(target)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        compose = compose,
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.library).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        configurationFeatures = kaptConfigurationFeature()
    )

    return TargetBuilder(this)
}
