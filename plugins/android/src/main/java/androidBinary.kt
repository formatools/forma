import org.gradle.api.Project
import tools.forma.android.feature.AndroidBinaryFeatureConfiguration
import tools.forma.android.feature.androidBinaryFeatureDefinition
import tools.forma.android.feature.processorConfigurationFeatures
import tools.forma.android.feature.applyFeatures
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.FormaProductFlavor
import tools.forma.android.utils.FormaSigningConfig
import tools.forma.android.validation.disallowResources
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Android Binary target — application entry point (single APK).
 *
 * Composition root for Dagger graphs: may depend on root [androidApp], feature
 * [api]/[impl], and shared libraries/utils. Project-dep types are restricted
 * (no longer [tools.forma.validation.EmptyValidator]).
 *
 * **Version identity (F-092 / GH #82):** [versionCode] and [versionName] are
 * **required call-site attributes** on each binary. They are **not** project-global
 * (`AndroidProjectSettings` / `androidProjectConfiguration`) so a monorepo can ship
 * multiple APKs with independent versions. [androidApp] is an AGP library
 * (composition shell), not an APK — it does not take version attrs (Gradle/AGP
 * only materialize `defaultConfig.versionCode`/`versionName` on
 * `com.android.application`).
 *
 * **APK signing (F-097 / GH #51):** [signingConfigs] and [buildTypeSigning] are
 * **binary-only** attrs. Forma registers named configs on AGP
 * `ApplicationExtension.signingConfigs`, then assigns each build type via
 * [buildTypeSigning] (build type name → signing config name). Libraries do not
 * take a signing API. Do not use raw module-level `android { signingConfigs }` as
 * the supported path.
 *
 * **Product flavors (F-115 / NiA F7):** [productFlavors] are **binary-only** attrs
 * ([FormaProductFlavor]). [BuildConfiguration] stays build-types only. Empty list
 * (default) keeps a single unflavored APK. v1 does **not** put flavors on library
 * targets — multi-module `demoImplementation` edges stay a documented gap.
 *
 * @param packageName Application package name / `applicationId` + namespace
 * @param owner owner of the target, team responsible for maintenance
 * @param versionCode Android `versionCode` for this APK (required; per-binary)
 * @param versionName Android `versionName` for this APK (required; per-binary)
 * @param dependencies list of external and project dependencies for the target
 * @param buildConfiguration Android Gradle Plugin build-type configuration DSL
 * @param signingConfigs named [FormaSigningConfig] entries for this APK (optional)
 * @param buildTypeSigning map of build type name → signing config name (optional)
 * @param productFlavors product flavors for this APK (optional; empty = unflavored)
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
    signingConfigs: Map<String, FormaSigningConfig> = emptyMap(),
    buildTypeSigning: Map<String, String> = emptyMap(),
    productFlavors: List<FormaProductFlavor> = emptyList(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
) {

    disallowResources()

    val selfV = AndroidTargetRegistry.selfValidator(AndroidTargetTypes.binary).asValidator()
    selfV.validate(target)
    // applicationId / namespace; composition root often has only a manifest.
    registerFormaLayout(packageName, requirePackageSourceDir = false)
    val binaryFeatureConfiguration = AndroidBinaryFeatureConfiguration(
        packageName = packageName,
        versionCode = versionCode,
        versionName = versionName,
        buildConfiguration = buildConfiguration,
        testInstrumentationRunnerClass = testInstrumentationRunner,
        consumerMinificationFiles = consumerMinificationFiles,
        manifestPlaceholders = manifestPlaceholders,
        signingConfigs = signingConfigs,
        buildTypeSigning = buildTypeSigning,
        productFlavors = productFlavors,
        selfValidator = selfV,
        compose = compose,
    )
    applyFeatures(
        androidBinaryFeatureDefinition(binaryFeatureConfiguration)
    )
    val processors = processorConfigurationFeatures()
    applyTargetPlugins(AndroidTargetTypes.binary, configurationFeatures = processors)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.binary).asValidator(),
        dependencies = dependencies,
        configurationFeatures = processors,
    )
}
