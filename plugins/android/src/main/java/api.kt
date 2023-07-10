import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.validation.validator
import tools.forma.owner.Owner
import tools.forma.owner.NoOwner
import tools.forma.android.validation.disallowResources
import org.gradle.api.Project
import tools.forma.deps_core.applyDependencies
import tools.forma.deps_core.FormaDependency
import tools.forma.validation.validate

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
        dependencies = dependencies,
        repositoriesConfiguration = Forma.settings.repositories
    )
}

