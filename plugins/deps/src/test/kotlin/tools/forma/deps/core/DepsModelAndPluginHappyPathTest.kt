package tools.forma.deps.core

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import dep
import forProductFlavor
import ksp
import plus
import tools.forma.core.target.targetType
import transitiveDep
import transitiveDeps
import tools.forma.deps.core.CompileOnly
import tools.forma.deps.core.CustomConfiguration
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FileDependency
import tools.forma.deps.core.FileSpec
import tools.forma.deps.core.Implementation
import tools.forma.deps.core.Ksp
import tools.forma.deps.core.MixedDependency
import tools.forma.deps.core.NameSpec
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.PlatformDependency
import tools.forma.deps.core.PlatformSpec
import tools.forma.deps.core.TargetPluginRegistry
import tools.forma.deps.core.deriveTargetType
import tools.forma.deps.core.productFlavorImplementation
import tools.forma.deps.core.registerTargetPlugin
import tools.forma.deps.core.targetPlugin
import tools.forma.deps.core.DefaultTargetPluginRegistry

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
    fun `String dep is non-transitive Implementation and preserves GAV`() {
        val viaProp = "g:a:1".dep
        val spec = viaProp.names.single()
        assertEquals("g:a:1", spec.name)
        assertEquals(Implementation, spec.config)
        assertEquals(false, spec.transitive)
    }

    @Test
    fun `String transitiveDep is transitive Implementation and preserves GAV`() {
        val viaProp = "g:a:1".transitiveDep
        val viaFun = transitiveDeps("g:a:1")
        val propSpec = viaProp.names.single()
        val funSpec = viaFun.names.single()
        assertEquals("g:a:1", propSpec.name)
        assertEquals(Implementation, propSpec.config)
        assertTrue(propSpec.transitive)
        assertEquals(funSpec.name, propSpec.name)
        assertEquals(funSpec.config, propSpec.config)
        assertEquals(funSpec.transitive, propSpec.transitive)
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
    fun `plus preserves PlatformDependency BOM with named artifacts`() {
        val bom = PlatformDependency(listOf(PlatformSpec("com.google.firebase:firebase-bom:33.16.0", Implementation)))
        val crash = "com.google.firebase:firebase-crashlytics".dep
        val mixed = bom + crash
        assertEquals(1, mixed.platforms.size)
        assertEquals("com.google.firebase:firebase-bom:33.16.0", mixed.platforms.single().name)
        assertEquals(1, mixed.names.size)
        assertEquals("com.google.firebase:firebase-crashlytics", mixed.names.single().name)
        // platforms appear in flattened dependency list for forEach
        assertTrue(mixed.dependency.any { it is PlatformSpec })
        assertTrue(mixed.dependency.any { it is NameSpec })
    }

    @Test
    fun `productFlavorImplementation maps to AGP flavor Implementation config`() {
        assertEquals("prodImplementation", productFlavorImplementation("prod").name)
        assertEquals("demoImplementation", productFlavorImplementation("demo").name)
        assertFailsWithBlankFlavor()
    }

    private fun assertFailsWithBlankFlavor() {
        try {
            productFlavorImplementation(" ")
            throw AssertionError("expected blank flavor to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("blank"))
        }
    }

    @Test
    fun `NamedDependency forProductFlavor remaps config and preserves transitive + flags`() {
        val flagged =
            NamedDependency(
                listOf(
                    NameSpec(
                        name = "com.google.firebase:firebase-analytics",
                        config = Implementation,
                        transitive = true,
                        featureFlag = "useFirebase",
                        featureFlagExpected = true,
                    ),
                ),
            ).forProductFlavor("prod")
        val spec = flagged.names.single()
        assertEquals("com.google.firebase:firebase-analytics", spec.name)
        assertEquals(CustomConfiguration("prodImplementation"), spec.config)
        assertEquals("prodImplementation", spec.config.name)
        assertTrue(spec.transitive)
        assertEquals("useFirebase", spec.featureFlag)
        assertEquals(true, spec.featureFlagExpected)
    }

    @Test
    fun `PlatformDependency forProductFlavor remaps BOM config and preserves transitive`() {
        val bom =
            PlatformDependency(
                listOf(PlatformSpec("com.google.firebase:firebase-bom:33.16.0", Implementation, transitive = true)),
            ).forProductFlavor("prod")
        val spec = bom.names.single()
        assertEquals("com.google.firebase:firebase-bom:33.16.0", spec.name)
        assertEquals(CustomConfiguration("prodImplementation"), spec.config)
        assertTrue(spec.transitive)
    }

    @Test
    fun `forProductFlavor composes with plus for NiA prod stack`() {
        val stack =
            transitiveDeps("com.google.firebase:firebase-analytics").forProductFlavor("prod") +
                PlatformDependency(
                    listOf(PlatformSpec("com.google.firebase:firebase-bom:33.16.0", Implementation)),
                ).forProductFlavor("prod")
        assertEquals(1, stack.names.size)
        assertEquals(1, stack.platforms.size)
        assertEquals("prodImplementation", stack.names.single().config.name)
        assertEquals("prodImplementation", stack.platforms.single().config.name)
        assertTrue(stack.names.single().transitive)
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
