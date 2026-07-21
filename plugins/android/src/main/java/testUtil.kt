import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.validation.disallowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.validation.asValidator
import tools.forma.validation.validate

fun Project.testUtil(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.testUtil).asValidator().validate(target)
    registerFormaLayout(packageName)

    applyFeatures(
        kotlinFeatureDefinition()
    )
    applyTargetPlugins(AndroidTargetTypes.testUtil)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.testUtil).asValidator(),
        dependencies = dependencies
    )
}
