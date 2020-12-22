import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.UtilTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.applyDependencies
import tools.forma.deps.FormaDependency
import tools.forma.validation.validate

/**
 * TODO - Can't be used without library
 *
 * External Libraries extensions - usage:
 * Add libraries to dependencies e.g. retrofit
 * retrofit dependency cluster should include extension
 *
 * ```
 * val retrofit = transitiveDeps(
 *     "com.square.retrofit:retrofit:2.8.0"
 * ) + deps(
 *     project("retrofit-util")
 * )
 * ```
 * Can't depend on api\impl
 */
fun Project.util(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    target.validate(UtilTargetTemplate)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = validator(UtilTargetTemplate, LibraryTargetTemplate),
        dependencies = dependencies,
        testDependencies = testDependencies,
        repositoriesConfiguration = Forma.configuration.repositories
    )
}
