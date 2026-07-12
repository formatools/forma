package tools.forma.deps.catalog

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GeneratorsTest {

    @Test
    fun `defaultNameGenerator drops common tokens and version`() {
        assertEquals(
            "jakewhartonTimber",
            defaultNameGenerator("com.jakewharton.timber:timber:5.0.1")
        )
        assertEquals("coilKt", defaultNameGenerator("io.coil-kt:coil:2.1.0"))
        assertEquals("coilKtBase", defaultNameGenerator("io.coil-kt:coil-base:2.1.0"))
        assertEquals("roomRuntime", defaultNameGenerator("androidx.room:room-runtime:2.5.1"))
        assertEquals("sqlite", defaultNameGenerator("androidx.sqlite:sqlite:2.2.0"))
    }

    @Test
    fun `pluginNameGenerator drops gradle plugin noise`() {
        assertEquals(
            "navigationSafeArgs",
            pluginNameGenerator("androidx.navigation:navigation-safe-args-gradle-plugin")
        )
        assertEquals(
            "firebaseCrashlytics",
            pluginNameGenerator("com.google.firebase:firebase-crashlytics-gradle")
        )
        assertEquals(
            "devtoolsKspSymbolProcessing",
            pluginNameGenerator("com.google.devtools.ksp:symbol-processing-gradle-plugin")
        )
        assertEquals(
            "toolsFormaDemoDependencies",
            pluginNameGenerator("tools.forma.demo:dependencies")
        )
    }

    @Test
    fun `parseGroupArtifactVersion requires three segments`() {
        assertEquals(
            Triple("g", "a", "1.0"),
            parseGroupArtifactVersion("g:a:1.0")
        )
        assertFailsWith<IllegalArgumentException> { parseGroupArtifactVersion("g:a") }
        assertFailsWith<IllegalArgumentException> { parseGroupArtifactVersion("g:a:1:extra") }
        assertFailsWith<IllegalArgumentException> { parseGroupArtifactVersion("g::1.0") }
    }

    @Test
    fun `name generators fail clearly when everything is filtered`() {
        assertFailsWith<IllegalArgumentException> {
            // group/artifact tokens are all in filteredTokens
            defaultNameGenerator("com.android:android:1.0")
        }
    }
}
