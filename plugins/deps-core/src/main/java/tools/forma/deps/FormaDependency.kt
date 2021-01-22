package tools.forma.deps

sealed class FormaDependency(
    val dependency: DepType = emptyList()
)

object EmptyDependency : FormaDependency()

class PlatformDependency(
    val names: List<PlatformSpec> = emptyList()
) : FormaDependency(names)

class NamedDependency(
    val names: List<NameSpec> = emptyList()
) : FormaDependency(names)

data class TargetDependency(
    val targets: List<TargetSpec> = emptyList()
) : FormaDependency(targets)

data class FileDependency(
    val files: List<FileSpec> = emptyList()
) : FormaDependency(files)

data class MixedDependency(
    val names: List<NameSpec> = emptyList(),
    val targets: List<TargetSpec> = emptyList(),
    val files: List<FileSpec> = emptyList()
) : FormaDependency(targets + names + files)