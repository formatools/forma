import com.stepango.forma.Api
import com.stepango.forma.FormaConfiguration
import com.stepango.forma.Validator
import com.stepango.forma.validateName
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
    validator: Validator = ApiNameValidator
) {
    apply(plugin = "kotlin")
    applyDependencies(
        formaConfiguration = formaConfiguration,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        validator = ApiDepsValidator
    )

    validator.validate(this)
}

private object ApiNameValidator : Validator {
    override fun validate(project: Project) {
        validateName(project.name, Api)
    }
}

private object ApiDepsValidator : Validator {
    override fun validate(project: Project) {
        validateName(project.name, Api)
    }
}