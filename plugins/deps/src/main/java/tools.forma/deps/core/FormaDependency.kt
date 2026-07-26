package tools.forma.deps.core

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

/**
 * Mixed first-party + third-party (and optional BOMs).
 *
 * [platforms] must be preserved across [plus] so Firebase/AndroidX BOM + artifact
 * stacks work (F-114). Platforms are applied as Gradle `platform(...)` entries.
 */
data class MixedDependency(
    val names: List<NameSpec> = emptyList(),
    val targets: List<TargetSpec> = emptyList(),
    val files: List<FileSpec> = emptyList(),
    val platforms: List<PlatformSpec> = emptyList(),
) : FormaDependency(targets + names + files + platforms)
