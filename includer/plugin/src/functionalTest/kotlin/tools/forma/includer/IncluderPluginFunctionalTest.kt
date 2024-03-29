/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package tools.forma.includer

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.assertThrows
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

        // :feature1-api
        File(projectDir, "feature1/api").mkdirs()
        projectDir.resolve("feature1/api/api.gradle.kts").createNewFile()

        // :feature1-impl
        File(projectDir, "feature1/impl").mkdirs()
        projectDir.resolve("feature1/impl/impl.gradle.kts").createNewFile()

        // :util-android
        File(projectDir, "util/android").mkdirs()
        projectDir.resolve("util/android/build.gradle.kts").createNewFile()

        // composite build :build-logic
        File(projectDir, "build-logic").mkdir()
        projectDir.resolve("build-logic/settings.gradle.kts").createNewFile()

        // composite build :build-logic:conventions
        File(projectDir, "build-logic/conventions").mkdir()
        projectDir.resolve("build-logic/conventions/build.gradle.kts").createNewFile()
    }

    @Test
    fun `include projects with default options`() {
        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("projects")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue("Should include ':app' project") {
            result.output.contains("Project ':app'")
        }
        assertFalse(
            "Shouldn't include ':feature1-api' project " +
                    "because this project has a non-standard build file name"
        ) {
            result.output.contains("Project ':feature1-api'")
        }
        assertFalse(
            "Shouldn't include ':feature1-impl' project " +
                    "because this project has a non-standard build file name"
        ) {
            result.output.contains("Project ':feature1-impl'")
        }
        assertTrue("Should include ':util-android' project") {
            result.output.contains("Project ':util-android'")
        }
        assertFalse(
            "Shouldn't include ':build-logic' project " +
                    "because it's a nested project"
        ) {
            result.output.contains("Project ':build-logic'")
        }
        assertFalse(
            "Shouldn't include ':build-logic:conventions' project " +
                    "because it's a subproject of a nested project"
        ) {
            result.output.contains("Project ':build-logic-conventions'")
        }
    }

    @Test
    fun `include projects with 'arbitraryBuildScriptNames=true' option`() {
        // Enable option `arbitraryBuildScriptNames`
        projectDir.resolve("settings.gradle.kts").appendText("""
            includer {
                arbitraryBuildScriptNames = true
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("projects")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue("Should include ':app' project") {
            result.output.contains("Project ':app'")
        }
        assertTrue("Should include ':feature1-api' project " +
                "because 'arbitraryBuildScriptNames = true'") {
            result.output.contains("Project ':feature1-api'")
        }
        assertTrue("Should include ':feature1-impl' project " +
                "because 'arbitraryBuildScriptNames = true'") {
            result.output.contains("Project ':feature1-impl'")
        }
        assertTrue("Should include ':util-android' project") {
            result.output.contains("Project ':util-android'")
        }
        assertFalse(
            "Shouldn't include ':build-logic' project " +
                    "because it's a nested project"
        ) {
            result.output.contains("Project ':build-logic'")
        }
        assertFalse(
            "Shouldn't include ':build-logic:conventions' project " +
                    "because it's a subproject of a nested project"
        ) {
            result.output.contains("Project ':build-logic-conventions'")
        }
    }


    @Test
    fun `include projects with 'excludeFolders' option`() {
        // Exclude additional folders with `excludeFolders(...)`
        projectDir.resolve("settings.gradle.kts").appendText("""
            includer {
                excludeFolders("android", "impl")
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("projects")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue("Should include ':app' project") {
            result.output.contains("Project ':app'")
        }
        assertFalse(
            "Shouldn't include ':feature1-api' project " +
                    "because this project has a non-standard build file name"
        ) {
            result.output.contains("Project ':feature1-api'")
        }
        assertFalse(
            "Shouldn't include ':feature1-impl' project " +
                    "because this project has a non-standard build file name"
        ) {
            result.output.contains("Project ':feature1-impl'")
        }
        assertFalse(
            "Shouldn't include ':util-android' project " +
                    "because its folder name is in ignored folder names"
        ) {
            result.output.contains("Project ':util-android'")
        }
        assertFalse(
            "Shouldn't include ':build-logic' project " +
                    "because it's a nested project"
        ) {
            result.output.contains("Project ':build-logic'")
        }
        assertFalse(
            "Shouldn't include ':build-logic:conventions' project " +
                    "because it's a subproject of a nested project"
        ) {
            result.output.contains("Project ':build-logic-conventions'")
        }
    }

    @Test
    fun `include projects with multiple build files`() {
        // Enable option `arbitraryBuildScriptNames`
        projectDir.resolve("settings.gradle.kts").appendText("""
            includer {
                arbitraryBuildScriptNames = true
            }
        """.trimIndent())

        // Create a second build file in addition to the existing one
        projectDir.resolve("app/something.gradle").createNewFile()

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("projects")
        runner.withProjectDir(projectDir)

        // Verify the result
        val exception = assertThrows<Exception>("The build should fail") {
            runner.build()
        }
        assertTrue("Should detect more than one build file in :app module") {
            exception.message?.contains("app has more than one gradle build file") == true
        }
    }
}
