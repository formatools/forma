package tools.forma.config

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.api.JavaVersion

/**
 * F-090 / GH #97: global dependency-validation exclusions matcher + store null-safety.
 */
class DependencyValidationExclusionsTest {

    @AfterTest
    fun resetStore() {
        // Re-store empty settings so later tests do not see leftovers.
        // FormaSettingsStore is a process-wide singleton.
        FormaSettingsStore.store(minimalSettings(emptySet()))
    }

    @Test
    fun `empty exclusions never match`() {
        assertFalse(
            matchesDependencyValidationExclusion(
                projectName = "library-core",
                projectPath = ":third-party:exoplayer:library-core",
                exclusions = emptySet(),
            )
        )
    }

    @Test
    fun `exact project path is excluded`() {
        val exclusions = setOf(":third-party:exoplayer:library-core")
        assertTrue(
            matchesDependencyValidationExclusion(
                projectName = "library-core",
                projectPath = ":third-party:exoplayer:library-core",
                exclusions = exclusions,
            )
        )
        assertFalse(
            matchesDependencyValidationExclusion(
                projectName = "library-core",
                projectPath = ":third-party:exoplayer:library-ui",
                exclusions = exclusions,
            )
        )
    }

    @Test
    fun `exact project name is excluded`() {
        val exclusions = setOf("exoplayer-library-core")
        assertTrue(
            matchesDependencyValidationExclusion(
                projectName = "exoplayer-library-core",
                projectPath = ":vendor:exoplayer-library-core",
                exclusions = exclusions,
            )
        )
        assertFalse(
            matchesDependencyValidationExclusion(
                projectName = "other-module",
                projectPath = ":vendor:other-module",
                exclusions = exclusions,
            )
        )
    }

    @Test
    fun `partial path or name is not excluded`() {
        val exclusions = setOf(":third-party:exoplayer")
        assertFalse(
            matchesDependencyValidationExclusion(
                projectName = "library-core",
                projectPath = ":third-party:exoplayer:library-core",
                exclusions = exclusions,
            )
        )
        assertFalse(
            matchesDependencyValidationExclusion(
                projectName = "exoplayer-library-core",
                projectPath = ":vendor:exoplayer-library-core",
                exclusions = setOf("exoplayer"),
            )
        )
    }

    @Test
    fun `non-excluded illegal suffix still not matched by unrelated list`() {
        // Simulates "feature-home-impl depending on another impl" — exclusion list does not
        // cover the dep, so matcher returns false and applyDependencies will still validate.
        assertFalse(
            matchesDependencyValidationExclusion(
                projectName = "feature-other-impl",
                projectPath = ":feature:other:impl",
                exclusions = setOf(":third-party:exoplayer:library-core"),
            )
        )
    }

    @Test
    fun `store with empty exclusions never excludes`() {
        FormaSettingsStore.store(minimalSettings(emptySet()))
        assertFalse(
            FormaSettingsStore.isExcludedFromDependencyValidation(
                "library-core",
                ":tp:library-core",
            )
        )
    }

    @Test
    fun `store reads exclusions after configuration-equivalent store`() {
        FormaSettingsStore.store(
            minimalSettings(
                setOf(
                    ":third-party:exoplayer:library-core",
                    "forked-media-engine",
                )
            )
        )
        assertTrue(
            FormaSettingsStore.isExcludedFromDependencyValidation(
                "library-core",
                ":third-party:exoplayer:library-core",
            )
        )
        assertTrue(
            FormaSettingsStore.isExcludedFromDependencyValidation(
                "forked-media-engine",
                ":vendor:forked-media-engine",
            )
        )
        assertFalse(
            FormaSettingsStore.isExcludedFromDependencyValidation(
                "feature-home-impl",
                ":feature:home:impl",
            )
        )
    }

    private fun minimalSettings(exclusions: Set<String>) =
        AndroidProjectSettings(
            minSdk = 23,
            targetSdk = 35,
            compileSdk = 35,
            kotlinVersion = "2.0.0",
            agpVersion = "8.0.0",
            repositories = {},
            compose = false,
            composeCompilerVersion = "2.0.0",
            vectorDrawablesUseSupportLibrary = false,
            javaVersionCompatibility = JavaVersion.VERSION_11,
            mandatoryOwners = false,
            dependencyValidationExclusions = exclusions,
        )
}
