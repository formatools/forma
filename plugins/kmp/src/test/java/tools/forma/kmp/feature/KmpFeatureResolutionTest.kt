package tools.forma.kmp.feature

import tools.forma.kmp.settings.KmpPlatforms
import tools.forma.kmp.settings.KmpProjectSettings
import tools.forma.kmp.settings.KmpSettingsStore
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pure unit tests for applicator settings resolution / platform flags (F-107).
 * No full AGP Project — Project apply paths stay out of jacoco happy-path gate.
 */
class KmpFeatureResolutionTest {

    @BeforeTest
    @AfterTest
    fun resetStore() {
        KmpSettingsStore.clear()
    }

    @Test
    fun `resolveSettings uses defaults when store empty`() {
        val s = KmpFeatureResolution.resolveSettings(store = null)
        assertTrue(s.platforms.jvm)
        assertTrue(s.platforms.android)
        assertEquals("11", KmpFeatureResolution.jvmTarget(s))
    }

    @Test
    fun `resolveSettings reads store when present`() {
        val custom =
            KmpProjectSettings(
                platforms = KmpPlatforms(jvm = true, android = false),
                jvmTarget = "17",
            )
        KmpSettingsStore.store(custom)
        val s = KmpFeatureResolution.resolveSettings()
        assertEquals(custom, s)
        assertEquals(false, KmpFeatureResolution.platforms(s).android)
        assertEquals("17", KmpFeatureResolution.jvmTarget(s))
    }

    @Test
    fun `androidApplyErrorOrNull null when android platform off`() {
        val s =
            KmpProjectSettings(
                platforms = KmpPlatforms(jvm = true, android = false),
            )
        assertNull(
            KmpFeatureResolution.androidApplyErrorOrNull(
                settings = s,
                androidSettingsStored = false,
            )
        )
    }

    @Test
    fun `androidApplyErrorOrNull when android on without Android settings`() {
        val s = KmpProjectSettings() // android default true
        val err =
            KmpFeatureResolution.androidApplyErrorOrNull(
                settings = s,
                androidSettingsStored = false,
            )
        assertNotNull(err)
        assertTrue(err.contains("androidProjectConfiguration"))
        assertTrue(err.contains("android = false"))
    }

    @Test
    fun `androidApplyErrorOrNull null when android on and Android settings present`() {
        val s = KmpProjectSettings()
        assertNull(
            KmpFeatureResolution.androidApplyErrorOrNull(
                settings = s,
                androidSettingsStored = true,
            )
        )
    }

    @Test
    fun `plugin ids match AGP 9_3 spike`() {
        assertEquals("org.jetbrains.kotlin.multiplatform", KmpPluginIds.KOTLIN_MULTIPLATFORM)
        assertEquals(
            "com.android.kotlin.multiplatform.library",
            KmpPluginIds.ANDROID_KMP_LIBRARY,
        )
    }
}
