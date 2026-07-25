package tools.forma.jvm.target

import tools.forma.core.target.TargetRef
import tools.forma.core.validation.FormaValidationException
import tools.forma.kmp.target.KmpTargetTypes
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * F-108: JVM consumers → kmp.* restriction edges (docs/KMP-TARGETS.md §6.2).
 *
 * Prefer graph-level [isAllowed] for design matrix truth. Suffix overlap note:
 * `*-kmp-api` ends with `-api`, `*-kmp-library` with `-library`, `*-kmp-util` with `-util`.
 */
class JvmTargetRegistryKmpEdgesTest {

    private data class TestRef(override val name: String) : TargetRef

    @BeforeTest
    fun setup() {
        registerJvmDefaults()
    }

    @Test
    fun `restriction graph encodes jvm to kmp consumer matrix`() {
        val t = JvmTargetTypes
        val k = KmpTargetTypes
        val g = JvmTargetRegistry.restrictionGraph()

        // api → kmp.api only
        assertTrue(g.isAllowed(t.api, k.api))
        assertFalse(g.isAllowed(t.api, k.library))
        assertFalse(g.isAllowed(t.api, k.util))
        assertFalse(g.isAllowed(t.api, k.testUtil))

        // impl / binary → kmp.api, kmp.library, kmp.util
        for (consumer in listOf(t.impl, t.binary)) {
            assertTrue(g.isAllowed(consumer, k.api))
            assertTrue(g.isAllowed(consumer, k.library))
            assertTrue(g.isAllowed(consumer, k.util))
            assertFalse(g.isAllowed(consumer, k.testUtil))
        }

        // library / util → kmp.library, kmp.util (not kmp.api)
        for (consumer in listOf(t.library, t.util)) {
            assertFalse(g.isAllowed(consumer, k.api))
            assertTrue(g.isAllowed(consumer, k.library))
            assertTrue(g.isAllowed(consumer, k.util))
            assertFalse(g.isAllowed(consumer, k.testUtil))
        }

        // test-util: no kmp edges in v1 §6.2
        assertFalse(g.isAllowed(t.testUtil, k.api))
        assertFalse(g.isAllowed(t.testUtil, k.library))
        assertFalse(g.isAllowed(t.testUtil, k.util))
        assertFalse(g.isAllowed(t.testUtil, k.testUtil))

        // Existing JVM edges stay intact (spot-check)
        assertTrue(g.isAllowed(t.api, t.api))
        assertTrue(g.isAllowed(t.api, t.library))
        assertFalse(g.isAllowed(t.impl, t.impl))
        assertTrue(g.isAllowed(t.binary, t.impl))
    }

    @Test
    fun `validators accept kmp project names for allowed consumers`() {
        val t = JvmTargetTypes

        JvmTargetRegistry.validatorFor(t.api).validate(TestRef("core-kmp-api"))

        val implV = JvmTargetRegistry.validatorFor(t.impl)
        implV.validate(TestRef("core-kmp-api"))
        implV.validate(TestRef("shared-kmp-library"))
        implV.validate(TestRef("net-kmp-util"))

        val libV = JvmTargetRegistry.validatorFor(t.library)
        libV.validate(TestRef("shared-kmp-library"))
        libV.validate(TestRef("net-kmp-util"))

        val utilV = JvmTargetRegistry.validatorFor(t.util)
        utilV.validate(TestRef("shared-kmp-library"))
        utilV.validate(TestRef("net-kmp-util"))

        val binaryV = JvmTargetRegistry.validatorFor(t.binary)
        binaryV.validate(TestRef("core-kmp-api"))
        binaryV.validate(TestRef("shared-kmp-library"))
        binaryV.validate(TestRef("net-kmp-util"))
    }

    @Test
    fun `validators still reject disallowed plain jvm suffixes`() {
        val t = JvmTargetTypes
        val apiV = JvmTargetRegistry.validatorFor(t.api)
        assertFailsWith<FormaValidationException> { apiV.validate(TestRef("feature-impl")) }
        assertFailsWith<FormaValidationException> { apiV.validate(TestRef("shared-util")) }

        val libV = JvmTargetRegistry.validatorFor(t.library)
        assertFailsWith<FormaValidationException> { libV.validate(TestRef("feature-api")) }
        assertFailsWith<FormaValidationException> { libV.validate(TestRef("other-impl")) }
    }
}
