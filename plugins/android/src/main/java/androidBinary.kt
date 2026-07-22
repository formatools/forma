import org.gradle.api.Project
import tools.forma.android.feature.AndroidBinaryFeatureConfiguration
import tools.forma.android.feature.androidBinaryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.utils.BuildConfiguration
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
 * @param packageName Application package name / `applicationId` + namespace
 * @param owner owner of the target, team responsible for maintenance
 * @param versionCode Android `versionCode` for this APK (required; per-binary)
 * @param versionName Android `versionName` for this APK (required; per-binary)
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
) {

    disallowResources()

    val selfV = AndroidTargetRegistry.selfValidator(AndroidTargetTypes.binary).asValidator()
    selfV.validate(target)
    // applicationId / namespace; composition root often has only a manifest.
    registerFormaLayout(packageName, requirePackageSourceDir = false)
    val binaryFeatureConfiguration = AndroidBinaryFeatureConfiguration(
        packageName,
        versionCode,
        versionName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        selfValidator = selfV,
        compose = compose,
    )
    applyFeatures(
        androidBinaryFeatureDefinition(binaryFeatureConfiguration)
    )
    applyTargetPlugins(AndroidTargetTypes.binary)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.binary).asValidator(),
        dependencies = dependencies
    )
}
