import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.validation.validator
import tools.forma.owners.Owner
import tools.forma.owners.NoOwner
import tools.forma.android.validation.disallowResources
import org.gradle.api.Project
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.FormaDependency
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

    target.validate(ApiTargetTemplate)
    applyFeatures(
        kotlinFeatureDefinition()
    )
    applyDependencies(
        validator = validator(ApiTargetTemplate, LibraryTargetTemplate),
        dependencies = dependencies
    )
}
