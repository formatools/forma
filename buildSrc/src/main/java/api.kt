import com.stepango.forma.Api
import com.stepango.forma.FormaConfiguration
import com.stepango.forma.Validator
import com.stepango.forma.throwProjectValidationError
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.api(
    dependencies: NamedDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency()
) = api(
    dependencies,
    projectDependencies,
    Forma.configuration
)

@Suppress("UnstableApiUsage")
internal fun Project.api(
    dependencies: NamedDependency,
    projectDependencies: ProjectDependency,
    formaConfiguration: FormaConfiguration,
    validator: Validator = ApiValidator
) {
    apply(plugin = "kotlin")
    applyDependencies(
        formaConfiguration = formaConfiguration,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        validator = validator
    )

    validator.validate(this)
}

object ApiValidator : Validator {
    override fun validate(project: Project) {
        if (!Api.validate(project)) {
            throwProjectValidationError(project, Api)
        }
    }
}