package tools.forma.deps

sealed class FormaDependency(
    val dependency: DepType = emptyList()
)

object EmptyDependency : FormaDependency()

class NamedDependency(
    val names: List<NameSpec> = emptyList()
) : FormaDependency(names)

data class ProjectDependency(
    val projects: List<ProjectSpec> = emptyList()
) : FormaDependency(projects)

data class MixedDependency(
    val names: List<NameSpec> = emptyList(),
    val projects: List<ProjectSpec> = emptyList()
) : FormaDependency(projects + names)