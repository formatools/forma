package tools.forma.deps

import org.funktionale.either.Either

sealed class FormaDependency(
    val dependency: DepType = emptyList()
)

object EmptyDependency : FormaDependency()

class NamedDependency(
    val names: List<NameSpec> = emptyList()
) :
    FormaDependency(names.map { Either.left(it) })

data class ProjectDependency(val projects: List<ProjectSpec> = emptyList()) :
    FormaDependency(projects.map { Either.right(it) })

data class MixedDependency(
    val names: List<NameSpec> = emptyList(),
    val projects: List<ProjectSpec> = emptyList()
) : FormaDependency(projects.map { Either.right(it) } + names.map { Either.left(it) })