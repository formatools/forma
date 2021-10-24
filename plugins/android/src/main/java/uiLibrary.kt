import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.UiLibraryTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.FormaDependency
import tools.forma.deps.NamedDependency
import tools.forma.deps.applyDependencies
import tools.forma.validation.validate
import tools.forma.validation.validator

/**
 * Android UI Library target - this can be used to share common ui code for impl and widget target.
 * Remember, widget cannot depends on library target, but can depends on uiLibrary.
 * You can use it to store you own ui libraries in project, f.e. recycler delegates library
 */
fun Project.uiLibrary(
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
    target.validate(UiLibraryTargetTemplate)
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
            // Better to have ability to use widget while we experiment with dependency rules
            WidgetTargetTemplate,
            UtilTargetTemplate,
            AndroidUtilTargetTemplate,
            ResourcesTargetTemplate
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma.configuration.repositories,
        configurationFeatures = kaptConfigurationFeature()
    )

    return TargetBuilder(this)
}

