package tools.forma.deps

import tools.forma.validation.Validator
import emptyDependency
import forEach
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.dependencies

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
                configurationFeatures[it.config]?.invoke()
                if (it.platform) {
                    addDependencyTo(it.config.name, platform(it.name)) { isTransitive = it.transitive }
                } else {
                    addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive }
                }
            },
            projectAction,
            { add(it.config.name, files(it.file)) }
        )
        testDependencies.forEach(
            { addDependencyTo("testImplementation", it.name) { isTransitive = it.transitive } },
            { add("testImplementation", it.target.project) },
            { add("testImplementation", files(it.file)) }
        )
        androidTestDependencies.forEach(
            { addDependencyTo("androidTestImplementation", it.name) { isTransitive = it.transitive } },
            { add("androidTestImplementation", it.target.project) },
            { add("androidTestImplementation", files(it.file)) }
        )
    }
}
