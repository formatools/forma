import org.gradle.api.Project

import tools.forma.deps.DepType
import tools.forma.deps.NameSpec
import tools.forma.deps.TargetSpec
import tools.forma.deps.ConfigurationType
import tools.forma.deps.Implementation
import tools.forma.deps.Kapt
import tools.forma.deps.FormaDependency
import tools.forma.deps.MixedDependency
import tools.forma.deps.EmptyDependency
import tools.forma.deps.ProjectDependency
import tools.forma.deps.NamedDependency
import tools.forma.target.FormaTarget

val DepType.names: List<NameSpec>
    get(): List<NameSpec> = this.filterIsInstance<NameSpec>()

val DepType.targets: List<TargetSpec>
    get(): List<TargetSpec> = this.filterIsInstance<TargetSpec>()

infix operator fun FormaDependency.plus(dep: FormaDependency): MixedDependency = MixedDependency(
    this.dependency.names + dep.dependency.names,
    this.dependency.targets + dep.dependency.targets
)

inline fun <reified T : FormaDependency> emptyDependency(): T = when {
    T::class == FormaDependency::class -> EmptyDependency as T
    T::class == NamedDependency::class -> NamedDependency() as T
    T::class == ProjectDependency::class -> ProjectDependency() as T
    T::class == MixedDependency::class -> MixedDependency() as T
    else -> throw IllegalArgumentException("Illegal Empty dependency, expected ${T::class.simpleName}")
}

fun FormaDependency.forEach(
    nameAction: (NameSpec) -> Unit = {},
    targetAction: (TargetSpec) -> Unit = {}
) {
    with(dependency) {
        names.forEach(nameAction)
        targets.forEach(targetAction)
    }
}

internal fun FormaDependency.hasConfigType(configType: ConfigurationType): Boolean {
    dependency.forEach { spec ->
        if (spec.config == configType) return true
    }
    return false
}

fun deps(vararg names: String): NamedDependency = transitiveDeps(names = *names, transitive = false)

fun transitiveDeps(vararg names: String, transitive: Boolean = true): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Implementation, transitive) })

fun deps(vararg projects: Project): ProjectDependency =
    ProjectDependency(projects.toList().map { TargetSpec(it.target, Implementation) })

fun deps(vararg dependencies: NamedDependency): NamedDependency =
    dependencies.flatMap { it.names }.let { NamedDependency(it) }

fun deps(vararg dependencies: ProjectDependency): ProjectDependency =
    dependencies.flatMap { it.targets }.let(::ProjectDependency)

fun kapt(vararg names: String): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Kapt, true) })

val String.dep: NamedDependency get() = deps(this)

val String.kapt: NamedDependency get() = kapt(this)

val Project.target: FormaTarget get() = FormaTarget(this)
