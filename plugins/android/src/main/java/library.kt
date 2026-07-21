import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.feature.processorConfigurationFeatures
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Can't depend on api\impl
 */
fun Project.library(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    testDependencies: NamedDependency = emptyDependency()
) {
    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.jvmLibrary).asValidator().validate(target)
    registerFormaLayout(packageName)

    applyFeatures(
        kotlinFeatureDefinition()
    )
    applyTargetPlugins(AndroidTargetTypes.jvmLibrary)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.jvmLibrary).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        configurationFeatures = processorConfigurationFeatures()
    )
}
