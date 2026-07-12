import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ComposeWidgetTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.UiLibraryTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.validation.validate
import tools.forma.validation.validator

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
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    target.validate(ComposeWidgetTargetTemplate)

    val featureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        testInstrumentationRunnerClass = testInstrumentationRunner,
        consumerMinificationFiles = consumerMinificationFiles,
        manifestPlaceholders = manifestPlaceholders,
        compose = true,
        selfValidator = validator(ComposeWidgetTargetTemplate)
    )

    applyFeatures(
        androidLibraryFeatureDefinition(featureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(
            UiLibraryTargetTemplate,
            ComposeWidgetTargetTemplate,
            WidgetTargetTemplate,
            UtilTargetTemplate,
            AndroidUtilTargetTemplate,
            ResourcesTargetTemplate
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}
