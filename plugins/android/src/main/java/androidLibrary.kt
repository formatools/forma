import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.LibraryTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.utils.BuildConfiguration
import tools.forma.validation.EmptyValidator
import tools.forma.validation.validate
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.FormaDependency
import tools.forma.deps.NamedDependency
import org.gradle.api.Project
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.deps.applyDependencies
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

/**
 * TODO Can't depend on widgets, cant depend on databindings
 */
fun Project.androidLibrary(
    configurationKey: FormaConfigurationKey = DefaultConfigurationKey,
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    validate(LibraryTarget)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )
    applyFeatures(
        androidLibraryFeatureDefinition(configurationKey, libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition(configurationKey)
    )

    applyDependencies(
        validator = EmptyValidator,
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma[configurationKey].repositories,
        configurationFeatures = kaptConfigurationFeature(configurationKey)
    )
}

