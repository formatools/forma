package tools.forma.deps.core

import emptyDependency
import forEach
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import tools.forma.config.FormaSettingsStore
import tools.forma.validation.Validator

fun Project.applyDependencies(
    validator: Validator,
    repositoriesConfiguration: RepositoryHandler.() -> Unit,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    androidTestDependencies: FormaDependency = emptyDependency(),
    configurationFeatures: Map<ConfigurationType, () -> Unit> = emptyMap()
) {
    repositoriesConfiguration(repositories)
    val appliedPlugins = mutableSetOf<String>()
    dependencies {
        val projectAction: (TargetSpec) -> Unit = {
            validator.validate(it.target)
            add(it.config.name, it.target.project)
        }
        dependencies.forEach(
            { spec ->
                val plugin = FormaSettingsStore.pluginFor(spec.name)
                if (plugin != null) {
                    val pluginName = plugin.plugin.get().pluginId.split(":")[0]
                    if (!appliedPlugins.contains(pluginName)) {
                        apply(plugin = pluginName)
                        appliedPlugins.add(pluginName)
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
        testDependencies.forEach(
            { addDependencyTo("testImplementation", it.name) { isTransitive = it.transitive } },
            { add("testImplementation", it.target.project) },
            { add("testImplementation", files(it.file)) }
        )
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
