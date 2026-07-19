package tools.forma.deps.core

import tools.forma.core.target.TargetRegistry
import tools.forma.core.target.TargetRegistration
import tools.forma.core.target.TargetType
import tools.forma.core.target.targetType

/**
 * Associates ordered list of external [TargetPluginSpec]s with a [TargetType].
 * Lives in Gradle layer (not core) so core remains portable.
 * Path A: [registerTargetPlugin] on existing registered type.
 * Path B: [deriveTargetType] creates + registers derived type (core + plugins).
 */
interface TargetPluginRegistryApi {
    /** Register/append plugins for the given type (accumulates, dedups by plugin id). */
    fun register(type: TargetType, plugins: List<TargetPluginSpec>)

    /** Ordered plugins bound to this type (empty if none). */
    fun get(type: TargetType): List<TargetPluginSpec>

    /** Snapshot for debugging. */
    fun all(): Map<TargetType, List<TargetPluginSpec>>
}

class DefaultTargetPluginRegistry : TargetPluginRegistryApi {
    private val byType = mutableMapOf<TargetType, List<TargetPluginSpec>>()

    override fun register(type: TargetType, plugins: List<TargetPluginSpec>) {
        if (plugins.isEmpty()) return
        val existing = byType[type] ?: emptyList()
        val merged = existing.toMutableList()
        for (p in plugins) {
            if (merged.none { it.id == p.id }) {
                merged += p
            }
        }
        byType[type] = merged
    }

    override fun get(type: TargetType): List<TargetPluginSpec> = byType[type] ?: emptyList()

    override fun all(): Map<TargetType, List<TargetPluginSpec>> = byType.toMap()
}

/** Global registry for type → plugin bindings. Same scope as TargetRegistry usage. */
object TargetPluginRegistry : TargetPluginRegistryApi by DefaultTargetPluginRegistry()

/**
 * Path A: attach (or accumulate) plugins to an already-registered target type.
 * From this point, every call site using that type will auto-apply the plugin(s).
 */
fun registerTargetPlugin(targetType: TargetType, vararg plugins: TargetPluginSpec) {
    if (plugins.isNotEmpty()) {
        TargetPluginRegistry.register(targetType, plugins.toList())
    }
}

/**
 * Path B + general: derive a new TargetType that inherits base's allowed deps + content rules.
 * - Creates + registers cloned TargetRegistration into the provided coreRegistry (if non-null).
 * - Attaches the plugin list in TargetPluginRegistry under the derived type.
 * - Returns the new TargetType (usable for validators and DSLs).
 *
 * Callers that want Android semantics pass AndroidTargetRegistry as coreRegistry.
 */
fun deriveTargetType(
    id: String,
    base: TargetType,
    nameSuffix: String = base.nameSuffix,
    plugins: List<TargetPluginSpec> = emptyList(),
    coreRegistry: TargetRegistry? = null
): TargetType {
    val derived = targetType(id, nameSuffix, displayName = id)

    coreRegistry?.let { registry ->
        val baseReg = registry.all().firstOrNull { it.type.id == base.id }
        if (baseReg != null) {
            registry.register(baseReg.copy(type = derived))
        } else {
            // Fallback: register minimal so validatorFor etc still work (no allow-list means strict)
            // but in practice bases are always pre-registered.
            registry.register(
                TargetRegistration(
                    type = derived,
                    allowedDependencies = emptySet(),
                    contentRules = emptyList()
                )
            )
        }
    }

    if (plugins.isNotEmpty()) {
        registerTargetPlugin(derived, *plugins.toTypedArray())
    }
    return derived
}
