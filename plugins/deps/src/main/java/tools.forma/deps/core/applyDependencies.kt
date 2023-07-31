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
    dependencies {
        val projectAction: (TargetSpec) -> Unit = {
            validator.validate(it.target)
            add(it.config.name, it.target.project)
        }
        dependencies.forEach(
            {
                // TODO refactor: we applying plugins(e.g. kapt) once per dependency with custom
                // configuration,
                //  ideally we should only apply once per configuration
                FormaSettingsStore.pluginFor(it.name)?.let {
                    // TODO: find better way of inferring plugin name
                    apply(plugin = it.plugin.get().pluginId.split(":")[0])
                }
                    ?: configurationFeatures[it.config]?.invoke()
                addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive }
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
