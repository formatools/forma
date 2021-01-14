package tools.forma.android.plugin

import tools.forma.deps.FormaDependency
import emptyDependency
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import tools.forma.deps.applyDependencies
import tools.forma.validation.EmptyValidator
import Forma
import kotlin.reflect.KClass

open class PluginWrapper<TPluginExtension : Any>(
        protected val pluginId: String,
        protected val pluginExtension: KClass<TPluginExtension>? = null,
        protected val dependencies: FormaDependency = emptyDependency(),
        protected val pluginConfiguration: (TPluginExtension.() -> Unit)? = null
) {

    init {
        if (pluginConfiguration != null) {
            require(pluginExtension != null) { "You must specify pluginExtension if you pass pluginConfiguration" }
        }
    }

    operator fun invoke(project: Project) {
        project.apply(plugin = pluginId)
        pluginExtension?.let {
            pluginConfiguration?.invoke(project.the(pluginExtension))
        }
        project.applyDependencies(
                validator = EmptyValidator,
                dependencies = dependencies,
                repositoriesConfiguration = Forma.configuration.repositories
        )
    }

}