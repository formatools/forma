package com.stepango.forma.utils

import Forma
import FormaDependency
import ProjectSpec
import Kapt
import com.stepango.forma.config.FormaConfiguration
import com.stepango.forma.validation.Validator
import emptyDependency
import forEach
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.applyDependencies(
    validator: Validator,
    formaConfiguration: FormaConfiguration = Forma.configuration,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    androidTestDependencies: FormaDependency = emptyDependency()
) {
    formaConfiguration.repositories(repositories)
    dependencies {
        val projectAction: (ProjectSpec) -> Unit = {
            validator.validate(it.project)
            add(it.config.name, it.project)
        }
        dependencies.forEach({
            when (it.config) {
                is Kapt -> kapt(it.name)
                else -> addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive }
            }},
            projectAction
        )
        testDependencies.forEach(
            { testImplementation(it.name) { isTransitive = it.transitive } },
            { testImplementation(it.project) }
        )
        androidTestDependencies.forEach(
            { androidTestImplementation(it.name) { isTransitive = it.transitive } },
            { androidTestImplementation(it.project) }
        )
    }
}
