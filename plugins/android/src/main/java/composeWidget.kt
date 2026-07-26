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
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Compose UI component target (Jetpack Compose counterpart of [widget]).
 *
 * Always enables Compose (`buildFeatures.compose` + compiler extension).
 * May depend on other `compose-widget` / `widget` modules so View and Compose
 * UI can coexist (GH #96).
 */
fun Project.composeWidget(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    androidTestDependencies: FormaDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    val selfV = AndroidTargetRegistry.selfValidator(AndroidTargetTypes.composeWidget).asValidator()
    selfV.validate(target)
    registerFormaLayout(packageName)

    val featureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        testInstrumentationRunnerClass = testInstrumentationRunner,
        consumerMinificationFiles = consumerMinificationFiles,
        manifestPlaceholders = manifestPlaceholders,
        compose = true,
        selfValidator = selfV
    )

    applyFeatures(
        androidLibraryFeatureDefinition(featureConfiguration),
        kotlinAndroidFeatureDefinition()
    )
    applyTargetPlugins(AndroidTargetTypes.composeWidget)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.composeWidget).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}
