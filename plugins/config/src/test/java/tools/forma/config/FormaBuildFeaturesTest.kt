package tools.forma.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * F-091 / GH #88: BuildFeatures defaults off + type/call-site override merge.
 */
class FormaBuildFeaturesTest {

    @Test
    fun `defaults are all false`() {
        val defaults = FormaBuildFeatures()
        assertFalse(defaults.aidl)
        assertFalse(defaults.buildConfig)
        assertFalse(defaults.dataBinding)
        assertFalse(defaults.prefab)
        assertFalse(defaults.resValues)
        assertFalse(defaults.shaders)
        assertFalse(defaults.viewBinding)
    }

    @Test
    fun `resolve keeps project defaults for fleet flags`() {
        val defaults =
            FormaBuildFeatures(
                aidl = true,
                buildConfig = true,
                dataBinding = false,
                prefab = true,
                resValues = true,
                shaders = false,
                viewBinding = false,
            )
        val resolved = defaults.resolveWith(viewBinding = false, compose = false)
        assertTrue(resolved.aidl)
        assertTrue(resolved.buildConfig)
        assertFalse(resolved.dataBinding)
        assertTrue(resolved.prefab)
        assertTrue(resolved.resValues)
        assertFalse(resolved.shaders)
        assertFalse(resolved.viewBinding)
        assertFalse(resolved.compose)
    }

    @Test
    fun `type owned viewBinding override wins over project default false`() {
        val defaults = FormaBuildFeatures(viewBinding = false)
        val resolved = defaults.resolveWith(viewBinding = true, compose = false)
        assertTrue(resolved.viewBinding)
        assertFalse(resolved.compose)
    }

    @Test
    fun `call site compose override wins independently of other flags`() {
        val defaults = FormaBuildFeatures(buildConfig = true)
        val resolved = defaults.resolveWith(viewBinding = false, compose = true)
        assertTrue(resolved.compose)
        assertTrue(resolved.buildConfig)
        assertFalse(resolved.viewBinding)
    }

    @Test
    fun `project viewBinding default is not auto applied without override arg`() {
        // resolveWith takes the *final* viewBinding value from the DSL layer.
        // Project default is applied at the impl() call-site default parameter, not here.
        val defaults = FormaBuildFeatures(viewBinding = true)
        val withoutOptIn = defaults.resolveWith(viewBinding = false, compose = false)
        assertFalse(withoutOptIn.viewBinding)
        val withOptIn = defaults.resolveWith(viewBinding = true, compose = false)
        assertTrue(withOptIn.viewBinding)
    }

    @Test
    fun `AndroidProjectSettings stores buildFeatures`() {
        val features = FormaBuildFeatures(buildConfig = true, shaders = true)
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
                javaVersionCompatibility = org.gradle.api.JavaVersion.VERSION_11,
                mandatoryOwners = false,
                buildFeatures = features,
            )
        assertEquals(features, settings.buildFeatures)
        assertTrue(settings.buildFeatures.buildConfig)
        assertTrue(settings.buildFeatures.shaders)
        assertFalse(settings.buildFeatures.aidl)
    }
}
