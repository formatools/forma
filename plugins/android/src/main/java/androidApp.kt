import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.ApplicationTargetTemplate
import tools.forma.android.target.ImplTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.TestUtilTargetTemplate
import tools.forma.android.target.UiLibraryTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.ViewBindingTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.android.target.ComposeWidgetTargetTemplate
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validate
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import org.gradle.api.Project
import tools.forma.android.feature.kaptConfigurationFeature

/**
 * Root Android application library (feature composition, not the APK entry).
 *
 * Project-deps allowlist is Dagger2-friendly: may wire feature [api]/[impl],
 * shared libraries/utils, and UI building blocks. Cannot depend on other `app`
 * or `binary` targets (composition stays single-rooted via [androidBinary]).
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
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
) {

    disallowResources()

    target.validate(ApplicationTargetTemplate)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        compose = compose,
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(
            ApiTargetTemplate,
            ImplTargetTemplate,
            LibraryTargetTemplate,
            UtilTargetTemplate,
            AndroidUtilTargetTemplate,
            TestUtilTargetTemplate,
            ResourcesTargetTemplate,
            ViewBindingTargetTemplate,
            WidgetTargetTemplate,
            ComposeWidgetTargetTemplate,
            UiLibraryTargetTemplate,
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        configurationFeatures = kaptConfigurationFeature()
    )
}
