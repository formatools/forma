package tools.forma.android.feature

import Forma
import tools.forma.deps.NamedDependency
import tools.forma.android.config.FormaConfiguration
import emptyDependency
import kotlin.reflect.KClass
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import tools.forma.deps.addDependencyTo

data class FeatureDefinition<Extension : Any, FeatureConfiguration : Any>(
    val pluginName: String,
    val pluginExtension: KClass<Extension>,
    val featureConfiguration: FeatureConfiguration,
    val defaultDependencies: NamedDependency = emptyDependency(),
    val formaConfiguration: FormaConfiguration = Forma.configuration,
    val configuration: (Extension, FeatureConfiguration, Project, FormaConfiguration) -> Unit
) {
    fun applyConfiguration(project: Project) = configuration(
        project.the(pluginExtension),
        featureConfiguration,
        project,
        formaConfiguration
    )
}

fun Project.applyFeatures(
    vararg features: FeatureDefinition<*, *>
) = features.forEach { definition ->
    apply(plugin = definition.pluginName)
    definition.applyConfiguration(this)
    definition.defaultDependencies.names.forEach {
        dependencies.addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive }
    }
}