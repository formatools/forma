import org.gradle.api.Project

import tools.forma.deps.DepType
import tools.forma.deps.FileSpec
import tools.forma.deps.NameSpec
import tools.forma.deps.TargetSpec
import tools.forma.deps.ConfigurationType
import tools.forma.deps.Implementation
import tools.forma.deps.Kapt
import tools.forma.deps.FormaDependency
import tools.forma.deps.FileDependency
import tools.forma.deps.MixedDependency
import tools.forma.deps.EmptyDependency
import tools.forma.deps.ProjectDependency
import tools.forma.deps.NamedDependency
import tools.forma.target.FormaTarget
import java.io.File

val DepType.names: List<NameSpec>
    get(): List<NameSpec> = filterIsInstance(NameSpec::class.java)

val DepType.targets: List<TargetSpec>
    get(): List<TargetSpec> = filterIsInstance(TargetSpec::class.java)

val DepType.files: List<FileSpec>
    get(): List<FileSpec> = filterIsInstance(FileSpec::class.java)

infix operator fun FormaDependency.plus(dep: FormaDependency): MixedDependency = MixedDependency(
    dependency.names + dep.dependency.names,
    dependency.targets + dep.dependency.targets,
    dependency.files + dep.dependency.files
)

inline fun <reified T : FormaDependency> emptyDependency(): T = when {
    T::class == FormaDependency::class -> EmptyDependency as T
    T::class == NamedDependency::class -> NamedDependency() as T
    T::class == FileDependency::class -> FileDependency() as T
    T::class == ProjectDependency::class -> ProjectDependency() as T
    T::class == MixedDependency::class -> MixedDependency() as T
    else -> throw IllegalArgumentException("Illegal Empty dependency, expected ${T::class.simpleName}")
}

fun FormaDependency.forEach(
    nameAction: (NameSpec) -> Unit = {},
    targetAction: (TargetSpec) -> Unit = {},
    fileAction: (FileSpec) -> Unit = {}
) {
    dependency.forEach loop@ { spec ->
        return@loop when (spec) {
            is TargetSpec -> targetAction(spec)
            is NameSpec -> nameAction(spec)
            is FileSpec -> fileAction(spec)
        }
    }
}

internal fun FormaDependency.hasConfigType(configType: ConfigurationType): Boolean {
    dependency.forEach { dep ->
        if (dep.config == configType) return true
    }
    return false
}

fun deps(vararg names: String): NamedDependency = transitiveDeps(names = *names, transitive = false)

fun transitiveDeps(vararg names: String, transitive: Boolean = true): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Implementation, transitive) })

fun deps(vararg projects: Project): ProjectDependency =
    ProjectDependency(projects.toList().map { TargetSpec(it.target, Implementation) })

fun deps(vararg files: File): FileDependency =
    FileDependency(files.toList().map { FileSpec(it, Implementation) })

fun deps(vararg dependencies: NamedDependency): NamedDependency =
    dependencies.flatMap { it.names }.let(::NamedDependency)

fun deps(vararg dependencies: ProjectDependency): ProjectDependency =
    dependencies.flatMap { it.targets }.let(::ProjectDependency)

fun kapt(vararg names: String): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Kapt, true) })

val String.dep: NamedDependency get() = deps(this)

val String.kapt: NamedDependency get() = kapt(this)

val Project.target: FormaTarget get() = FormaTarget(this)

