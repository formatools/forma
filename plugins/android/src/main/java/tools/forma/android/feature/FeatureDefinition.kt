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
    /** Gradle plugin id to apply. Blank = no plugin (e.g. AGP 9 built-in Kotlin config-only). */
    val pluginName: String,
    /**
     * Extension type looked up via `project.the`. Null when [pluginName] is blank / no extension
     * (then [configuration] receives [Unit] cast to [Extension]).
     */
    val pluginExtension: KClass<Extension>? = null,
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
    @Suppress("UNCHECKED_CAST")
    fun applyConfiguration(project: Project) {
        val settings = androidProjectSettings ?: Forma.settings
        val extension: Extension =
            if (pluginExtension != null) {
                project.the(pluginExtension)
            } else {
                Unit as Extension
            }
        configuration(extension, featureConfiguration, project, settings)
    }
}

fun Project.applyFeatures(
    vararg features: FeatureDefinition<*, *>
) = features.forEach { definition ->
    if (definition.pluginName.isNotBlank()) {
        apply(plugin = definition.pluginName)
    }
    definition.applyConfiguration(this)
    val defaults = definition.defaultDependencies.names
    if (defaults.isNotEmpty()) {
        defaults.forEach {
            dependencies.addDependencyTo(it.config.name, it.name) { isTransitive = it.transitive }
        }
    }
}
