import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.feature.processorConfigurationFeatures
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.validation.disallowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.core.target.TargetType
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Non-UI Android library target bound to an arbitrary [TargetType].
 *
 * Used by [androidUtil] (pre-defined `android-util`) and by Path B derived DSLs
 * (e.g. dogfood `roomAndroidUtil`) so plugin identity stays on the type while the
 * AGP/util wiring is shared. Call sites of derived DSLs remain attributes-only —
 * never pass plugin ids here.
 */
fun Project.androidUtilTarget(
    type: TargetType,
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
) {
    disallowResources()

    AndroidTargetRegistry.selfValidator(type).asValidator().validate(target)
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
    applyTargetPlugins(type, configurationFeatures = processors)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(type).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        configurationFeatures = processors
    )
}
