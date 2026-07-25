package tools.forma.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.api.JavaVersion

/**
 * F-099 / GH #126: project-global named feature flags (pure model).
 */
class FormaFeatureFlagsTest {

    @Test
    fun `empty defaults unknown names to false`() {
        val flags = FormaFeatureFlags()
        assertFalse(flags["daggerReflect"])
        assertFalse(flags.isEnabled("anything"))
        assertFalse(flags.contains("daggerReflect"))
        assertTrue(flags.toMap().isEmpty())
        assertEquals(FormaFeatureFlags.EMPTY, FormaFeatureFlags())
    }

    @Test
    fun `declared true and false are readable`() {
        val flags =
            FormaFeatureFlags(
                "daggerReflect" to true,
                "offlineMode" to false,
            )
        assertTrue(flags["daggerReflect"])
        assertTrue(flags.isEnabled("daggerReflect"))
        assertFalse(flags["offlineMode"])
        assertFalse(flags.isEnabled("offlineMode"))
        assertTrue(flags.contains("offlineMode"))
        assertFalse(flags.contains("missing"))
        assertEquals(
            mapOf("daggerReflect" to true, "offlineMode" to false),
            flags.toMap(),
        )
    }

    @Test
    fun `map constructor matches vararg pairs`() {
        val fromMap = FormaFeatureFlags(mapOf("a" to true, "b" to false))
        val fromPairs = FormaFeatureFlags("a" to true, "b" to false)
        assertEquals(fromMap.toMap(), fromPairs.toMap())
        assertTrue(fromMap.isEnabled("a"))
        assertFalse(fromMap.isEnabled("b"))
    }

    @Test
    fun `require returns declared value and throws for unknown`() {
        val flags = FormaFeatureFlags("strict" to true)
        assertTrue(flags.require("strict"))
        val ex =
            assertFailsWith<IllegalArgumentException> {
                flags.require("neverDeclared")
            }
        assertTrue(ex.message!!.contains("neverDeclared"))
        assertTrue(ex.message!!.contains("androidProjectConfiguration"))
    }

    @Test
    fun `AndroidProjectSettings stores featureFlags`() {
        val featureFlags = FormaFeatureFlags("daggerReflect" to true)
        val settings =
            AndroidProjectSettings(
                minSdk = 23,
                targetSdk = 35,
                compileSdk = 35,
                kotlinVersion = "2.0.0",
                agpVersion = "9.3.0",
                repositories = {},
                compose = false,
                composeCompilerVersion = "2.0.0",
                vectorDrawablesUseSupportLibrary = false,
                javaVersionCompatibility = JavaVersion.VERSION_11,
                mandatoryOwners = false,
                featureFlags = featureFlags,
            )
        assertEquals(featureFlags, settings.featureFlags)
        assertTrue(settings.featureFlags.isEnabled("daggerReflect"))
        assertFalse(settings.featureFlags.isEnabled("missing"))
    }

    @Test
    fun `default AndroidProjectSettings featureFlags are empty`() {
        val settings =
            AndroidProjectSettings(
                minSdk = 23,
                targetSdk = 35,
                compileSdk = 35,
                kotlinVersion = "2.0.0",
                agpVersion = "9.3.0",
                repositories = {},
                compose = false,
                composeCompilerVersion = "2.0.0",
                vectorDrawablesUseSupportLibrary = false,
                javaVersionCompatibility = JavaVersion.VERSION_11,
                mandatoryOwners = false,
            )
        assertEquals(FormaFeatureFlags.EMPTY, settings.featureFlags)
        assertFalse(settings.featureFlags["x"])
    }
}
