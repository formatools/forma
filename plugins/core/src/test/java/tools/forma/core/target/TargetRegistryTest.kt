package tools.forma.core.target

import tools.forma.core.restriction.EdgeKind
import tools.forma.core.validation.FormaValidationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TargetRegistryTest {

    private data class TestRef(override val name: String) : TargetRef

    @Test
    fun `register + get by id roundtrips`() {
        val reg = DefaultTargetRegistry()
        val api = targetType("android.api", "api")
        reg.register(TargetRegistration(api, allowedDependencies = setOf(api)))

        assertEquals(api, reg.get("android.api"))
        assertEquals(null, reg.get("nope"))
    }

    @Test
    fun `getBySuffix returns all with same suffix (library collision)`() {
        val reg = DefaultTargetRegistry()
        val jvmLib = targetType("jvm.library", "library")
        val androidLib = targetType("android.library", "library")
        reg.register(TargetRegistration(jvmLib, allowedDependencies = emptySet()))
        reg.register(TargetRegistration(androidLib, allowedDependencies = emptySet()))

        val hits = reg.getBySuffix("library")
        assertEquals(2, hits.size)
        assertTrue(hits.any { it.id == "jvm.library" })
        assertTrue(hits.any { it.id == "android.library" })
    }

    @Test
    fun `restrictionGraph isAllowed matches registered edges`() {
        val reg = DefaultTargetRegistry()
        val api = targetType("android.api", "api")
        val impl = targetType("android.impl", "impl")
        val lib = targetType("android.library", "library")
        reg.register(TargetRegistration(api, allowedDependencies = setOf(api, lib)))
        reg.register(TargetRegistration(impl, allowedDependencies = setOf(api, lib))) // no impl

        val g = reg.restrictionGraph()
        assertTrue(g.isAllowed(api, api))
        assertTrue(g.isAllowed(api, lib))
        assertFalse(g.isAllowed(api, impl))
        assertTrue(g.isAllowed(impl, api))
        assertFalse(g.isAllowed(impl, impl)) // critical
    }

    @Test
    fun `validatorFor accepts names matching allowed suffixes and rejects others`() {
        val reg = DefaultTargetRegistry()
        val api = targetType("android.api", "api")
        val lib = targetType("jvm.library", "library")
        reg.register(TargetRegistration(api, allowedDependencies = setOf(api, lib)))

        val v = reg.validatorFor(api)
        v.validate(TestRef("api"))
        v.validate(TestRef("core-library"))
        val ex = assertFailsWith<FormaValidationException> {
            v.validate(TestRef("impl"))
        }
        assertTrue("api, library" in ex.message.orEmpty() || "api,library" in ex.message.orEmpty())
    }

    @Test
    fun `selfValidator accepts matching suffix and rejects wrong`() {
        val reg = DefaultTargetRegistry()
        val impl = targetType("android.impl", "impl")
        reg.register(TargetRegistration(impl, allowedDependencies = emptySet()))

        val v = reg.selfValidator(impl)
        v.validate(TestRef("impl"))
        v.validate(TestRef("feature-foo-impl"))
        assertFailsWith<FormaValidationException> {
            v.validate(TestRef("api"))
        }
    }

    @Test
    fun `validatorFor returns same instance on repeated calls (identity cache)`() {
        val reg = DefaultTargetRegistry()
        val api = targetType("x.api", "api")
        reg.register(TargetRegistration(api, allowedDependencies = setOf(api)))

        val v1 = reg.validatorFor(api)
        val v2 = reg.validatorFor(api)
        assertSame(v1, v2)
    }

    @Test
    fun `selfValidator returns cached instance`() {
        val reg = DefaultTargetRegistry()
        val t = targetType("x.t", "t")
        reg.register(TargetRegistration(t, allowedDependencies = emptySet()))

        val s1 = reg.selfValidator(t)
        val s2 = reg.selfValidator(t)
        assertSame(s1, s2)
    }

    @Test
    fun `re-register replaces and updates allowed set (no stale edges)`() {
        val reg = DefaultTargetRegistry()
        val c = targetType("c.c", "c")
        val d1 = targetType("d.one", "d1")
        val d2 = targetType("d.two", "d2")
        reg.register(TargetRegistration(c, allowedDependencies = setOf(d1)))
        reg.register(TargetRegistration(c, allowedDependencies = setOf(d2)))

        val g = reg.restrictionGraph()
        assertTrue(g.isAllowed(c, d2))
        assertFalse(g.isAllowed(c, d1))
    }

    @Test
    fun `restrictionGraph exposes ruleFor and edge defaults`() {
        val reg = DefaultTargetRegistry()
        val a = targetType("a.a", "a")
        val b = targetType("b.b", "b")
        reg.register(TargetRegistration(a, allowedDependencies = setOf(a, b)))

        val g = reg.restrictionGraph()
        val rule = g.ruleFor(a)
        assertNotNull(rule)
        assertEquals(setOf(a, b), rule!!.allowedDependencies)
        assertEquals(setOf(EdgeKind.IMPLEMENTATION), rule.edgeKinds)
    }
}
