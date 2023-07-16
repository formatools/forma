import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.TestUtilTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.FormaDependency
import tools.forma.validation.validate

fun Project.testUtil(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    target.validate(TestUtilTargetTemplate)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = validator(TestUtilTargetTemplate, UtilTargetTemplate),
        dependencies = dependencies,
        repositoriesConfiguration = Forma.settings.repositories
    )
}
