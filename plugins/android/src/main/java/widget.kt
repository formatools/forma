import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

// TODO only allow layouts and view classes
/**
 * Custom View / UI component target.
 *
 * Compose UI lives in [composeWidget] (separate suffix) so View and Compose
 * graphs stay distinguishable while still allowed to depend on each other.
 */
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
    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.widget).asValidator().validate(target)

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
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.widget).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}
