package tools.forma.deps

sealed class FormaDependency(
    val dependency: DepType = emptyList()
)

object EmptyDependency : FormaDependency()

class NamedDependency(
    val names: List<NameSpec> = emptyList()
) : FormaDependency(names)

data class ProjectDependency(
    val targets: List<TargetSpec> = emptyList()
) : FormaDependency(targets)

data class MixedDependency(
    val names: List<NameSpec> = emptyList(),
    val targets: List<TargetSpec> = emptyList()
) : FormaDependency(targets + names)