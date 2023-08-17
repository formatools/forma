import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import tools.forma.config.FormaSettingsStore
import tools.forma.deps.core.CustomConfiguration
import tools.forma.deps.core.DepType
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FileDependency
import tools.forma.deps.core.FileSpec
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.Implementation
import tools.forma.deps.core.Kapt
import tools.forma.deps.core.MixedDependency
import tools.forma.deps.core.NameSpec
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.PlatformDependency
import tools.forma.deps.core.PlatformSpec
import tools.forma.deps.core.TargetDependency
import tools.forma.deps.core.TargetSpec
import tools.forma.target.FormaTarget
import java.io.File

val DepType.names: List<NameSpec>
    get(): List<NameSpec> = filterIsInstance(NameSpec::class.java)

val DepType.targets: List<TargetSpec>
    get(): List<TargetSpec> = filterIsInstance(TargetSpec::class.java)

val DepType.files: List<FileSpec>
    get(): List<FileSpec> = filterIsInstance(FileSpec::class.java)

val Provider<out Dependency>.dep: NameSpec
    get() {
        val depName = get().run { "$group:$name:$version" }
        val pluginConf = FormaSettingsStore.pluginFor(depName)
        return with(get()) {
            NameSpec(
                "$group:$name:$version",
                pluginConf?.configuration?.let(::CustomConfiguration) ?: Implementation
            )
        }
    }

fun Provider<out Dependency>.dep(configuration: CustomConfiguration): NameSpec {
    return with(get()) { NameSpec("$group:$name:$version", configuration) }
}

private operator fun String.invoke(
    vararg providers: Provider<MinimalExternalModuleDependency>
): NamedDependency = NamedDependency(providers.map { it.dep })

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

fun deps(vararg names: String): NamedDependency = transitiveDeps(names = names, transitive = false)

fun transitivePlatform(vararg names: String, transitive: Boolean = true): PlatformDependency =
    PlatformDependency(names.toList().map { PlatformSpec(it, Implementation, transitive) })

fun transitiveDeps(vararg names: String, transitive: Boolean = true): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Implementation, transitive) })

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

fun String.dep(configuration: CustomConfiguration, transitive: Boolean = true) =
    NamedDependency(listOf(NameSpec(this, configuration, transitive)))

val String.dep: NamedDependency
    get() = deps(this)

val String.kapt: NamedDependency
    get() = kapt(this)

val Project.target: FormaTarget
    get() = FormaTarget(this)

fun Project.target(name: String): FormaTarget =
    FormaTarget(project(":" + name.substring(1).replace(":", "-")))
