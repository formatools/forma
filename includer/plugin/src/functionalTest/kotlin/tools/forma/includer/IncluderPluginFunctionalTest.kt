/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package tools.forma.includer

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * A simple functional test includer plugin.
 */
class IncluderPluginFunctionalTest {

    @field:TempDir
    lateinit var projectDir: File

    @BeforeTest
    fun `prepare filesystem`() {
        projectDir.resolve("build.gradle.kts").createNewFile()
        projectDir.resolve("settings.gradle.kts").writeText(
            """
            plugins {
                id("tools.forma.includer")
            }
        """.trimIndent()
        )
        // :app
        File(projectDir, "app").mkdir()
        projectDir.resolve("app/build.gradle.kts").createNewFile()

        // :feature-dashboard-api
        File(projectDir, "feature/dashboard/api").mkdirs()
        projectDir.resolve("feature/dashboard/api/build.gradle.kts").createNewFile()

        // composite build :build-logic
        File(projectDir, "build-logic").mkdir()
        projectDir.resolve("build-logic/settings.gradle.kts").createNewFile()

        // composite build :build-logic:conventions
        File(projectDir, "build-logic/conventions").mkdir()
        projectDir.resolve("build-logic/conventions/build.gradle.kts").createNewFile()
    }

    @Test
    fun `include extra projects`() {
        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("projects")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue("Root project should include ':app' project") {
            result.output.contains("Project ':app'")
        }
        assertTrue("Root project should include ':feature-dashboard-api' project") {
            result.output.contains("Project ':feature-dashboard-api'")
        }
        assertFalse("Root project shouldn't include ':build-logic' project") {
            result.output.contains("Project ':build-logic'")
        }
        assertFalse("Root project shouldn't include ':build-logic:conventions' project") {
            result.output.contains("Project ':build-logic-conventions'")
        }
    }
}
