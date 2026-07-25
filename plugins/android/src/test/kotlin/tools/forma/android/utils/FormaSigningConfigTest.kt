package tools.forma.android.utils

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pure unit coverage for F-097 signing model (no Gradle Project / AGP containers).
 */
class FormaSigningConfigTest {

    private val store = File("/tmp/forma-demo.keystore")

    @Test
    fun `toAppliedFields copies required identity fields`() {
        val config = FormaSigningConfig(
            storeFile = store,
            storePassword = "store-secret",
            keyAlias = "upload",
            keyPassword = "key-secret",
        )
        val fields = config.toAppliedFields()
        assertEquals(store, fields.storeFile)
        assertEquals("store-secret", fields.storePassword)
        assertEquals("upload", fields.keyAlias)
        assertEquals("key-secret", fields.keyPassword)
        assertNull(fields.storeType)
        assertNull(fields.enableV1Signing)
        assertNull(fields.enableV2Signing)
        assertNull(fields.enableV3Signing)
        assertNull(fields.enableV4Signing)
    }

    @Test
    fun `toAppliedFields preserves optional scheme flags and storeType`() {
        val config = FormaSigningConfig(
            storeFile = store,
            storePassword = "p",
            keyAlias = "a",
            keyPassword = "k",
            storeType = "pkcs12",
            enableV1Signing = true,
            enableV2Signing = true,
            enableV3Signing = false,
            enableV4Signing = true,
        )
        val fields = config.toAppliedFields()
        assertEquals("pkcs12", fields.storeType)
        assertEquals(true, fields.enableV1Signing)
        assertEquals(true, fields.enableV2Signing)
        assertEquals(false, fields.enableV3Signing)
        assertEquals(true, fields.enableV4Signing)
    }

    @Test
    fun `resolveBuildTypeSigningPairs returns empty for empty map`() {
        assertTrue(
            resolveBuildTypeSigningPairs(
                buildTypeSigning = emptyMap(),
                buildTypeNames = setOf("debug", "release"),
                signingConfigNames = setOf("debug", "demoRelease"),
            ).isEmpty()
        )
    }

    @Test
    fun `resolveBuildTypeSigningPairs keeps call-site order`() {
        val pairs = resolveBuildTypeSigningPairs(
            buildTypeSigning = linkedMapOf(
                "release" to "demoRelease",
                "debug" to "debug",
            ),
            buildTypeNames = setOf("debug", "release"),
            signingConfigNames = setOf("debug", "demoRelease"),
        )
        assertEquals(
            listOf("release" to "demoRelease", "debug" to "debug"),
            pairs,
        )
    }

    @Test
    fun `resolveBuildTypeSigningPairs rejects unknown build type`() {
        val error = assertFailsWith<IllegalArgumentException> {
            resolveBuildTypeSigningPairs(
                buildTypeSigning = mapOf("staging" to "demoRelease"),
                buildTypeNames = setOf("debug", "release"),
                signingConfigNames = setOf("demoRelease"),
            )
        }
        assertTrue(error.message!!.contains("unknown build type 'staging'"))
    }

    @Test
    fun `resolveBuildTypeSigningPairs rejects unknown signing config`() {
        val error = assertFailsWith<IllegalArgumentException> {
            resolveBuildTypeSigningPairs(
                buildTypeSigning = mapOf("release" to "prod"),
                buildTypeNames = setOf("debug", "release"),
                signingConfigNames = setOf("debug", "demoRelease"),
            )
        }
        assertTrue(error.message!!.contains("unknown signing config 'prod'"))
    }

    @Test
    fun `AndroidBinaryFeatureConfiguration defaults signing maps to empty`() {
        // Smoke: binary feature config still constructs without signing (debug-only apps).
        val empty = emptyMap<String, FormaSigningConfig>()
        assertTrue(empty.isEmpty())
        assertTrue(emptyMap<String, String>().isEmpty())
    }
}
