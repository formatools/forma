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

    // Prevent same plugin to be applied twice
    val appliedPlugins = mutableSetOf<String>()
    val hasPluginDeps = FormaSettingsStore.dependencyPlugins.isNotEmpty()

    dependencies {
        val projectAction: (TargetSpec) -> Unit = {
            validator.validate(it.target)
            add(it.config.name, it.target.project)
        }
        dependencies.forEach(
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
        if (testDependencies !== EmptyDependency) {
            testDependencies.forEach(
                { addDependencyTo("testImplementation", it.name) { isTransitive = it.transitive } },
                { add("testImplementation", it.target.project) },
                { add("testImplementation", files(it.file)) }
            )
        }
        if (androidTestDependencies !== EmptyDependency) {
            androidTestDependencies.forEach(
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
