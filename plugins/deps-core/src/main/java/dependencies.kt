import java.io.File
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.provider.Provider
import tools.forma.config.FormaSettingsStore
import tools.forma.deps_core.ConfigurationType
import tools.forma.deps_core.Custom
import tools.forma.deps_core.DepType
import tools.forma.deps_core.EmptyDependency
import tools.forma.deps_core.FileDependency
import tools.forma.deps_core.FileSpec
import tools.forma.deps_core.FormaDependency
import tools.forma.deps_core.Implementation
import tools.forma.deps_core.Kapt
import tools.forma.deps_core.MixedDependency
import tools.forma.deps_core.NameSpec
import tools.forma.deps_core.NamedDependency
import tools.forma.deps_core.PlatformDependency
import tools.forma.deps_core.PlatformSpec
import tools.forma.deps_core.TargetDependency
import tools.forma.deps_core.TargetSpec
import tools.forma.target.FormaTarget

val DepType.names: List<NameSpec>
    get(): List<NameSpec> = filterIsInstance(NameSpec::class.java)

val DepType.targets: List<TargetSpec>
    get(): List<TargetSpec> = filterIsInstance(TargetSpec::class.java)

val DepType.files: List<FileSpec>
    get(): List<FileSpec> = filterIsInstance(FileSpec::class.java)

val Provider<out Dependency>.dep: NameSpec
    get() {
        val pluginConf = FormaSettingsStore.pluginFor(this)
        return with(get()) {
            NameSpec(
                "$group:$name:$version",
                pluginConf?.configuration?.let(::Custom) ?: Implementation
            )
        }
    }

val Dependency.dep: NameSpec
    get() = NameSpec("$group:$name:$version", Implementation)

val Provider<ExternalModuleDependencyBundle>.dep: List<NameSpec>
    get() = get().map { it.dep }

infix operator fun FormaDependency.plus(dep: FormaDependency): MixedDependency =
    MixedDependency(
        dependency.names + dep.dependency.names,
        dependency.targets + dep.dependency.targets,
        dependency.files + dep.dependency.files
    )

inline fun <reified T : FormaDependency> emptyDependency(): T =
    when (T::class) {
        FormaDependency::class -> EmptyDependency as T
        NamedDependency::class -> NamedDependency() as T
        FileDependency::class -> FileDependency() as T
        TargetDependency::class -> TargetDependency() as T
        MixedDependency::class -> MixedDependency() as T
        else ->
            throw IllegalArgumentException(
                "Illegal Empty dependency, expected ${T::class.simpleName}"
            )
    }

fun FormaDependency.forEach(
    nameAction: (NameSpec) -> Unit = {},
    targetAction: (TargetSpec) -> Unit = {},
    fileAction: (FileSpec) -> Unit = {},
    platformAction: (PlatformSpec) -> Unit = {}
) {
    dependency.forEach loop@{ spec ->
        return@loop when (spec) {
            is TargetSpec -> targetAction(spec)
            is NameSpec -> nameAction(spec)
            is PlatformSpec -> platformAction(spec)
            is FileSpec -> fileAction(spec)
        }
    }
}

internal fun FormaDependency.hasConfigType(configType: ConfigurationType): Boolean {
    dependency.forEach { dep -> if (dep.config == configType) return true }
    return false
}

fun deps(vararg names: String): NamedDependency = transitiveDeps(names = names, transitive = false)

fun platform(vararg names: String): PlatformDependency =
    transitivePlatform(*names, transitive = false)

fun transitivePlatform(vararg names: String, transitive: Boolean = true): PlatformDependency =
    PlatformDependency(names.toList().map { PlatformSpec(it, Implementation, transitive) })

fun transitiveDeps(vararg names: String, transitive: Boolean = true): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Implementation, transitive) })

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "Deprecated in favor of targets version of this function:\n" + "deps(target(\":name\"))"
)
fun deps(vararg projects: Project): TargetDependency =
    TargetDependency(projects.map { TargetSpec(it.target, Implementation) })

fun deps(vararg targets: FormaTarget): TargetDependency =
    TargetDependency(targets.map { TargetSpec(it, Implementation) })

fun deps(vararg files: File): FileDependency =
    FileDependency(files.map { FileSpec(it, Implementation) })

fun deps(vararg dependencies: NamedDependency): NamedDependency =
    dependencies.flatMap { it.names }.let(::NamedDependency)

fun deps(vararg dependencies: Provider<*>): NamedDependency =
    dependencies
        .flatMap { provider ->
            val source = provider.get()
            @Suppress("UNCHECKED_CAST")
            when (source) {
                // here we need to call .dep on provider to get the correct configuration
                // since on configuration phase we don't have the actual dependency
                is Dependency -> listOf((provider as Provider<Dependency>).dep)
                is ExternalModuleDependencyBundle -> source.map { it.dep }
                else ->
                    throw IllegalArgumentException(
                        "Unsupported dependency type ${source::class.simpleName}"
                    )
            }
        }
        .let(::NamedDependency)

fun deps(vararg dependencies: TargetDependency): TargetDependency =
    dependencies.flatMap { it.targets }.let(::TargetDependency)

fun kapt(vararg names: String): NamedDependency =
    NamedDependency(names.map { NameSpec(it, Kapt, true) })

val String.dep: NamedDependency
    get() = deps(this)

val String.kapt: NamedDependency
    get() = kapt(this)

val Project.target: FormaTarget
    get() = FormaTarget(this)

fun Project.target(name: String): FormaTarget =
    FormaTarget(project(":" + name.substring(1).replace(":", "-")))
