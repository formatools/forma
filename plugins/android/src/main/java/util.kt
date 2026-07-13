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
import tools.forma.deps.core.FormaDependency
import tools.forma.validation.asValidator
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

    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.util).asValidator().validate(target)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.util).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}
