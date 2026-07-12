package tools.forma.android.feature

import Forma
import tools.forma.deps.core.NamedDependency
import tools.forma.config.AndroidProjectSettings
import emptyDependency
import kotlin.reflect.KClass
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import tools.forma.deps.core.addDependencyTo

data class FeatureDefinition<Extension : Any, FeatureConfiguration : Any>(
    val pluginName: String,
    val pluginExtension: KClass<Extension>,
    val featureConfiguration: FeatureConfiguration,
    val defaultDependencies: NamedDependency = emptyDependency(),
    /**
     * Optional settings override. When null (default), [applyConfiguration] reads the live
     * [Forma.settings] so cached [FeatureDefinition] instances stay correct after
     * `androidProjectConfiguration { … }` stores settings (F-017).
     */
    val androidProjectSettings: AndroidProjectSettings? = null,
    val configuration: (Extension, FeatureConfiguration, Project, AndroidProjectSettings) -> Unit
) {
    fun applyConfiguration(project: Project) = configuration(
        project.the(pluginExtension),
        featureConfiguration,
        project,
        androidProjectSettings ?: Forma.settings
    )
}

fun Project.applyFeatures(
    vararg features: FeatureDefinition<*, *>
) = features.forEach { definition ->
    apply(plugin = definition.pluginName)
    definition.applyConfiguration(this)
    val defaults = definition.defaultDependencies.names
    if (defaults.isNotEmpty()) {
        defaults.forEach {
            dependencies.addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive }
        }
    }
}
