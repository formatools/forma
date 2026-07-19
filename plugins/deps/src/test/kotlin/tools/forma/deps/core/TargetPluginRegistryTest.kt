package tools.forma.deps.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import tools.forma.core.target.DefaultTargetRegistry
import tools.forma.core.target.TargetRegistration
import tools.forma.core.target.targetType

/**
 * F-071: type owns plugins — registry + derive without any call-site plugin API.
 */
class TargetPluginRegistryTest {

    @Test
    fun `DefaultTargetPluginRegistry registers ordered plugins and dedupes by id`() {
        val registry = DefaultTargetPluginRegistry()
        val type = targetType("test.res", "res")
        val first = targetPlugin("plugin.a")
        val second = targetPlugin("plugin.b")
        val dup = targetPlugin("plugin.a")

        registry.register(type, listOf(first, second))
        registry.register(type, listOf(dup, targetPlugin("plugin.c")))

        val ids = registry.get(type).map { it.id }
        assertEquals(listOf("plugin.a", "plugin.b", "plugin.c"), ids)
    }

    @Test
    fun `registerTargetPlugin Path A binds plugins on global registry`() {
        val type = targetType("test.path-a-${System.nanoTime()}", "res")
        val plugin = targetPlugin("androidx.navigation.safeargs.kotlin")

        registerTargetPlugin(type, plugin)

        val specs = TargetPluginRegistry.get(type)
        assertEquals(listOf("androidx.navigation.safeargs.kotlin"), specs.map { it.id })
        // Call site never passes plugin ids — type lookup is sufficient
        assertTrue(specs.isNotEmpty())
    }

    @Test
    fun `deriveTargetType Path B clones base registration and attaches plugins`() {
        val core = DefaultTargetRegistry()
        val base = targetType("test.base-res-${System.nanoTime()}", "res")
        val allowed = setOf(targetType("test.allowed", "library"))
        core.register(
            TargetRegistration(
                type = base,
                allowedDependencies = allowed,
                contentRules = emptyList()
            )
        )

        val safeArgs = targetPlugin("androidx.navigation.safeargs.kotlin")
        val derived = deriveTargetType(
            id = "test.navigation-res-${System.nanoTime()}",
            base = base,
            nameSuffix = "res",
            plugins = listOf(safeArgs),
            coreRegistry = core
        )

        assertEquals("res", derived.nameSuffix)
        val derivedReg = core.all().firstOrNull { it.type.id == derived.id }
        assertNotNull(derivedReg)
        assertEquals(allowed, derivedReg.allowedDependencies)

        val baseReg = core.all().first { it.type.id == base.id }
        assertEquals(allowed, baseReg.allowedDependencies)

        assertEquals(
            listOf("androidx.navigation.safeargs.kotlin"),
            TargetPluginRegistry.get(derived).map { it.id }
        )
        // Base type has no plugins unless Path A registered them
        assertTrue(TargetPluginRegistry.get(base).isEmpty())
    }

    @Test
    fun `empty plugin list leaves type unbound`() {
        val registry = DefaultTargetPluginRegistry()
        val type = targetType("test.empty", "impl")
        registry.register(type, emptyList())
        assertTrue(registry.get(type).isEmpty())
    }
}
