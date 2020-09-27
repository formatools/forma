package com.stepango.forma.utils

import Forma
import FormaDependency
import ProjectDependency
import ProjectSpec
import com.stepango.forma.config.FormaConfiguration
import com.stepango.forma.validation.EmptyValidator
import com.stepango.forma.validation.Validator
import emptyDependency
import forEach
import kotlin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.applyDependencies(
    validator: Validator,
    testValidator: Validator = EmptyValidator, //TODO need to validate test dependencies
    formaConfiguration: FormaConfiguration = Forma.configuration,
    dependencies: FormaDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    androidTestDependencies: FormaDependency = emptyDependency(),
    kotlinStdLib: Boolean = true
) {
    formaConfiguration.repositories(repositories)
    dependencies {
        val projectAction: (ProjectSpec) -> Unit = {
            validator.validate(it.project)
            add(it.config.name, it.project)
        }
        dependencies.forEach(
            { addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive } },
            projectAction
        )
        projectDependencies.forEach(projectAction = projectAction)
        testDependencies.forEach(
            { testImplementation(it.name) { isTransitive = it.transitive } },
            { testImplementation(it.project) }
        )
        androidTestDependencies.forEach(
            { androidTestImplementation(it.name) { isTransitive = it.transitive } },
            { androidTestImplementation(it.project) }
        )
        if (kotlinStdLib) dependencies {
            kotlin.stdlib_jdk8.names.forEach {
                implementation(it.name)
            }
        }
    }
}
