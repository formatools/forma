import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidUtilTarget
import tools.forma.android.target.UtilTarget
import tools.forma.android.target.WidgetTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.validation.validate
import tools.forma.android.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.dependencies.applyDependencies

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
    validate(WidgetTarget)

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
            WidgetTarget,
            UtilTarget,
            AndroidUtilTarget
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}
