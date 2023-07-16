import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.target.TestUtilTargetTemplate
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
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
    target.validate(LibraryTargetTemplate)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = validator(UtilTargetTemplate, TestUtilTargetTemplate),
        dependencies = dependencies,
        testDependencies = testDependencies,
        repositoriesConfiguration = Forma.settings.repositories,
        configurationFeatures = kaptConfigurationFeature()
    )
}
