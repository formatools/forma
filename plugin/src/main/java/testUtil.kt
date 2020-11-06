import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.target.TestUtilTarget
import com.stepango.forma.target.UtilTarget
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project

fun Project.testUtil(
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency()
) {
    validate(TestUtilTarget)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = validator(TestUtilTarget, UtilTarget),
        dependencies = dependencies
    )
}
