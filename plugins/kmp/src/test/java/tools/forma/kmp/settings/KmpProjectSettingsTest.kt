package tools.forma.kmp.settings

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pure unit tests for KMP settings store and platform defaults (F-107).
 */
class KmpProjectSettingsTest {

    @BeforeTest
    @AfterTest
    fun resetStore() {
        KmpSettingsStore.clear()
    }

    @Test
    fun `defaults enable jvm and android with jvmTarget 11`() {
        val s = KmpProjectSettings()
        assertTrue(s.platforms.jvm)
        assertTrue(s.platforms.android)
        assertEquals("11", s.jvmTarget)
        assertEquals(DEFAULT_KMP_JVM_TARGET, s.jvmTarget)
        assertNull(s.kotlinVersion)
        assertNull(s.agpVersion)
    }

    @Test
    fun `platforms require at least one of jvm or android`() {
        assertFailsWith<IllegalArgumentException> {
            KmpPlatforms(jvm = false, android = false)
        }
        // jvm-only and android-only are valid
        KmpPlatforms(jvm = true, android = false)
        KmpPlatforms(jvm = false, android = true)
    }

    @Test
    fun `store and settingsOrDefaults`() {
        assertFalse(KmpSettingsStore.isSettingsStored)
        assertNull(KmpSettingsStore.settingsOrNull())
        val defaults = KmpSettingsStore.settingsOrDefaults()
        assertTrue(defaults.platforms.jvm)
        assertTrue(defaults.platforms.android)

        val custom =
            KmpProjectSettings(
                platforms = KmpPlatforms(jvm = true, android = false),
                jvmTarget = "17",
                kotlinVersion = "2.3.21",
                agpVersion = null,
            )
        KmpSettingsStore.store(custom)
        assertTrue(KmpSettingsStore.isSettingsStored)
        assertEquals(custom, KmpSettingsStore.settings)
        assertEquals(custom, KmpSettingsStore.settingsOrNull())
        assertEquals("17", KmpSettingsStore.settingsOrDefaults().jvmTarget)
        assertFalse(KmpSettingsStore.settings.platforms.android)
    }

    @Test
    fun `settings throws when never stored`() {
        assertFailsWith<IllegalStateException> {
            KmpSettingsStore.settings
        }
    }
}
