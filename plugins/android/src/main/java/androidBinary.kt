import org.gradle.api.Project
import tools.forma.android.feature.AndroidBinaryFeatureConfiguration
import tools.forma.android.feature.androidBinaryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.ApplicationTargetTemplate
import tools.forma.android.target.BinaryTargetTemplate
import tools.forma.android.target.ImplTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.TestUtilTargetTemplate
import tools.forma.android.target.UiLibraryTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.ViewBindingTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.android.target.ComposeWidgetTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.validation.disallowResources
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.validation.validate
import tools.forma.validation.validator

/**
 * Android Binary target — application entry point (single APK).
 *
 * Composition root for Dagger graphs: may depend on root [androidApp], feature
 * [api]/[impl], and shared libraries/utils. Project-dep types are restricted
 * (no longer [tools.forma.validation.EmptyValidator]).
 *
 * @param packageName Application package name, used for publishing
 * @param owner owner of the target, team responsible for maintenance
 * @param dependencies list of external and project dependencies for the target
 * @param buildConfiguration Android Gradle Plugin configuration DSL
 * @param testInstrumentationRunner class name used for instrumentation tests execution
 * @param consumerMinificationFiles Proguard/R8 minification files list
 * @param manifestPlaceholders placeholders to be injected in manifest
 */
fun Project.androidBinary(
    packageName: String,
    owner: Owner = NoOwner,
    versionCode: Int,
    versionName: String,
    dependencies: FormaDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
): TargetBuilder {

    disallowResources()

    target.validate(BinaryTargetTemplate)

    val binaryFeatureConfiguration = AndroidBinaryFeatureConfiguration(
        packageName,
        versionCode,
        versionName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        compose = compose,
    )
    applyFeatures(
        androidBinaryFeatureDefinition(binaryFeatureConfiguration)
    )

    applyDependencies(
        validator = validator(
            ApplicationTargetTemplate,
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
        dependencies = dependencies
    )

    return TargetBuilder(this)
}
