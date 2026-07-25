package tools.forma.android.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.gradle.api.JavaVersion
import tools.forma.config.AndroidProjectSettings
import tools.forma.config.DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY
import tools.forma.config.coreLibraryDesugaringDependencyOrNull

/**
 * Pure unit coverage for F-098 desugar resolve path used by [applyCoreLibraryDesugaring].
 * (No Gradle Project / AGP containers — mirrors [FormaSigningConfigTest] style.)
 */
class CoreLibraryDesugaringApplyTest {

    private fun settings(
        enabled: Boolean,
        dependency: String = DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY,
    ) =
        AndroidProjectSettings(
            minSdk = 21,
            targetSdk = 35,
            compileSdk = 35,
            kotlinVersion = "2.3.21",
            agpVersion = "9.3.0",
            repositories = {},
            compose = false,
            composeCompilerVersion = "2.3.21",
            vectorDrawablesUseSupportLibrary = false,
            javaVersionCompatibility = JavaVersion.VERSION_11,
            mandatoryOwners = false,
            coreLibraryDesugaring = enabled,
            coreLibraryDesugaringDependency = dependency,
        )

    @Test
    fun `resolve null when desugaring disabled — apply path must no-op`() {
        assertNull(coreLibraryDesugaringDependencyOrNull(settings(enabled = false)))
    }

    @Test
    fun `resolve default GAV when desugaring enabled`() {
        assertEquals(
            DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY,
            coreLibraryDesugaringDependencyOrNull(settings(enabled = true)),
        )
    }

    @Test
    fun `settings flag mirrors compileOptions isCoreLibraryDesugaringEnabled source`() {
        // CompileOptions.applyFrom sets isCoreLibraryDesugaringEnabled from this field.
        assertFalse(settings(enabled = false).coreLibraryDesugaring)
        assertTrue(settings(enabled = true).coreLibraryDesugaring)
    }

    @Test
    fun `custom dependency flows through resolve when enabled`() {
        val gav = "com.android.tools:desugar_jdk_libs:2.1.4"
        assertEquals(gav, coreLibraryDesugaringDependencyOrNull(settings(true, gav)))
        assertNull(coreLibraryDesugaringDependencyOrNull(settings(false, gav)))
    }
}
