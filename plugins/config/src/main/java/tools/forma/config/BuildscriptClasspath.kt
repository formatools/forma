package tools.forma.config

import java.io.File
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

/**
 * Pure classifier/resolver for root **buildscript classpath** entries
 * (`androidProjectConfiguration(extraPlugins=…)` / `kmpProjectConfiguration(extraPlugins=…)`).
 *
 * **Classpath only** — never applies plugins. Type-owned apply stays in TARGET-PLUGINS.
 *
 * ## Supported shapes (F-100)
 *
 * | Entry | Becomes |
 * |-------|---------|
 * | `String` GAV (`"g:a:v"`) | classpath notation as-is |
 * | `Provider<PluginDependency>` (catalog `plugin(...)`) | `"pluginId:strictVersion"` |
 * | bare [PluginDependency] | same GAV-style string |
 * | `Provider<String>` | string GAV |
 * | [File], [FileCollection], `Provider` of either | files notation |
 * | Other [Dependency] that is **not** [ProjectDependency] | as-is |
 * | `Map` module notation | as-is |
 *
 * ## Not supported
 *
 * - [Project] / [ProjectDependency] — Gradle rejects project deps on the root
 *   `buildscript` classpath (`Project dependencies cannot be declared here` /
 *   class-loader ordering). Use **includeBuild** + catalog `plugin(GAV)` +
 *   composite substitution (sample `build-dependencies`, example `forma-defs`).
 *
 * Design note: `docs/BUILDSCRIPT-PROJECT-CLASSPATH.md`.
 */
object BuildscriptClasspath {

    /** In-repo design note (relative to repo root). */
    const val DOCS_PATH: String = "docs/BUILDSCRIPT-PROJECT-CLASSPATH.md"

    /**
     * Resolve one `extraPlugins` (or internal classpath) entry to a notation
     * suitable for `dependencies.classpath(...)`.
     *
     * @param entry raw list element from configuration
     * @param contextLabel API name for error messages (`extraPlugins`, …)
     * @throws IllegalArgumentException for unsupported types / null providers
     */
    @JvmStatic
    fun resolve(entry: Any, contextLabel: String = "extraPlugins"): Any =
        when (entry) {
            is Project -> throw projectRejected(projectLabel(entry), contextLabel)
            is ProjectDependency ->
                throw projectRejected(projectDependencyLabel(entry), contextLabel)
            is Provider<*> -> resolveProvider(entry, contextLabel)
            is PluginDependency -> pluginDependencyToNotation(entry)
            is String -> entry
            is File -> entry
            is FileCollection -> entry
            is Map<*, *> -> entry
            is Dependency -> entry
            else -> entry
        }

    /**
     * Convert a catalog/version-catalog [PluginDependency] into the GAV-style
     * string historically used by Forma buildscript wiring.
     *
     * Forma catalog `plugin("group:artifact", version)` stores the coordinate
     * pair in [PluginDependency.pluginId] (not only a dotted plugin id).
     */
    @JvmStatic
    fun pluginDependencyToNotation(plugin: PluginDependency): String {
        val version = plugin.version.strictVersion
        require(version.isNotBlank()) {
            "PluginDependency '${plugin.pluginId}' has no strict version; " +
                "buildscript classpath needs an explicit version (catalog plugin(id, version))"
        }
        return "${plugin.pluginId}:$version"
    }

    private fun resolveProvider(provider: Provider<*>, contextLabel: String): Any {
        val value =
            provider.orNull
                ?: throw IllegalArgumentException(
                    "$contextLabel: Provider yielded null. " +
                        "Use catalog plugin(...) providers, String GAV, or files — see $DOCS_PATH"
                )
        return when (value) {
            is PluginDependency -> pluginDependencyToNotation(value)
            is Project -> throw projectRejected(projectLabel(value), contextLabel)
            is ProjectDependency ->
                throw projectRejected(projectDependencyLabel(value), contextLabel)
            is String -> value
            is File -> value
            is FileCollection -> value
            is Map<*, *> -> value
            is Dependency -> value
            else ->
                throw IllegalArgumentException(
                    "$contextLabel: unsupported Provider value type " +
                        "${value::class.java.name}. " +
                        "Supported providers: PluginDependency (catalog plugin(...)), " +
                        "String GAV, File / FileCollection. " +
                        "Same-build project(...) is not supported on buildscript classpath — " +
                        "see $DOCS_PATH"
                )
        }
    }

    private fun projectLabel(project: Project): String =
        try {
            project.path
        } catch (_: Exception) {
            project.toString()
        }

    private fun projectDependencyLabel(dep: ProjectDependency): String =
        try {
            dep.path
        } catch (_: Exception) {
            try {
                dep.group + ":" + dep.name
            } catch (_: Exception) {
                dep.toString()
            }
        }

    /**
     * Actionable error when callers pass `project(":convention")` / a [Project]
     * into `extraPlugins`.
     */
    @JvmStatic
    fun projectRejectedMessage(projectLabel: String, contextLabel: String = "extraPlugins"): String =
        buildString {
            append(contextLabel)
            append(" does not accept Gradle Project / project(")
            append(projectLabel)
            append(") as a buildscript classpath dependency. ")
            append(
                "Root buildscript { } runs before the project graph can supply " +
                    "same-build project deps (Gradle: \"Project dependencies cannot be " +
                    "declared here\"). "
            )
            append("Supported local-plugin path: includeBuild(\"…\") + catalog ")
            append("plugin(\"group:artifact\", version) in extraPlugins ")
            append("(composite substitutes the GAV). ")
            append("See $DOCS_PATH")
        }

    private fun projectRejected(projectLabel: String, contextLabel: String): Nothing =
        throw IllegalArgumentException(projectRejectedMessage(projectLabel, contextLabel))
}
