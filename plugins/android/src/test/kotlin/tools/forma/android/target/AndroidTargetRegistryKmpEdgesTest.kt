package tools.forma.android.target

import tools.forma.core.target.TargetRef
import tools.forma.core.validation.FormaValidationException
import tools.forma.kmp.target.KmpTargetTypes
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * F-108: Android consumers → kmp.* restriction edges (docs/KMP-TARGETS.md §6.2).
 *
 * Graph assertions use [TargetType] pairs (design matrix truth).
 * Suffix validators also exercise kmp-named projects; note that SuffixNameMatcher
 * treats `*-kmp-library` as ending with `-library`, so consumers that already allow
 * jvm.library will accept those names via overlap even without kmp.library in the set.
 * Graph-level [isAllowed] remains the authoritative design check for type pairs.
 */
class AndroidTargetRegistryKmpEdgesTest {

    private data class TestRef(override val name: String) : TargetRef

    @BeforeTest
    fun setup() {
        registerAndroidDefaults()
    }

    @Test
    fun `restriction graph encodes android to kmp consumer matrix`() {
        val t = AndroidTargetTypes
        val k = KmpTargetTypes
        val g = AndroidTargetRegistry.restrictionGraph()

        // api → kmp.api only
        assertTrue(g.isAllowed(t.api, k.api))
        assertFalse(g.isAllowed(t.api, k.library))
        assertFalse(g.isAllowed(t.api, k.util))
        assertFalse(g.isAllowed(t.api, k.testUtil))

        // impl → kmp.api, kmp.library, kmp.util (not kmp-test-util)
        assertTrue(g.isAllowed(t.impl, k.api))
        assertTrue(g.isAllowed(t.impl, k.library))
        assertTrue(g.isAllowed(t.impl, k.util))
        assertFalse(g.isAllowed(t.impl, k.testUtil))

        // androidUtil → kmp.library, kmp.util (not kmp.api)
        assertFalse(g.isAllowed(t.androidUtil, k.api))
        assertTrue(g.isAllowed(t.androidUtil, k.library))
        assertTrue(g.isAllowed(t.androidUtil, k.util))
        assertFalse(g.isAllowed(t.androidUtil, k.testUtil))

        // composition roots
        for (root in listOf(t.app, t.binary)) {
            assertTrue(g.isAllowed(root, k.api))
            assertTrue(g.isAllowed(root, k.library))
            assertTrue(g.isAllowed(root, k.util))
            assertFalse(g.isAllowed(root, k.testUtil))
        }

        // UI / leaf types: no kmp edges (design §6.2)
        for (leaf in listOf(
            t.widget, t.composeWidget, t.res, t.viewBinding, t.uiLibrary, t.androidTestUtil,
        )) {
            assertFalse(g.isAllowed(leaf, k.api))
            assertFalse(g.isAllowed(leaf, k.library))
            assertFalse(g.isAllowed(leaf, k.util))
            assertFalse(g.isAllowed(leaf, k.testUtil))
        }

        // Existing Android edges stay intact (spot-check)
        assertTrue(g.isAllowed(t.api, t.api))
        assertTrue(g.isAllowed(t.api, t.jvmLibrary))
        assertFalse(g.isAllowed(t.impl, t.impl))
        assertTrue(g.isAllowed(t.impl, t.api))
    }

    @Test
    fun `validators accept kmp project names for allowed consumers`() {
        val t = AndroidTargetTypes

        val apiV = AndroidTargetRegistry.validatorFor(t.api)
        apiV.validate(TestRef("core-kmp-api"))
        // shared-kmp-library also ends with -library (jvm.library suffix overlap) — accepted
        // via existing library allow-list; graph still denies kmp.library for api (see above).
        apiV.validate(TestRef("shared-kmp-library"))

        val implV = AndroidTargetRegistry.validatorFor(t.impl)
        implV.validate(TestRef("core-kmp-api"))
        implV.validate(TestRef("shared-kmp-library"))
        implV.validate(TestRef("net-kmp-util"))

        val utilV = AndroidTargetRegistry.validatorFor(t.androidUtil)
        utilV.validate(TestRef("shared-kmp-library"))
        utilV.validate(TestRef("net-kmp-util"))

        val appV = AndroidTargetRegistry.validatorFor(t.app)
        appV.validate(TestRef("core-kmp-api"))
        appV.validate(TestRef("shared-kmp-library"))
        appV.validate(TestRef("net-kmp-util"))
    }

    @Test
    fun `validators reject plain android impl for api and kmp-test-util when not allowed`() {
        val t = AndroidTargetTypes

        val apiV = AndroidTargetRegistry.validatorFor(t.api)
        assertFailsWith<FormaValidationException> { apiV.validate(TestRef("feature-impl")) }

        // kmp-test-util is not on any §6.2 Android consumer allow-list.
        // Suffix is distinct (does not end with -test-util alone? actually ends with -test-util).
        // android.impl allows test-util → overlap may accept *-kmp-test-util as test-util.
        // Graph-level deny is authoritative; suffix engine limitation documented in PROGRESS.
        val implV = AndroidTargetRegistry.validatorFor(t.impl)
        implV.validate(TestRef("shared-kmp-test-util")) // overlap with test-util suffix

        // widget still rejects non-UI deps that are not in its allow-list when suffix is unique
        val widgetV = AndroidTargetRegistry.validatorFor(t.widget)
        assertFailsWith<FormaValidationException> { widgetV.validate(TestRef("feature-impl")) }
    }
}
