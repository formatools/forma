import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.processorConfigurationFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.utils.BuildConfiguration
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.validation.asValidator
import tools.forma.validation.validate


/**
 * Feature **implementation** (Android library + optional view binding / kapt / Compose).
 *
 * Dagger2-friendly boundaries:
 * - **May** depend on feature `api` contracts, shared libraries/utils, and UI
 *   building blocks (`res`, `viewbinding`, `widget`, `compose-widget`, `ui-library`).
 * - **Must not** depend on other `impl` modules — feature graphs compose only
 *   at [androidApp] / [androidBinary] so implementations stay independent.
 *
 * @param compose enable Jetpack Compose for this target; defaults to project-wide
 *   [androidProjectConfiguration] `compose` flag.
 */
fun Project.impl(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    viewBinding: Boolean = false,
    compose: Boolean = Forma.settings.compose,
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {

    val selfV = AndroidTargetRegistry.selfValidator(AndroidTargetTypes.impl).asValidator()
    selfV.validate(target)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        selfValidator = selfV,
        viewBinding = viewBinding,
        compose = compose
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )
    applyTargetPlugins(AndroidTargetTypes.impl)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.impl).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        configurationFeatures = processorConfigurationFeatures()
    )
}
