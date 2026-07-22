import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.processorConfigurationFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.validation.disallowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Root Android application library (feature composition, not the APK entry).
 *
 * Project-deps allowlist is Dagger2-friendly: may wire feature [api]/[impl],
 * shared libraries/utils, and UI building blocks. Cannot depend on other `app`
 * or `binary` targets (composition stays single-rooted via [androidBinary]).
 *
 * **Not an APK:** this target uses AGP `com.android.library`. Do **not** put
 * `versionCode` / `versionName` here — set them on [androidBinary] (F-092 / GH #82).
 * Multiple binaries in one project each carry their own version identity.
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

    val selfV = AndroidTargetRegistry.selfValidator(AndroidTargetTypes.app).asValidator()
    selfV.validate(target)
    registerFormaLayout(packageName)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        selfValidator = selfV,
        compose = compose,
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )
    applyTargetPlugins(AndroidTargetTypes.app)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.app).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        configurationFeatures = processorConfigurationFeatures()
    )
}
