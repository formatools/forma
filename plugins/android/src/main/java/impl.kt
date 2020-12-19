import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validate
import tools.forma.validation.validator
import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.applyFeatures
import tools.forma.android.target.AndroidUtilTarget
import tools.forma.android.target.ApiTarget
import tools.forma.android.target.DataBindingAdapterTarget
import tools.forma.android.target.DataBindingTarget
import tools.forma.android.target.ImplTarget
import tools.forma.android.target.LibraryTarget
import tools.forma.android.target.ResourcesTarget
import tools.forma.android.target.TestUtilTarget
import tools.forma.android.target.UtilTarget
import tools.forma.android.target.WidgetTarget
import tools.forma.deps.applyDependencies
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.deps.FormaDependency
import tools.forma.deps.NamedDependency
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

fun Project.impl(
    configurationKey: FormaConfigurationKey = DefaultConfigurationKey,
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {

    disallowResources()

    validate(ImplTarget)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        selfValidator = validator(ImplTarget)
    )
    applyFeatures(
        androidLibraryFeatureDefinition(configurationKey, libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition(configurationKey)
    )

    applyDependencies(
        validator = validator(
            ApiTarget,
            AndroidUtilTarget,
            TestUtilTarget,
            UtilTarget,
            LibraryTarget,
            DataBindingAdapterTarget,
            DataBindingTarget,
            ResourcesTarget,
            WidgetTarget //TODO: do we need widget targets here?
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma[configurationKey].repositories,
        configurationFeatures = kaptConfigurationFeature(configurationKey)
    )
}

