package tools.forma.config

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.gradle.api.internal.provider.Providers
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.plugin.use.PluginDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.gradle.api.artifacts.VersionConstraint

/**
 * Pure unit coverage for F-100 buildscript classpath classifier
 * ([BuildscriptClasspath.resolve]).
 *
 * No Project / TestKit — Project/ProjectDependency rejection is covered via
 * message helpers + type branches exercised with non-Project supported types.
 */
class BuildscriptClasspathTest {

    private class TestPluginDependency(
        private val id: String,
        private val strict: String,
    ) : PluginDependency {
        override fun getPluginId(): String = id

        override fun getVersion(): VersionConstraint =
            DefaultMutableVersionConstraint(strict).apply { strictly(strict) }
    }

    @Test
    fun `string GAV passes through`() {
        val gav = "com.example:plugin:1.2.3"
        assertEquals(gav, BuildscriptClasspath.resolve(gav))
    }

    @Test
    fun `plugin dependency becomes id colon strict version`() {
        val plugin = TestPluginDependency("tools.forma.demo:dependencies", "0.0.1")
        assertEquals(
            "tools.forma.demo:dependencies:0.0.1",
            BuildscriptClasspath.pluginDependencyToNotation(plugin),
        )
        assertEquals(
            "tools.forma.demo:dependencies:0.0.1",
            BuildscriptClasspath.resolve(plugin),
        )
    }

    @Test
    fun `provider of plugin dependency resolves like catalog plugin accessor`() {
        val plugin = TestPluginDependency("androidx.navigation:navigation-safe-args-gradle-plugin", "2.9.8")
        val provider = Providers.of(plugin)
        assertEquals(
            "androidx.navigation:navigation-safe-args-gradle-plugin:2.9.8",
            BuildscriptClasspath.resolve(provider),
        )
    }

    @Test
    fun `provider of string GAV resolves`() {
        assertEquals(
            "g:a:1",
            BuildscriptClasspath.resolve(Providers.of("g:a:1")),
        )
    }

    @Test
    fun `file and provider of file pass through`() {
        val file = File("/tmp/convention-plugin.jar")
        assertSame(file, BuildscriptClasspath.resolve(file))
        assertSame(file, BuildscriptClasspath.resolve(Providers.of(file)))
    }

    @Test
    fun `map module notation passes through`() {
        val map = mapOf("group" to "g", "name" to "a", "version" to "1")
        assertSame(map, BuildscriptClasspath.resolve(map))
    }

    @Test
    fun `external module dependency passes through`() {
        val dep = DefaultExternalModuleDependency("g", "a", "1")
        assertSame(dep, BuildscriptClasspath.resolve(dep))
    }

    @Test
    fun `null provider fails with actionable message`() {
        val ex =
            assertFailsWith<IllegalArgumentException> {
                BuildscriptClasspath.resolve(Providers.notDefined<Any>())
            }
        assertTrue(ex.message!!.contains("extraPlugins"))
        assertTrue(ex.message!!.contains(BuildscriptClasspath.DOCS_PATH))
    }

    @Test
    fun `provider of unsupported type fails clearly`() {
        val ex =
            assertFailsWith<IllegalArgumentException> {
                BuildscriptClasspath.resolve(Providers.of(42))
            }
        assertTrue(ex.message!!.contains("unsupported Provider value type"))
        assertTrue(ex.message!!.contains(BuildscriptClasspath.DOCS_PATH))
        assertTrue(ex.message!!.contains("PluginDependency"))
    }

    @Test
    fun `blank plugin version rejected`() {
        val plugin = TestPluginDependency("com.example.plugin", "")
        val ex =
            assertFailsWith<IllegalArgumentException> {
                BuildscriptClasspath.pluginDependencyToNotation(plugin)
            }
        assertTrue(ex.message!!.contains("no strict version"))
    }

    @Test
    fun `project rejected message points at includeBuild recipe`() {
        val msg = BuildscriptClasspath.projectRejectedMessage(":my-convention", "extraPlugins")
        assertTrue(msg.contains("extraPlugins"))
        assertTrue(msg.contains(":my-convention"))
        assertTrue(msg.contains("includeBuild"))
        assertTrue(msg.contains("plugin("))
        assertTrue(msg.contains(BuildscriptClasspath.DOCS_PATH))
        assertTrue(msg.contains("Project dependencies cannot be declared here"))
    }

    @Test
    fun `context label flows into project rejected message`() {
        val msg = BuildscriptClasspath.projectRejectedMessage(":x", "kmpProjectConfiguration extraPlugins")
        assertTrue(msg.startsWith("kmpProjectConfiguration extraPlugins"))
    }
}
