package tools.forma.core.target

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NameMatcherTest {

    private val api = targetType("android.api", "api")
    private val impl = targetType("android.impl", "impl")
    private val library = targetType("android.library", "library")
    private val composeWidget = targetType("android.compose-widget", "compose-widget")

    @Test
    fun `SuffixNameMatcher matches exact suffix`() {
        assertTrue(SuffixNameMatcher.matches("api", api))
        assertTrue(SuffixNameMatcher.matches("impl", impl))
        assertTrue(SuffixNameMatcher.matches("library", library))
        assertTrue(SuffixNameMatcher.matches("compose-widget", composeWidget))
    }

    @Test
    fun `SuffixNameMatcher matches dashed suffix`() {
        assertTrue(SuffixNameMatcher.matches("feature-home-api", api))
        assertTrue(SuffixNameMatcher.matches("feature-characters-impl", impl))
        assertTrue(SuffixNameMatcher.matches("core-library", library))
        assertTrue(SuffixNameMatcher.matches("common-greeting-compose-widget", composeWidget))
    }

    @Test
    fun `SuffixNameMatcher rejects non-matching`() {
        assertFalse(SuffixNameMatcher.matches("impl", api))
        assertFalse(SuffixNameMatcher.matches("api", impl))
        assertFalse(SuffixNameMatcher.matches("feature", library))
        assertFalse(SuffixNameMatcher.matches("library", composeWidget))
        assertFalse(SuffixNameMatcher.matches("compose-widget", api))
    }

    @Test
    fun `SuffixNameMatcher is case sensitive on suffix part`() {
        // current behavior (ignoreCase=false in endsWith)
        assertFalse(SuffixNameMatcher.matches("Feature-Api", api))
        assertFalse(SuffixNameMatcher.matches("LIB", library))
    }
}
