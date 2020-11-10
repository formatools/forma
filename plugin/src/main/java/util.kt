import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.target.UtilTarget
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.disallowResources
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project

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
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    validate(UtilTarget)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = validator(UtilTarget),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}
