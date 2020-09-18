import com.stepango.forma.Api
import com.stepango.forma.FormaConfiguration
import com.stepango.forma.Validator
import com.stepango.forma.validator
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
    validator: Validator = validator(Api)
) {
    validator.validate(this)
    apply(plugin = "kotlin")
    applyDependencies(
        formaConfiguration = formaConfiguration,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        validator = validator
    )
}
