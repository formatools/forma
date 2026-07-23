package tools.forma.deps.core

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import ksp
import tools.forma.core.target.targetType

/**
 * Happy-path unit coverage for pure deps model + type-owned plugin registry helpers
 * (no Gradle Project apply paths).
 */
class DepsModelAndPluginHappyPathTest {

    @Test
    fun `configuration type names match Gradle config ids`() {
        assertEquals("implementation", Implementation.name)
        assertEquals("compileOnly", CompileOnly.name)
        assertEquals("runtimeOnly", RuntimeOnly.name)
        assertEquals("annotationProcessor", AnnotationProcessor.name)
        assertEquals("ksp", Ksp.name)
        assertEquals("ksp", CustomConfiguration("ksp").name)
    }

    @Test
    fun `ksp helpers place specs on Ksp configuration`() {
        val viaFun = ksp("g:processor:1")
        val viaExt = "g:processor:1".ksp
        assertEquals(1, viaFun.names.size)
        assertEquals(Ksp, viaFun.names.single().config)
        assertTrue(viaFun.names.single().transitive)
        assertEquals("g:processor:1", viaFun.names.single().name)
        assertEquals(Ksp, viaExt.names.single().config)
    }

    @Test
    fun `dependency model constructors preserve specs`() {
        val name = NameSpec("g:a:1", Implementation, transitive = true)
        val platform = PlatformSpec("g:bom:1", Implementation)
        val file = FileSpec(File("/tmp/x.jar"), CompileOnly)

        val named = NamedDependency(listOf(name))
        val plat = PlatformDependency(listOf(platform))
        val files = FileDependency(listOf(file))
        val mixed = MixedDependency(names = listOf(name), files = listOf(file))

        assertEquals(listOf(name), named.names)
        assertEquals(listOf(platform), plat.names)
        assertEquals(listOf(file), files.files)
        assertEquals(listOf(name), mixed.names)
        assertEquals(listOf(file), mixed.files)
        assertSame(EmptyDependency, EmptyDependency)
        assertTrue(EmptyDependency.dependency.isEmpty())
    }

    @Test
    fun `targetPlugin factory defaults empty companion deps`() {
        val spec = targetPlugin("androidx.navigation.safeargs.kotlin")
        assertEquals("androidx.navigation.safeargs.kotlin", spec.id)
        assertSame(EmptyDependency, spec.dependencies)

        val withDeps = targetPlugin("x.y", NamedDependency(listOf(NameSpec("g:a:1", Ksp))))
        assertEquals("x.y", withDeps.id)
        assertTrue(withDeps.dependencies is NamedDependency)
    }

    @Test
    fun `DefaultTargetPluginRegistry all snapshot and empty get`() {
        val registry = DefaultTargetPluginRegistry()
        val type = targetType("test.snap-${System.nanoTime()}", "res")
        assertTrue(registry.get(type).isEmpty())
        assertTrue(registry.all().isEmpty())

        registry.register(type, listOf(targetPlugin("p.one"), targetPlugin("p.two")))
        assertEquals(listOf("p.one", "p.two"), registry.get(type).map { it.id })
        assertEquals(1, registry.all().size)
        assertEquals(listOf("p.one", "p.two"), registry.all().getValue(type).map { it.id })
    }

    @Test
    fun `registerTargetPlugin no-ops on empty varargs`() {
        val type = targetType("test.empty-reg-${System.nanoTime()}", "impl")
        registerTargetPlugin(type)
        assertTrue(TargetPluginRegistry.get(type).isEmpty())
    }

    @Test
    fun `deriveTargetType without coreRegistry still attaches plugins`() {
        val base = targetType("test.base-only-${System.nanoTime()}", "res")
        val derived =
            deriveTargetType(
                id = "test.derived-only-${System.nanoTime()}",
                base = base,
                plugins = listOf(targetPlugin("plugin.only")),
                coreRegistry = null,
            )
        assertEquals("res", derived.nameSuffix)
        assertEquals(listOf("plugin.only"), TargetPluginRegistry.get(derived).map { it.id })
    }

    @Test
    fun `deriveTargetType without base registration uses empty allow-list fallback`() {
        val core = tools.forma.core.target.DefaultTargetRegistry()
        val missingBase = targetType("test.missing-base-${System.nanoTime()}", "res")
        val derived =
            deriveTargetType(
                id = "test.derived-fallback-${System.nanoTime()}",
                base = missingBase,
                plugins = emptyList(),
                coreRegistry = core,
            )
        val reg = core.all().first { it.type.id == derived.id }
        assertTrue(reg.allowedDependencies.isEmpty())
    }
}
