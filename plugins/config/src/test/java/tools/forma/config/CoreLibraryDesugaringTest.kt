package tools.forma.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.gradle.api.JavaVersion

/**
 * F-098 / GH #103: project-global core library desugaring settings + resolve helper.
 */
class CoreLibraryDesugaringTest {

    private fun settings(
        coreLibraryDesugaring: Boolean = false,
        coreLibraryDesugaringDependency: String = DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY,
    ) =
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
            coreLibraryDesugaring = coreLibraryDesugaring,
            coreLibraryDesugaringDependency = coreLibraryDesugaringDependency,
        )

    @Test
    fun `defaults are off with pinned desugar dependency`() {
        val s = settings()
        assertFalse(s.coreLibraryDesugaring)
        assertEquals(DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY, s.coreLibraryDesugaringDependency)
        assertTrue(s.coreLibraryDesugaringDependency.startsWith("com.android.tools:desugar_jdk_libs:"))
        assertNull(coreLibraryDesugaringDependencyOrNull(s))
    }

    @Test
    fun `enabled stores flag and returns default dependency coordinate`() {
        val s = settings(coreLibraryDesugaring = true)
        assertTrue(s.coreLibraryDesugaring)
        assertEquals(
            DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY,
            coreLibraryDesugaringDependencyOrNull(s),
        )
    }

    @Test
    fun `enabled with override returns custom coordinate`() {
        val custom = "com.android.tools:desugar_jdk_libs:2.0.4"
        val s =
            settings(
                coreLibraryDesugaring = true,
                coreLibraryDesugaringDependency = custom,
            )
        assertEquals(custom, s.coreLibraryDesugaringDependency)
        assertEquals(custom, coreLibraryDesugaringDependencyOrNull(s))
    }

    @Test
    fun `disabled does not apply even when custom dependency is stored`() {
        val custom = "com.android.tools:desugar_jdk_libs:2.0.4"
        val s =
            settings(
                coreLibraryDesugaring = false,
                coreLibraryDesugaringDependency = custom,
            )
        assertEquals(custom, s.coreLibraryDesugaringDependency)
        assertNull(coreLibraryDesugaringDependencyOrNull(s))
    }

    @Test
    fun `default constant pin is stable 2_x GAV`() {
        assertEquals(
            "com.android.tools:desugar_jdk_libs:2.1.5",
            DEFAULT_CORE_LIBRARY_DESUGARING_DEPENDENCY,
        )
    }
}
