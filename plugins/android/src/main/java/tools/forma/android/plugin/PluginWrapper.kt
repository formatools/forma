package tools.forma.android.plugin

import Forma
import emptyDependency
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import tools.forma.deps.FormaDependency
import tools.forma.deps.applyDependencies
import tools.forma.validation.EmptyValidator

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
            repositoriesConfiguration = Forma.configuration.repositories
        )
    }

}