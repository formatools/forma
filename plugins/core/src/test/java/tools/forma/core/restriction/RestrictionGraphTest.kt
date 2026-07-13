package tools.forma.core.restriction

import tools.forma.core.target.targetType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RestrictionGraphTest {

    private val api = targetType("android.api", "api")
    private val impl = targetType("android.impl", "impl")
    private val library = targetType("android.library", "library")
    private val util = targetType("android.util", "util")
    private val res = targetType("android.res", "res")

    // Simulate shared suffix collision per forma-core-api §7 (different ids, same suffix)
    private val jvmLibrary = targetType("jvm.library", "library")
    private val androidLibrary = targetType("android.library", "library")

    @Test
    fun `empty graph denies everything`() {
        val g = MutableRestrictionGraph()
        assertFalse(g.isAllowed(api, api))
        assertTrue(g.allowedTypes(impl).isEmpty())
    }

    @Test
    fun `allow basic edges and isAllowed works by TargetType id`() {
        val g = MutableRestrictionGraph()
        g.allow(api, api, library)
        g.allow(impl, api, library, util, res)

        assertTrue(g.isAllowed(api, api))
        assertTrue(g.isAllowed(api, library))
        assertFalse(g.isAllowed(api, impl)) // not listed
        assertTrue(g.isAllowed(impl, api))
        assertTrue(g.isAllowed(impl, res))
        assertFalse(g.isAllowed(impl, impl)) // critical product rule
    }

    @Test
    fun `impl never allows impl (critical Dagger boundary)`() {
        val g = MutableRestrictionGraph()
        g.allow(impl, api, library, util) // deliberately no impl

        assertFalse(g.isAllowed(impl, impl))
        assertFalse(g.isAllowed(impl, impl, EdgeKind.IMPLEMENTATION))
    }

    @Test
    fun `allow is idempotent and merges`() {
        val g = MutableRestrictionGraph()
        g.allow(api, api)
        g.allow(api, library)
        g.allow(api, api) // dup

        assertEquals(setOf(api, library), g.allowedTypes(api))
    }

    @Test
    fun `suffix collision semantics - allow if ANY registered type with suffix is allowed (by name lookup sim)`() {
        // Graph is id-keyed. Consumer of restriction (future validator) may resolve by name.
        // Per §7: if checking a dep by name "foo-library", and any type with suffix "library" is allowed
        // for the consumer (jvm or android), then treat as allowed (preserve current behavior).
        val g = MutableRestrictionGraph()
        // Consumer allows the android library type
        g.allow(impl, api, androidLibrary, util)

        // Simulate name-based resolution for a dep project named "core-library"
        // (which could be jvm or android flavored)
        val candidates = listOf(jvmLibrary, androidLibrary).filter { it.nameSuffix == "library" }

        val allowedByName = candidates.any { g.isAllowed(impl, it) }
        // Since androidLibrary is allowed, name "core-library" should be accepted under collision rule
        assertTrue(allowedByName)

        // If neither is allowed, deny
        val g2 = MutableRestrictionGraph()
        g2.allow(impl, api, util) // no library types
        val allowedByName2 = listOf(jvmLibrary, androidLibrary).any { g2.isAllowed(impl, it) }
        assertFalse(allowedByName2)
    }

    @Test
    fun `ruleFor and allowedTypes respect edge default`() {
        val g = MutableRestrictionGraph()
        g.allow(impl, api)
        g.allowEdges(impl, EdgeKind.IMPLEMENTATION) // explicit but default

        assertEquals(setOf(api), g.allowedTypes(impl))
        assertEquals(setOf(api), g.allowedTypes(impl, EdgeKind.IMPLEMENTATION))
        assertTrue(g.allowedTypes(impl, EdgeKind.TEST).isEmpty())
    }

    @Test
    fun `allRules snapshot`() {
        val g = MutableRestrictionGraph()
        g.allow(api, api)
        assertEquals(1, g.allRules().size)
        assertEquals(api, g.allRules().keys.first())
    }

    @Test
    fun `jvm library and android library consumers have distinct non-merged allow lists`() {
        // Documents F-021 fix: separate ids (jvm.library vs android.library) for same suffix
        // ensures graph.allow() for one does not merge into the other (see DEPENDENCY-MATRIX
        // and forma-core-api.md §7). Uses synthetic types (core test stays independent of android kit).
        val jvmLib = targetType("jvm.library", "library")
        val androidLib = targetType("android.library", "library")
        val jvmUtil = targetType("jvm.util", "util")
        val androidUtil = targetType("android.android-util", "android-util")
        val testUtilT = targetType("android.test-util", "test-util")
        val resT = targetType("android.res", "res")

        val g = MutableRestrictionGraph()

        // JVM library matrix (per library.kt + DEPENDENCY-MATRIX)
        g.allow(jvmLib, jvmUtil, testUtilT)

        // androidLibrary matrix (per androidLibrary.kt + DEPENDENCY-MATRIX)
        g.allow(androidLib, androidLib, jvmUtil, androidUtil, testUtilT, resT, api)

        // Verify distinct rules (no accidental merge from duplicate consumer id)
        val jvmAllowed = g.allowedTypes(jvmLib)
        val androidAllowed = g.allowedTypes(androidLib)

        assertEquals(setOf(jvmUtil, testUtilT), jvmAllowed)
        assertTrue(androidAllowed.contains(androidLib))
        assertTrue(androidAllowed.contains(jvmUtil))
        assertTrue(androidAllowed.contains(androidUtil))
        assertTrue(androidAllowed.contains(testUtilT))
        assertTrue(androidAllowed.contains(resT))
        assertTrue(androidAllowed.contains(api))
        assertEquals(6, androidAllowed.size)
        assertFalse(jvmAllowed.contains(api))
        assertFalse(jvmAllowed.contains(resT))
        assertFalse(jvmAllowed == androidAllowed)
    }
}
