import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.ImplTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.TestUtilTargetTemplate
import tools.forma.android.target.UiLibraryTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.ViewBindingTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.validation.validate
import tools.forma.validation.validator

fun Project.impl(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    viewBinding: Boolean = false,
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {

    target.validate(ImplTargetTemplate)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        selfValidator = validator(ImplTargetTemplate),
        viewBinding = viewBinding
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
            UiLibraryTargetTemplate,
            ResourcesTargetTemplate,
            ViewBindingTargetTemplate,
            WidgetTargetTemplate,
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma.settings.repositories,
        configurationFeatures = kaptConfigurationFeature()
    )
}

