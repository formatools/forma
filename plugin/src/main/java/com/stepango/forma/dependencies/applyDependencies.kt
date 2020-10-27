package com.stepango.forma.dependencies

import Forma
import FormaDependency
import ProjectSpec
import Kapt
import com.stepango.forma.config.FormaConfiguration
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
import com.stepango.forma.utils.addDependencyTo
import com.stepango.forma.utils.androidTestImplementation
import com.stepango.forma.utils.kapt
import com.stepango.forma.utils.testImplementation
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
    var kaptApplied = false
    formaConfiguration.repositories(repositories)
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
            { testImplementation(it.name) { isTransitive = it.transitive } },
            { testImplementation(it.project) }
        )
        androidTestDependencies.forEach(
            { androidTestImplementation(it.name) { isTransitive = it.transitive } },
            { androidTestImplementation(it.project) }
        )
    }
}
