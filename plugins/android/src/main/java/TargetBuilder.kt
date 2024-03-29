import org.gradle.api.Project
import tools.forma.deps.core.PluginWrapper

class TargetBuilder(
        private val project: Project
) {

    fun withPlugin(pluginWrapper: PluginWrapper<*>): TargetBuilder {
        pluginWrapper(project)
        return this
    }

    fun withPlugins(vararg pluginWrappers: PluginWrapper<*>): TargetBuilder {
        pluginWrappers.forEach { withPlugin(it) }
        return this
    }

}
