import org.gradle.api.Project
import tools.forma.deps.FormaDependency
import tools.forma.android.plugin.PluginWrapper
import kotlin.reflect.KClass

class FormaBuilder(
        private val project: Project
) {

    fun <TPluginExtension : Any> withPlugin(
            pluginId: String,
            pluginExtension: KClass<TPluginExtension>,
            dependencies: FormaDependency = emptyDependency(),
            pluginConfiguration: (TPluginExtension.() -> Unit)? = null
    ): FormaBuilder = withPlugin(
            PluginWrapper(pluginId, pluginExtension, dependencies, pluginConfiguration)
    )

    // TODO: maybe should crate withPlugins(vararg pluginWrapper: PluginWrapper<*>)
    fun withPlugin(pluginWrapper: PluginWrapper<*>): FormaBuilder {
        pluginWrapper(project)
        return this
    }

}