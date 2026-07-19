package tools.forma.deps.core

/**
 * Specification for an external Gradle plugin that is owned by a TargetType.
 * Registered once against a type (Path A or Path B); auto-applied on every use of that type.
 * No plugin ids ever appear at call sites.
 */
data class TargetPluginSpec(
    val id: String,
    val dependencies: FormaDependency = EmptyDependency
)

/** Factory for a type-owned external plugin spec (id + optional companion deps). */
fun targetPlugin(
    id: String,
    dependencies: FormaDependency = EmptyDependency
): TargetPluginSpec = TargetPluginSpec(id, dependencies)
