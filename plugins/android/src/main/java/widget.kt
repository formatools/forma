import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidUtilTarget
import tools.forma.android.target.UtilTarget
import tools.forma.android.target.WidgetTarget
import tools.forma.android.target.ResourcesTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.validation.validate
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.applyDependencies
import tools.forma.deps.FormaDependency
import tools.forma.deps.NamedDependency
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

// TODO only allow layouts and view classes
fun Project.widget(
    configurationKey: FormaConfigurationKey = DefaultConfigurationKey,
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
    validate(WidgetTarget)

    val featureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        testInstrumentationRunnerClass = testInstrumentationRunner,
        consumerMinificationFiles = consumerMinificationFiles,
        manifestPlaceholders = manifestPlaceholders
    )

    applyFeatures(
        androidLibraryFeatureDefinition(configurationKey, featureConfiguration),
        kotlinAndroidFeatureDefinition(configurationKey)
    )

    applyDependencies(
        validator = validator(
            WidgetTarget,
            UtilTarget,
            AndroidUtilTarget,
            ResourcesTarget
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        repositoriesConfiguration = Forma[configurationKey].repositories
    )
}
