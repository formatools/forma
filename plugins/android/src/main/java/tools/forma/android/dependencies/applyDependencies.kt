package tools.forma.android.dependencies

import FormaDependency
import ProjectSpec
import Kapt
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinKaptFeatureDefinition
import tools.forma.deps.addDependencyTo
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
    androidTestDependencies: FormaDependency = emptyDependency()
) {
    var kaptApplied = false
    repositoriesConfiguration(repositories)
    dependencies {
        val projectAction: (ProjectSpec) -> Unit = {
            validator.validate(it.project)
            add(it.config.name, it.project)
        }
        dependencies.forEach(
            {
                if (!kaptApplied && it.config == Kapt) {
                    // TODO Force one AP per module
                    applyFeatures(kotlinKaptFeatureDefinition())
                    kaptApplied = true
                }
                addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive }
            },
            projectAction
        )
        testDependencies.forEach(
            { addDependencyTo("testImplementation", it.name) { isTransitive = it.transitive } },
            { add("testImplementation", it.project) }
        )
        androidTestDependencies.forEach(
            { addDependencyTo("androidTestImplementation", it.name) { isTransitive = it.transitive } },
            { add("androidTestImplementation", it.project) }
        )
    }
}
