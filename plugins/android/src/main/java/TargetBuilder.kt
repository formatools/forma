import org.gradle.api.Project
import tools.forma.deps.core.PluginWrapper

/**
 * @deprecated Chain API for external plugins. The target **type** owns plugins.
 * Use [deriveTargetType] + [registerTargetPlugin] (or Path B derived DSLs such as
 * navigationRes in sample) so call sites stay attributes-only.
 * See docs/TARGET-PLUGINS.md
 */
@Deprecated(
    message = "TargetBuilder chain removed. Plugin identity belongs to the TargetType via deriveTargetType / registerTargetPlugin. Call sites must be Bazel-flat. See docs/TARGET-PLUGINS.md",
    replaceWith = ReplaceWith("/* navigationRes(...) or androidRes(...) after type registration; no .withPlugin */"),
    level = DeprecationLevel.WARNING
)
class TargetBuilder(
        private val project: Project
) {

    @Deprecated("Use type-owned plugins", ReplaceWith("/* see docs/TARGET-PLUGINS.md */"), DeprecationLevel.WARNING)
    fun withPlugin(pluginWrapper: PluginWrapper<*>): TargetBuilder {
        pluginWrapper(project)
        return this
    }

    @Deprecated("Use type-owned plugins", ReplaceWith("/* see docs/TARGET-PLUGINS.md */"), DeprecationLevel.WARNING)
    fun withPlugins(vararg pluginWrappers: PluginWrapper<*>): TargetBuilder {
        pluginWrappers.forEach { withPlugin(it) }
        return this
    }

}
