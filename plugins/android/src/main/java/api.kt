import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.validation.disallowResources
import tools.forma.owners.Owner
import tools.forma.owners.NoOwner
import org.gradle.api.Project
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.core.FormaDependency
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Feature **API** surface (JVM). Dagger2-friendly public contracts only.
 *
 * May depend on other `api` and pure `library` modules. Must not depend on
 * `impl`, Android UI (`widget` / `viewbinding` / `res`), or utils that pull
 * Android into the contract layer.
 */
fun Project.api(
    packageName: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.api).asValidator().validate(target)
    applyFeatures(
        kotlinFeatureDefinition()
    )
    applyTargetPlugins(AndroidTargetTypes.api)

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.api).asValidator(),
        dependencies = dependencies
    )
}
