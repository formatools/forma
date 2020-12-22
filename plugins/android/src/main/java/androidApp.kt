import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.ApplicationTargetTemplate
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.utils.BuildConfiguration
import tools.forma.validation.EmptyValidator
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validate
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.FormaDependency
import tools.forma.deps.NamedDependency
import tools.forma.deps.applyDependencies
import org.gradle.api.Project
import tools.forma.android.feature.kaptConfigurationFeature

/**
 * TODO Can't depend on widgets, cant depend on databindings
 */
fun Project.androidApp(
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

    disallowResources()

    target.validate(ApplicationTargetTemplate)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )


    applyDependencies(
        validator = EmptyValidator,
        dependencies = dependencies,
        repositoriesConfiguration = Forma.configuration.repositories,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        configurationFeatures = kaptConfigurationFeature()
    )
}

