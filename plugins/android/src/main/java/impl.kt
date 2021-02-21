import tools.forma.android.config.BuildConfiguration
import tools.forma.android.config.None
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validator
import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.applyFeatures
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.DataBindingAdapterTargetTemplate
import tools.forma.android.target.DataBindingTargetTemplate
import tools.forma.android.target.ImplTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.TestUtilTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.deps.applyDependencies
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.deps.FormaDependency
import tools.forma.deps.NamedDependency
import tools.forma.validation.validate

fun Project.impl(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = None(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {

    disallowResources()

    target.validate(ImplTargetTemplate)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        selfValidator = validator(ImplTargetTemplate)
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(
            ApiTargetTemplate,
            AndroidUtilTargetTemplate,
            TestUtilTargetTemplate,
            UtilTargetTemplate,
            LibraryTargetTemplate,
            DataBindingAdapterTargetTemplate,
            DataBindingTargetTemplate,
            ResourcesTargetTemplate,
            WidgetTargetTemplate //TODO: do we need widget targets here?
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma.configuration.repositories,
        configurationFeatures = kaptConfigurationFeature()
    )
}

