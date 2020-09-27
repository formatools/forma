package com.stepango.forma.feature

import Forma
import NamedDependency
import com.stepango.forma.config.FormaConfiguration
import com.stepango.forma.utils.implementation
import emptyDependency
import kotlin.reflect.KClass
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

data class FeatureDefinition<Extension : Any, FeatureConfiguration : Any>(
    val pluginName: String,
    val pluginExtension: KClass<Extension>,
    val featureConfiguration: FeatureConfiguration,
    val defaultDependencies: NamedDependency = emptyDependency(), // TODO do we need projects here?
    val configuration: (Extension, FeatureConfiguration, Project, FormaConfiguration) -> Unit
) {
    fun applyConfiguration(project: Project, conf: FormaConfiguration) {
        configuration(project.the(pluginExtension), featureConfiguration, project, conf)
    }
}

fun Project.applyFeatures(
    vararg features: FeatureDefinition<*, *>
) {
    features
        .forEach { definition ->
            apply(plugin = definition.pluginName)
            definition.applyConfiguration(
                this,
                Forma.configuration
            ) //TODO remove static singleton access
            definition.defaultDependencies.names.forEach {
                dependencies.implementation(it.name)
            }
        }
}