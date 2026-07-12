/**
 * Shared metadata for publishing Forma Gradle plugins to the Plugin Portal.
 *
 * Configured once on the plugins root via [formaPluginConfiguration], then
 * consumed by each subproject through [formaPublishedPlugin].
 *
 * See GH #132 / F-016 and docs/PLUGIN-PUBLISH.md.
 */
open class FormaPluginPublishExtension {
    /** Maven group / plugin id prefix (e.g. `tools.forma`). */
    var group: String = "tools.forma"

    /** Shared artifact version for all Forma plugins in this build. */
    var version: String = "0.0.0"

    var website: String = "https://forma.tools/"
    var vcsUrl: String = "https://github.com/formatools/forma.git"

    /** Default Portal display name (subprojects may override). */
    var displayName: String =
        "Forma - Meta Build System with Gradle and Android support"

    /** Default Portal description (subprojects may override). */
    var description: String = "Best way to structure your Gradle Project"

    /** Default Portal tags; subprojects may replace or append via extraTags. */
    var tags: List<String> =
        listOf("kotlin", "android", "structure", "target", "rules", "project")
}
