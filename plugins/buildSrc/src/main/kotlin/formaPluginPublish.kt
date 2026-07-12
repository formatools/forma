import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

private const val EXT_NAME = "formaPluginConfiguration"

/**
 * Root-level publish metadata for the Forma plugins multi-project.
 *
 * Example (`plugins/build.gradle.kts`):
 * ```
 * formaPluginConfiguration {
 *     group = "tools.forma"
 *     version = "0.1.3"
 *     website = "https://forma.tools/"
 *     vcsUrl = "https://github.com/formatools/forma.git"
 *     displayName = "Forma - Meta Build System with Gradle and Android support"
 *     description = "Best way to structure your Gradle Project"
 *     tags = listOf("kotlin", "android", "structure", "target", "rules", "project")
 * }
 * ```
 */
fun Project.formaPluginConfiguration(configure: FormaPluginPublishExtension.() -> Unit) {
    require(this == rootProject) {
        "formaPluginConfiguration must be called on the plugins root project"
    }
    val extension =
        extensions.findByType(FormaPluginPublishExtension::class.java)
            ?: extensions.create(EXT_NAME, FormaPluginPublishExtension::class.java)
    extension.configure()
    group = extension.group
    version = extension.version
    // Keep legacy ext keys for any residual consumers / debugging.
    extra["group"] = extension.group
    extra["version"] = extension.version
    extra["website"] = extension.website
    extra["vcsUrl"] = extension.vcsUrl
    extra["displayName"] = extension.displayName
    extra["description"] = extension.description
    extra["tags"] = extension.tags
}

/**
 * Registers this project as a published Gradle plugin using root
 * [formaPluginConfiguration] defaults.
 *
 * Replaces the copy-pasted `gradlePlugin { … rootProject.ext[…] }` blocks
 * (GH #132). Defaults match historical Forma Portal metadata:
 * - plugin id: `{group}.{name}` (e.g. `tools.forma.android`)
 * - implementation class: `{id}.plugin.FormaPlugin`
 *
 * Example (`plugins/android/build.gradle.kts`):
 * ```
 * formaPublishedPlugin(
 *     name = "android",
 *     // optional overrides:
 *     // description = "Android targets for Forma",
 *     // extraTags = listOf("agp"),
 * )
 * ```
 *
 * @param name short plugin name segment (usually the Gradle project name)
 * @param id full plugin id; default `{group}.{name}`
 * @param displayName Portal display name; default from root config
 * @param description Portal description; default from root config
 * @param implementationClass FQCN of the plugin implementation class
 * @param tags full tag list replacement; default from root config
 * @param extraTags tags appended after [tags] / root defaults
 */
fun Project.formaPublishedPlugin(
    name: String = this.name,
    id: String? = null,
    displayName: String? = null,
    description: String? = null,
    implementationClass: String? = null,
    tags: List<String>? = null,
    extraTags: List<String> = emptyList(),
) {
    val rootConfig = rootProject.extensions.getByType<FormaPluginPublishExtension>()
    group = rootConfig.group
    version = rootConfig.version

    val pluginId = id ?: "${rootConfig.group}.$name"
    val pluginDisplayName = displayName ?: rootConfig.displayName
    val pluginDescription = description ?: rootConfig.description
    val pluginImpl = implementationClass ?: "$pluginId.plugin.FormaPlugin"
    val pluginTags = (tags ?: rootConfig.tags) + extraTags

    extensions.configure(GradlePluginDevelopmentExtension::class.java) {
        website.set(rootConfig.website)
        vcsUrl.set(rootConfig.vcsUrl)
        plugins {
            create(name) {
                this.id = pluginId
                this.displayName = pluginDisplayName
                this.description = pluginDescription
                this.implementationClass = pluginImpl
                this.tags.set(pluginTags)
            }
        }
    }
}
