package tools.forma.deps.core

import emptyDependency
import forEach
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import tools.forma.config.FormaSettingsStore
import tools.forma.validation.Validator

/** No-op repo config sentinel — skip [Project.repositories] when callers pass this. */
val EmptyRepositoriesConfiguration: RepositoryHandler.() -> Unit = {}

fun Project.applyDependencies(
    validator: Validator,
    repositoriesConfiguration: RepositoryHandler.() -> Unit = EmptyRepositoriesConfiguration,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    androidTestDependencies: FormaDependency = emptyDependency(),
    configurationFeatures: Map<ConfigurationType, () -> Unit> = emptyMap()
) {
    // Per-target repository blocks are expensive at scale. Prefer empty / settings-level
    // dependencyResolutionManagement; only invoke when the caller passes a real config.
    if (repositoriesConfiguration !== EmptyRepositoriesConfiguration) {
        repositoriesConfiguration(repositories)
    }

    // EmptyDependency is the common default — avoid opening a dependencies {} block at all.
    if (
        dependencies === EmptyDependency &&
            testDependencies === EmptyDependency &&
            androidTestDependencies === EmptyDependency
    ) {
        return
    }

    // F-099: resolve feature-flag-gated named deps against project-global flags at apply
    // time (not when depsIf/depsUnless was called). Unknown flags = false.
    val featureFlags = FormaSettingsStore.featureFlagsOrEmpty()
    val resolvedDependencies = dependencies.resolveFeatureFlags(featureFlags)
    val resolvedTestDependencies = testDependencies.resolveFeatureFlags(featureFlags)
    val resolvedAndroidTestDependencies =
        androidTestDependencies.resolveFeatureFlags(featureFlags)

    if (
        resolvedDependencies === EmptyDependency &&
            resolvedTestDependencies === EmptyDependency &&
            resolvedAndroidTestDependencies === EmptyDependency
    ) {
        return
    }

    // Prevent same plugin to be applied twice
    val appliedPlugins = mutableSetOf<String>()
    val hasPluginDeps = FormaSettingsStore.dependencyPlugins.isNotEmpty()

    dependencies {
        // F-090: skip project-dep suffix validation only for globally excluded modules
        // (forked third-party trees). Self-type validation on DSL entry stays enforced.
        val projectAction: (TargetSpec) -> Unit = {
            val depProject = it.target.project
            if (!FormaSettingsStore.isExcludedFromDependencyValidation(
                    projectName = depProject.name,
                    projectPath = depProject.path,
                )
            ) {
                validator.validate(it.target)
            }
            add(it.config.name, depProject)
        }
        resolvedDependencies.forEach(
            { spec ->
                val plugin =
                    if (hasPluginDeps) FormaSettingsStore.pluginFor(spec.name) else null
                if (plugin != null) {
                    val pluginName = plugin.plugin.get().pluginId.split(":")[0]
                    if (appliedPlugins.add(pluginName)) {
                        apply(plugin = pluginName)
                    }
                    // For custom plugin specs we always apply transitive dependencies
                    // since this is what most of the plugins expect
                    addDependencyTo(spec.config.name, spec.name) { isTransitive = true }
                } else {
                    configurationFeatures[spec.config]?.invoke()
                    addDependencyTo(spec.config.name, spec.name) { isTransitive = spec.transitive }
                }
            },
            projectAction,
            { add(it.config.name, files(it.file)) },
            { addDependencyTo(it.config.name, platform(it.name)) { isTransitive = it.transitive } }
        )
        if (resolvedTestDependencies !== EmptyDependency) {
            resolvedTestDependencies.forEach(
                { addDependencyTo("testImplementation", it.name) { isTransitive = it.transitive } },
                { add("testImplementation", it.target.project) },
                { add("testImplementation", files(it.file)) }
            )
        }
        if (resolvedAndroidTestDependencies !== EmptyDependency) {
            resolvedAndroidTestDependencies.forEach(
                {
                    addDependencyTo("androidTestImplementation", it.name) {
                        isTransitive = it.transitive
                    }
                },
                { add("androidTestImplementation", it.target.project) },
                { add("androidTestImplementation", files(it.file)) }
            )
        }
    }
}
