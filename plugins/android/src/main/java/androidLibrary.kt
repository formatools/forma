import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.TestUtilTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.validation.validate
import tools.forma.validation.validator

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
): TargetBuilder {
    target.validate(LibraryTargetTemplate)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(
            LibraryTargetTemplate,
            UtilTargetTemplate,
            AndroidUtilTargetTemplate,
            TestUtilTargetTemplate,
            ResourcesTargetTemplate,
            ApiTargetTemplate,
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma.settings.repositories,
        configurationFeatures = kaptConfigurationFeature()
    )

    return TargetBuilder(this)
}
