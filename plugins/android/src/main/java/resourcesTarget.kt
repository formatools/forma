import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.validation.onlyAllowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.core.target.TargetType
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Resources-only Android library target bound to an arbitrary [TargetType].
 *
 * Used by [androidRes] (pre-defined `res`) and by Path B derived DSLs (e.g. sample
 * `navigationRes`) so plugin identity stays on the type while the AGP/res wiring is shared.
 * Call sites of derived DSLs remain attributes-only — never pass plugin ids here.
 */
fun Project.resourcesTarget(
    type: TargetType,
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
) {
    onlyAllowResources()

    AndroidTargetRegistry.selfValidator(type).asValidator().validate(target)
    // packageName drives AGP namespace; res trees are not package source dirs.
    registerFormaLayout(packageName, requirePackageSourceDir = false)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        manifestPlaceholders = manifestPlaceholders
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )
    applyTargetPlugins(type)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(type).asValidator(),
        dependencies = dependencies
    )
}
