package tools.forma.deps.core

import emptyDependency
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import tools.forma.validation.EmptyValidator

/**
 * @deprecated Legacy wrapper for applying plugins at call sites.
 * External plugin identity is now owned by TargetType (Path A registerTargetPlugin or
 * Path B deriveTargetType). See docs/TARGET-PLUGINS.md
 */
@Deprecated(
    message = "PluginWrapper is part of the removed .withPlugin chain. Use targetPlugin() + deriveTargetType / registerTargetPlugin instead.",
    replaceWith = ReplaceWith("/* targetPlugin(\"id\") then deriveTargetType or registerTargetPlugin */"),
    level = DeprecationLevel.WARNING
)
class PluginWrapper<TPluginExtension : Any>(
    private val pluginId: String,
    private val dependencies: FormaDependency = emptyDependency(),
    private val pluginConfiguration: PluginConfiguration<TPluginExtension>? = null
) {

    operator fun invoke(project: Project) {
        project.apply(plugin = pluginId)
        pluginConfiguration?.let {
            it.configuration(project.the(it.extensionClass))
        }
        project.applyDependencies(
            validator = EmptyValidator,
            dependencies = dependencies,
            repositoriesConfiguration = EmptyRepositoriesConfiguration
        )
    }
}
