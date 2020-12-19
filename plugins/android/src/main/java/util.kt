import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.UtilTarget
import tools.forma.android.target.LibraryTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validate
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.applyDependencies
import tools.forma.deps.FormaDependency
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

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
    configurationKey: FormaConfigurationKey = DefaultConfigurationKey,
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    validate(UtilTarget)

    applyFeatures(
        kotlinFeatureDefinition(configurationKey)
    )

    applyDependencies(
        validator = validator(UtilTarget, LibraryTarget),
        dependencies = dependencies,
        testDependencies = testDependencies,
        repositoriesConfiguration = Forma[configurationKey].repositories
    )
}
