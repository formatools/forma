import java.io.File
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.Provider
import tools.forma.config.FormaSettingsStore
import tools.forma.core.fleet.ProjectPathForms
import tools.forma.deps.core.CustomConfiguration
import tools.forma.deps.core.DepType
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FileDependency
import tools.forma.deps.core.FileSpec
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.Implementation
import tools.forma.deps.core.Ksp
import tools.forma.deps.core.MixedDependency
import tools.forma.deps.core.NameSpec
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.PlatformDependency
import tools.forma.deps.core.PlatformSpec
import tools.forma.deps.core.TargetDependency
import tools.forma.deps.core.TargetSpec
import tools.forma.target.FormaTarget

/**
 * Typed views over [DepType]. Prefer [FormaDependency.forEach] at apply-time so we do not
 * allocate three filtered lists when only one kind is needed (F-017 / GH #106).
 */
val DepType.names: List<NameSpec>
    get(): List<NameSpec> = filterIsInstance(NameSpec::class.java)

val DepType.targets: List<TargetSpec>
    get(): List<TargetSpec> = filterIsInstance(TargetSpec::class.java)

val DepType.files: List<FileSpec>
    get(): List<FileSpec> = filterIsInstance(FileSpec::class.java)

val Provider<out Dependency>.dep: NameSpec
    get() {
        val resolved = get()
        val depName = "${resolved.group}:${resolved.name}:${resolved.version}"
        val pluginConf = FormaSettingsStore.pluginFor(depName)
        return NameSpec(
            depName,
            pluginConf?.configuration?.let(::CustomConfiguration) ?: Implementation
        )
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

infix operator fun FormaDependency.plus(dep: FormaDependency): MixedDependency {
    // Avoid three filterIsInstance passes (names/targets/files) when both sides are already typed.
    val leftNames: List<NameSpec>
    val leftTargets: List<TargetSpec>
    val leftFiles: List<FileSpec>
    when (this) {
        is NamedDependency -> {
            leftNames = names
            leftTargets = emptyList()
            leftFiles = emptyList()
        }
        is TargetDependency -> {
            leftNames = emptyList()
            leftTargets = targets
            leftFiles = emptyList()
        }
        is FileDependency -> {
            leftNames = emptyList()
            leftTargets = emptyList()
            leftFiles = files
        }
        is MixedDependency -> {
            leftNames = names
            leftTargets = targets
            leftFiles = files
        }
        is PlatformDependency -> {
            leftNames = emptyList()
            leftTargets = emptyList()
            leftFiles = emptyList()
        }
        EmptyDependency -> {
            leftNames = emptyList()
            leftTargets = emptyList()
            leftFiles = emptyList()
        }
    }
    val rightNames: List<NameSpec>
    val rightTargets: List<TargetSpec>
    val rightFiles: List<FileSpec>
    when (dep) {
        is NamedDependency -> {
            rightNames = dep.names
            rightTargets = emptyList()
            rightFiles = emptyList()
        }
        is TargetDependency -> {
            rightNames = emptyList()
            rightTargets = dep.targets
            rightFiles = emptyList()
        }
        is FileDependency -> {
            rightNames = emptyList()
            rightTargets = emptyList()
            rightFiles = dep.files
        }
        is MixedDependency -> {
            rightNames = dep.names
            rightTargets = dep.targets
            rightFiles = dep.files
        }
        is PlatformDependency -> {
            rightNames = emptyList()
            rightTargets = emptyList()
            rightFiles = emptyList()
        }
        EmptyDependency -> {
            rightNames = emptyList()
            rightTargets = emptyList()
            rightFiles = emptyList()
        }
    }
    return MixedDependency(leftNames + rightNames, leftTargets + rightTargets, leftFiles + rightFiles)
}

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
    val specs = dependency
    if (specs.isEmpty()) return
    for (i in specs.indices) {
        when (val spec = specs[i]) {
            is TargetSpec -> targetAction(spec)
            is NameSpec -> nameAction(spec)
            is PlatformSpec -> platformAction(spec)
            is FileSpec -> fileAction(spec)
        }
    }
}

fun deps(vararg names: String): NamedDependency = transitiveDeps(names = names, transitive = false)

fun transitivePlatform(vararg names: String, transitive: Boolean = true): PlatformDependency =
    PlatformDependency(names.map { PlatformSpec(it, Implementation, transitive) })

fun transitiveDeps(vararg names: String, transitive: Boolean = true): NamedDependency =
    NamedDependency(names.map { NameSpec(it, Implementation, transitive) })

fun deps(vararg targets: FormaTarget): TargetDependency =
    TargetDependency(targets.map { TargetSpec(it, Implementation) })

fun deps(vararg files: File): FileDependency =
    FileDependency(files.map { FileSpec(it, Implementation) })

fun deps(vararg dependencies: NamedDependency): NamedDependency {
    if (dependencies.isEmpty()) return NamedDependency()
    if (dependencies.size == 1) return dependencies[0]
    val out = ArrayList<NameSpec>(dependencies.sumOf { it.names.size })
    for (dep in dependencies) out.addAll(dep.names)
    return NamedDependency(out)
}

/**
 * Gate every name spec in this [NamedDependency] on project-global feature flag [flag]
 * (F-099 / GH #126). Resolution happens at [tools.forma.deps.core.applyDependencies]
 * time via [tools.forma.deps.core.resolveFeatureFlags] — the flag value is **not**
 * frozen when this helper runs.
 *
 * @param flag name declared in `androidProjectConfiguration(featureFlags = …)`
 * @param enabled expected flag value (default `true` = include when flag is on)
 */
fun NamedDependency.whenFlag(flag: String, enabled: Boolean = true): NamedDependency =
    NamedDependency(
        names.map { spec ->
            NameSpec(
                name = spec.name,
                config = spec.config,
                transitive = spec.transitive,
                featureFlag = flag,
                featureFlagExpected = enabled,
            )
        }
    )

/**
 * Include [dependencies] only when project-global flag [flag] equals [enabled]
 * (default: flag on). Compose with [deps]:
 *
 * ```kotlin
 * dependencies = deps(
 *     "g:always:1".dep,
 *     depsIf("daggerReflect", "com.jakewharton.dagger:dagger-reflect:…".dep),
 *     depsUnless("daggerReflect", "com.google.dagger:dagger:…".dep),
 *     depsUnless("daggerReflect", "com.google.dagger:dagger-compiler:…".ksp),
 * )
 * ```
 *
 * Flag values are read at [tools.forma.deps.core.applyDependencies] time from
 * `Forma.settings.featureFlags` / [tools.forma.config.FormaSettingsStore.featureFlagsOrEmpty].
 */
fun depsIf(
    flag: String,
    vararg dependencies: NamedDependency,
    enabled: Boolean = true,
): NamedDependency = deps(*dependencies).whenFlag(flag, enabled)

/**
 * Include [dependencies] only when project-global flag [flag] is **off**
 * (unknown flags count as off — see [tools.forma.config.FormaFeatureFlags]).
 */
fun depsUnless(flag: String, vararg dependencies: NamedDependency): NamedDependency =
    deps(*dependencies).whenFlag(flag, enabled = false)

/** Typesafe project accessors / [ProjectDependency] → target deps (Gradle 9: path only, no dependencyProject). */
fun Project.deps(vararg projects: ProjectDependency): TargetDependency =
    TargetDependency(projects.map { TargetSpec(target(it)) })

fun deps(vararg dependencies: Provider<*>): NamedDependency {
    if (dependencies.isEmpty()) return NamedDependency()
    val out = ArrayList<NameSpec>(dependencies.size)
    for (provider in dependencies) {
        val source = provider.get()
        @Suppress("UNCHECKED_CAST")
        when (source) {
            // here we need to call .dep on provider to get the correct configuration
            // since on configuration phase we don't have the actual dependency
            is Dependency -> out.add((provider as Provider<Dependency>).dep)
            is ExternalModuleDependencyBundle -> {
                for (item in source) out.add(item.dep)
            }
            else ->
                throw IllegalArgumentException(
                    "Unsupported dependency type ${source::class.simpleName}"
                )
        }
    }
    return NamedDependency(out)
}

fun deps(vararg dependencies: TargetDependency): TargetDependency {
    if (dependencies.isEmpty()) return TargetDependency()
    if (dependencies.size == 1) return dependencies[0]
    val out = ArrayList<TargetSpec>(dependencies.sumOf { it.targets.size })
    for (dep in dependencies) out.addAll(dep.targets)
    return TargetDependency(out)
}

fun ksp(vararg names: String): NamedDependency =
    NamedDependency(names.map { NameSpec(it, Ksp, true) })

fun String.dep(configuration: CustomConfiguration, transitive: Boolean = true) =
    NamedDependency(listOf(NameSpec(this, configuration, transitive)))

val String.dep: NamedDependency
    get() = deps(this)

/** Single-string transitive named dep (parity with [String.dep]; multi via [transitiveDeps]). */
val String.transitiveDep: NamedDependency
    get() = transitiveDeps(this)

val String.ksp: NamedDependency
    get() = ksp(this)

/** Self-ref when a target needs to depend on / name itself. */
val Project.target: FormaTarget
    get() = FormaTarget(this)

/**
 * Resolve a **Forma logical path** to a [FormaTarget].
 *
 * Happy path: colon paths such as `target(":feature:home:impl")`. Includer maps nested
 * dirs to dashed Gradle project names (`:feature-home-impl`); see
 * [ProjectPathForms.gradleProjectPathFromFormaTarget]. Do **not** use raw `project(":…")`
 * at call sites — that is an implementation detail inside this helper.
 *
 * Typesafe accessors: [target] overload taking [ProjectDependency], or [Project.deps].
 * Slash / Bazel-style paths are out of scope (discussion #57).
 */
fun Project.target(name: String): FormaTarget =
    project(ProjectPathForms.gradleProjectPathFromFormaTarget(name)).target

/**
 * Resolve a [ProjectDependency] (including typesafe project accessors) to a [FormaTarget].
 * Gradle 9 removed [ProjectDependency]→Project (`dependencyProject`); use [ProjectDependency.getPath].
 */
fun Project.target(projectDependency: ProjectDependency): FormaTarget =
    project(projectDependency.path).target
