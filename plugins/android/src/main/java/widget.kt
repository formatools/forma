import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.target.UiLibraryTargetTemplate
import tools.forma.deps.applyDependencies
import tools.forma.deps.FormaDependency
import tools.forma.deps.NamedDependency
import tools.forma.validation.validate

// TODO only allow layouts and view classes
fun Project.widget(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    target.validate(WidgetTargetTemplate)

    val featureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        testInstrumentationRunnerClass = testInstrumentationRunner,
        consumerMinificationFiles = consumerMinificationFiles,
        manifestPlaceholders = manifestPlaceholders
    )

    applyFeatures(
        androidLibraryFeatureDefinition(featureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(
            UiLibraryTargetTemplate,
            WidgetTargetTemplate,
            UtilTargetTemplate,
            AndroidUtilTargetTemplate,
            ResourcesTargetTemplate
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma.configuration.repositories
    )
}
