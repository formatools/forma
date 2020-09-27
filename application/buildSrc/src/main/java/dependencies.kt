import org.funktionale.either.Either
import org.gradle.api.Project

/**
 * Dependency wrapper
 * TODO: inline class
 * TODO: Annotation processor flag
 */

sealed class ConfigurationType(val name: String)
object Implementation : ConfigurationType("implementation")
object CompileOnly : ConfigurationType("compileOnly")
object RuntimeOnly : ConfigurationType("runtimeOnly")
object AnnotationProcessor : ConfigurationType("annotationProcessor")
class Custom(name: String) : ConfigurationType(name)

typealias DepType = List<Either<NameSpec, ProjectSpec>>

val DepType.names get(): List<NameSpec> = this.filter { it.isLeft() }.map { it.left().get() }
val DepType.projects
    get(): List<ProjectSpec> = this.filter { it.isRight() }.map { it.right().get() }

data class NameSpec(
    val name: String,
    val config: ConfigurationType,
    val transitive: Boolean = false
)

data class ProjectSpec(val project: Project, val config: ConfigurationType)

sealed class FormaDependency(
    val dependency: DepType = emptyList(),
    val type: ConfigurationType = Implementation
)

object EmptyDependency : FormaDependency()

data class NamedDependency(val names: List<NameSpec> = emptyList()) :
    FormaDependency(names.map { Either.left(it) })

data class ProjectDependency(val projects: List<ProjectSpec> = emptyList()) :
    FormaDependency(projects.map { Either.right(it) })

data class MixedDependency(
    val names: List<NameSpec> = emptyList(),
    val projects: List<ProjectSpec> = emptyList()
) : FormaDependency(projects.map { Either.right(it) } + names.map { Either.left(it) })

infix operator fun FormaDependency.plus(dep: FormaDependency): MixedDependency = MixedDependency(
    this.dependency.names + dep.dependency.names,
    this.dependency.projects + dep.dependency.projects
)

inline fun <reified T : FormaDependency> emptyDependency(): T = when {
    T::class == FormaDependency::class -> EmptyDependency as T
    T::class == NamedDependency::class -> NamedDependency() as T
    T::class == ProjectDependency::class -> ProjectDependency() as T
    T::class == MixedDependency::class -> MixedDependency() as T
    else -> throw IllegalArgumentException("Illegal Empty dependency, expected ${T::class.simpleName}")
}

internal fun FormaDependency.forEach(
    nameAction: (NameSpec) -> Unit = {},
    projectAction: (ProjectSpec) -> Unit = {}
) {
    dependency.forEach {
        it.right().forEach(projectAction)
        it.left().forEach(nameAction)
    }
}

fun deps(vararg names: String): NamedDependency = transitiveDeps(names = *names, transitive = false)

fun transitiveDeps(vararg names: String, transitive: Boolean = true): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Implementation, transitive) })

fun deps(vararg projects: Project): ProjectDependency =
    ProjectDependency(projects.toList().map { ProjectSpec(it, Implementation) })

fun deps(vararg dependencies: NamedDependency): NamedDependency =
    dependencies.flatMap { it.names }.let(::NamedDependency)

fun deps(vararg dependencies: ProjectDependency): ProjectDependency =
    dependencies.flatMap { it.projects }.let(::ProjectDependency)

val String.dep get() = deps(this)
