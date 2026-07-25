package tools.forma.jvm.target

import tools.forma.core.target.TargetRef
import tools.forma.core.validation.FormaValidationException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * F-030: pure JVM matrix + registry behavior (no AGP).
 */
class JvmTargetRegistryTest {

    private data class TestRef(override val name: String) : TargetRef

    @BeforeTest
    fun setup() {
        // Fresh defaults each test (replace semantics)
        registerJvmDefaults()
    }

    @Test
    fun `all six jvm types are registered by id`() {
        val t = JvmTargetTypes
        assertEquals(t.api, JvmTargetRegistry.get("jvm.api"))
        assertEquals(t.impl, JvmTargetRegistry.get("jvm.impl"))
        assertEquals(t.library, JvmTargetRegistry.get("jvm.library"))
        assertEquals(t.util, JvmTargetRegistry.get("jvm.util"))
        assertEquals(t.testUtil, JvmTargetRegistry.get("jvm.test-util"))
        assertEquals(t.binary, JvmTargetRegistry.get("jvm.binary"))
        assertEquals(6, JvmTargetRegistry.all().size)
    }

    @Test
    fun `restriction graph encodes dagger friendly matrix`() {
        val t = JvmTargetTypes
        val g = JvmTargetRegistry.restrictionGraph()

        // api
        assertTrue(g.isAllowed(t.api, t.api))
        assertTrue(g.isAllowed(t.api, t.library))
        assertFalse(g.isAllowed(t.api, t.impl))
        assertFalse(g.isAllowed(t.api, t.util))

        // impl: no impl→impl
        assertTrue(g.isAllowed(t.impl, t.api))
        assertTrue(g.isAllowed(t.impl, t.library))
        assertTrue(g.isAllowed(t.impl, t.util))
        assertTrue(g.isAllowed(t.impl, t.testUtil))
        assertFalse(g.isAllowed(t.impl, t.impl))

        // library
        assertTrue(g.isAllowed(t.library, t.util))
        assertTrue(g.isAllowed(t.library, t.testUtil))
        assertFalse(g.isAllowed(t.library, t.api))
        assertFalse(g.isAllowed(t.library, t.impl))
        assertFalse(g.isAllowed(t.library, t.library))

        // util
        assertTrue(g.isAllowed(t.util, t.util))
        assertTrue(g.isAllowed(t.util, t.library))
        assertFalse(g.isAllowed(t.util, t.api))
        assertFalse(g.isAllowed(t.util, t.impl))

        // test-util
        assertTrue(g.isAllowed(t.testUtil, t.testUtil))
        assertTrue(g.isAllowed(t.testUtil, t.util))
        assertTrue(g.isAllowed(t.testUtil, t.library))
        assertFalse(g.isAllowed(t.testUtil, t.api))
        assertFalse(g.isAllowed(t.testUtil, t.impl))

        // binary (composition root): may consume impls + api + shared; no binary→binary
        assertTrue(g.isAllowed(t.binary, t.api))
        assertTrue(g.isAllowed(t.binary, t.impl))
        assertTrue(g.isAllowed(t.binary, t.library))
        assertTrue(g.isAllowed(t.binary, t.util))
        assertTrue(g.isAllowed(t.binary, t.testUtil))
        assertFalse(g.isAllowed(t.binary, t.binary))

        // F-108 kmp edges covered in JvmTargetRegistryKmpEdgesTest
    }

    @Test
    fun `validatorFor rejects disallowed project suffixes`() {
        val t = JvmTargetTypes
        val apiV = JvmTargetRegistry.validatorFor(t.api)
        apiV.validate(TestRef("feature-api"))
        apiV.validate(TestRef("core-library"))
        assertFailsWith<FormaValidationException> { apiV.validate(TestRef("feature-impl")) }
        assertFailsWith<FormaValidationException> { apiV.validate(TestRef("shared-util")) }

        val implV = JvmTargetRegistry.validatorFor(t.impl)
        implV.validate(TestRef("feature-api"))
        assertFailsWith<FormaValidationException> { implV.validate(TestRef("other-impl")) }

        val libV = JvmTargetRegistry.validatorFor(t.library)
        libV.validate(TestRef("shared-util"))
        assertFailsWith<FormaValidationException> { libV.validate(TestRef("feature-api")) }
    }

    @Test
    fun `selfValidator accepts matching suffix`() {
        val t = JvmTargetTypes
        JvmTargetRegistry.selfValidator(t.api).validate(TestRef("api"))
        JvmTargetRegistry.selfValidator(t.api).validate(TestRef("feature-api"))
        assertFailsWith<FormaValidationException> {
            JvmTargetRegistry.selfValidator(t.api).validate(TestRef("feature-impl"))
        }
        JvmTargetRegistry.selfValidator(t.impl).validate(TestRef("home-impl"))
        JvmTargetRegistry.selfValidator(t.library).validate(TestRef("common-library"))
        JvmTargetRegistry.selfValidator(t.util).validate(TestRef("network-util"))
        JvmTargetRegistry.selfValidator(t.testUtil).validate(TestRef("shared-test-util"))
        JvmTargetRegistry.selfValidator(t.binary).validate(TestRef("binary"))
        JvmTargetRegistry.selfValidator(t.binary).validate(TestRef("app-binary"))
    }

    @Test
    fun `validatorFor is identity cached for same type`() {
        val t = JvmTargetTypes
        val v1 = JvmTargetRegistry.validatorFor(t.api)
        val v2 = JvmTargetRegistry.validatorFor(t.api)
        assertSame(v1, v2)
        val s1 = JvmTargetRegistry.selfValidator(t.impl)
        val s2 = JvmTargetRegistry.selfValidator(t.impl)
        assertSame(s1, s2)
    }
}
