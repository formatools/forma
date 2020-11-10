import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.target.ApiTarget
import com.stepango.forma.target.LibraryTarget
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.owner.Owner
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.validation.disallowResources
import org.gradle.api.Project

fun Project.api(
    owner: Owner = NoOwner,
    dependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    validate(ApiTarget)
    applyFeatures(
        kotlinFeatureDefinition()
    )
    applyDependencies(
        validator = validator(ApiTarget, LibraryTarget),
        dependencies = dependencies
    )
}

