import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.LibraryTarget
import tools.forma.android.target.TestUtilTarget
import tools.forma.android.target.UtilTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.validation.validate
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.dependencies.applyDependencies

/**
 * Can't depend on api\impl
 */
fun Project.library(
    dependencies: FormaDependency = emptyDependency(),
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    testDependencies: NamedDependency = emptyDependency()
) {
    validate(LibraryTarget)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = validator(UtilTarget, TestUtilTarget),
        dependencies = dependencies,
        testDependencies = testDependencies,
        repositoriesConfiguration = Forma.configuration.repositories
    )
}