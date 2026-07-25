package tools.forma.kmp.target

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
 * F-106: KMP matrix + registry behavior (no MPP apply yet).
 */
class KmpTargetRegistryTest {

    private data class TestRef(override val name: String) : TargetRef

    @BeforeTest
    fun setup() {
        registerKmpDefaults()
    }

    @Test
    fun `all four kmp types are registered by id`() {
        val t = KmpTargetTypes
        assertEquals(t.api, KmpTargetRegistry.get("kmp.api"))
        assertEquals(t.library, KmpTargetRegistry.get("kmp.library"))
        assertEquals(t.util, KmpTargetRegistry.get("kmp.util"))
        assertEquals(t.testUtil, KmpTargetRegistry.get("kmp.test-util"))
        assertEquals(4, KmpTargetRegistry.all().size)
    }

    @Test
    fun `restriction graph encodes kmp matrix from design doc`() {
        val t = KmpTargetTypes
        val g = KmpTargetRegistry.restrictionGraph()

        // kmp-api: only other kmp-api
        assertTrue(g.isAllowed(t.api, t.api))
        assertFalse(g.isAllowed(t.api, t.library))
        assertFalse(g.isAllowed(t.api, t.util))
        assertFalse(g.isAllowed(t.api, t.testUtil))

        // kmp-library: layered shared stack + contracts + utils
        assertTrue(g.isAllowed(t.library, t.api))
        assertTrue(g.isAllowed(t.library, t.library))
        assertTrue(g.isAllowed(t.library, t.util))
        assertTrue(g.isAllowed(t.library, t.testUtil))

        // kmp-util: only util peers
        assertTrue(g.isAllowed(t.util, t.util))
        assertFalse(g.isAllowed(t.util, t.api))
        assertFalse(g.isAllowed(t.util, t.library))
        assertFalse(g.isAllowed(t.util, t.testUtil))

        // kmp-test-util: may see contracts + library + util + peers
        assertTrue(g.isAllowed(t.testUtil, t.api))
        assertTrue(g.isAllowed(t.testUtil, t.library))
        assertTrue(g.isAllowed(t.testUtil, t.util))
        assertTrue(g.isAllowed(t.testUtil, t.testUtil))
    }

    @Test
    fun `validatorFor rejects disallowed project suffixes`() {
        val t = KmpTargetTypes
        val apiV = KmpTargetRegistry.validatorFor(t.api)
        apiV.validate(TestRef("core-kmp-api"))
        assertFailsWith<FormaValidationException> {
            apiV.validate(TestRef("shared-kmp-library"))
        }
        assertFailsWith<FormaValidationException> {
            apiV.validate(TestRef("feature-api")) // plain android/jvm api suffix
        }

        val libV = KmpTargetRegistry.validatorFor(t.library)
        libV.validate(TestRef("shared-kmp-library"))
        libV.validate(TestRef("core-kmp-api"))
        libV.validate(TestRef("net-kmp-util"))
        assertFailsWith<FormaValidationException> {
            libV.validate(TestRef("feature-impl"))
        }

        val utilV = KmpTargetRegistry.validatorFor(t.util)
        utilV.validate(TestRef("shared-kmp-util"))
        assertFailsWith<FormaValidationException> {
            utilV.validate(TestRef("shared-kmp-library"))
        }
    }

    @Test
    fun `selfValidator accepts kmp-prefixed suffixes only`() {
        val t = KmpTargetTypes
        KmpTargetRegistry.selfValidator(t.api).validate(TestRef("kmp-api"))
        KmpTargetRegistry.selfValidator(t.api).validate(TestRef("core-kmp-api"))
        assertFailsWith<FormaValidationException> {
            KmpTargetRegistry.selfValidator(t.api).validate(TestRef("feature-api"))
        }
        KmpTargetRegistry.selfValidator(t.library).validate(TestRef("shared-kmp-library"))
        KmpTargetRegistry.selfValidator(t.util).validate(TestRef("tools-kmp-util"))
        KmpTargetRegistry.selfValidator(t.testUtil).validate(TestRef("shared-kmp-test-util"))
        assertFailsWith<FormaValidationException> {
            KmpTargetRegistry.selfValidator(t.library).validate(TestRef("common-library"))
        }
    }

    @Test
    fun `validatorFor is identity cached for same type`() {
        val t = KmpTargetTypes
        val v1 = KmpTargetRegistry.validatorFor(t.library)
        val v2 = KmpTargetRegistry.validatorFor(t.library)
        assertSame(v1, v2)
        val s1 = KmpTargetRegistry.selfValidator(t.api)
        val s2 = KmpTargetRegistry.selfValidator(t.api)
        assertSame(s1, s2)
    }
}
