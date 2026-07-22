package tools.forma.deps.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.api.JavaVersion
import tools.forma.config.AndroidProjectSettings
import tools.forma.config.FormaSettingsStore
import tools.forma.config.matchesDependencyValidationExclusion

/**
 * F-090: documents the [applyDependencies] `projectAction` gate — run suffix validation
 * only when the dependency project is **not** in the global exclusion set.
 *
 * Full Gradle Project wiring is heavy; this mirrors the production branch and proves
 * excluded deps skip validation while non-excluded illegal names still fail.
 */
class DependencyValidationExclusionApplyTest {

    private var validateInvocations = 0

    @AfterTest
    fun reset() {
        FormaSettingsStore.store(minimalSettings(emptySet()))
        validateInvocations = 0
    }

    /**
     * Same control flow as [applyDependencies] `projectAction`:
     * skip validate when excluded; always record the edge (add).
     */
    private fun projectActionGate(
        projectName: String,
        projectPath: String,
        validate: (String) -> Unit,
        added: MutableList<String>,
    ) {
        if (
            !FormaSettingsStore.isExcludedFromDependencyValidation(
                projectName = projectName,
                projectPath = projectPath,
            )
        ) {
            validateInvocations++
            validate(projectName)
        }
        added += projectPath
    }

    private val matrixValidate: (String) -> Unit = { name ->
        // Simulate matrix failure for non-Forma suffixes (ExoPlayer-class names).
        if (!name.endsWith("-api") && name != "api") {
            error("Illegal project dependency name '$name' (simulated matrix failure)")
        }
    }

    @Test
    fun `excluded dependency skips validator and still records edge`() {
        FormaSettingsStore.store(
            minimalSettings(setOf(":third-party:exoplayer:library-core"))
        )
        val added = mutableListOf<String>()

        projectActionGate(
            projectName = "library-core",
            projectPath = ":third-party:exoplayer:library-core",
            validate = matrixValidate,
            added = added,
        )

        assertEquals(0, validateInvocations, "validator must not run for exclusions")
        assertEquals(listOf(":third-party:exoplayer:library-core"), added)
    }

    @Test
    fun `non-excluded illegal suffix still fails validation`() {
        FormaSettingsStore.store(
            minimalSettings(setOf(":third-party:exoplayer:library-core"))
        )
        val added = mutableListOf<String>()

        val ex =
            assertFailsWith<IllegalStateException> {
                projectActionGate(
                    projectName = "feature-other-impl",
                    projectPath = ":feature:other:impl",
                    validate = matrixValidate,
                    added = added,
                )
            }
        assertTrue(ex.message.orEmpty().contains("feature-other-impl"))
        assertEquals(1, validateInvocations)
        assertTrue(added.isEmpty(), "failed validation should not add (gate throws before add)")
    }

    @Test
    fun `empty exclusions behave like strict matrix (no skip)`() {
        FormaSettingsStore.store(minimalSettings(emptySet()))
        assertFalse(
            matchesDependencyValidationExclusion(
                "library-core",
                ":third-party:exoplayer:library-core",
                emptySet(),
            )
        )
        val added = mutableListOf<String>()
        assertFailsWith<IllegalStateException> {
            projectActionGate(
                projectName = "library-core",
                projectPath = ":third-party:exoplayer:library-core",
                validate = matrixValidate,
                added = added,
            )
        }
        assertEquals(1, validateInvocations)
    }

    @Test
    fun `allowed forma suffix still validates when not excluded`() {
        FormaSettingsStore.store(minimalSettings(emptySet()))
        val added = mutableListOf<String>()
        projectActionGate(
            projectName = "feature-home-api",
            projectPath = ":feature:home:api",
            validate = matrixValidate,
            added = added,
        )
        assertEquals(1, validateInvocations)
        assertEquals(listOf(":feature:home:api"), added)
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
