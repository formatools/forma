import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.feature.processorConfigurationFeatures
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
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
 * Pure JVM library target bound to an arbitrary [TargetType].
 *
 * Used by [library] (pre-defined `jvm.library`) and by Path B derived DSLs
 * (e.g. dogfood `protobufLibrary`) so plugin identity stays on the type while the
 * Kotlin/JVM wiring is shared. Call sites of derived DSLs remain attributes-only —
 * never pass plugin ids here.
 */
fun Project.libraryTarget(
    type: TargetType,
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    testDependencies: FormaDependency = emptyDependency(),
) {
    AndroidTargetRegistry.selfValidator(type).asValidator().validate(target)
    registerFormaLayout(packageName)

    applyFeatures(
        kotlinFeatureDefinition()
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
