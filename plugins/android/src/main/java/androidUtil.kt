import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.validation.disallowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.feature.processorConfigurationFeatures
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * TODO
 *
 * External AAR Libraries extensions - usage:
 * Add libraries to dependencies e.g. appcompat
 * retrofit dependency cluster should include extension
 *
 * ```
 * val retrofit = transitiveDeps(
 *     "com.google.design:material:2.8.0"
 * ) + deps(
 *     project("material-android-util")
 * )
 * ```
 *
 */
fun Project.androidUtil(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
) {

    disallowResources()

    //TODO unify with util, use androidJar dependency
    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.androidUtil).asValidator().validate(target)
    registerFormaLayout(packageName)

    val androidFeatureConfig = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        compose = compose,
    )

    applyFeatures(
        androidLibraryFeatureDefinition(androidFeatureConfig),
        kotlinAndroidFeatureDefinition()
    )
    val processors = processorConfigurationFeatures()
    applyTargetPlugins(AndroidTargetTypes.androidUtil, configurationFeatures = processors)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.androidUtil).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        configurationFeatures = processors
    )
}
