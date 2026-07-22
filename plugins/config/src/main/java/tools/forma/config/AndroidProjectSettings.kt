package tools.forma.config

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

// TODO use jacoco by default
// TODO publishing
// TODO owners
// TODO modules dependency restrictions
// TODO api for module dependencies
// TODO should we mark dependencies?
// TODO how we make sure dependency sources is correct?
// TODO Should we use extension class as the last arg for complex config?
// TODO Should we restrict build flavor per module?
// TODO Configuration override
// TODO 3rd party plugins
// TODO generalize and split into code configuration and android specific configuration
data class AndroidProjectSettings(
    val minSdk: Int,
    val targetSdk: Int,
    val compileSdk: Int,
    val kotlinVersion: String,
    val agpVersion: String,
    val repositories: RepositoryHandler.() -> Unit,
    /**
     * Project-wide default for per-target `compose` flags
     * (`impl`, `androidUtil`, `androidApp`, `uiLibrary`, `androidBinary`).
     * Individual targets may still pass `compose = true/false`
     * to override. Does **not** auto-enable `composeWidget` modules (those always
     * enable Compose themselves).
     *
     * Kept top-level (not nested under [buildFeatures]) so there is one documented
     * happy path for the Compose project default — see F-091 / [FormaBuildFeatures].
     */
    val compose: Boolean,
    /**
     * Jetpack Compose compiler extension version applied when a target enables
     * Compose (`composeOptions.kotlinCompilerExtensionVersion`). Must match the
     * Kotlin version used by the project (see Compose Compiler compatibility map).
     * Default `2.3.21` pairs with Kotlin **2.3.21** (Gradle 9.6.1 embedded Kotlin used
     * by the sample application). Override when bumping Kotlin.
     */
    val composeCompilerVersion: String,
    val vectorDrawablesUseSupportLibrary: Boolean,
    val javaVersionCompatibility: JavaVersion, // Java/Kotlin configuration
    val mandatoryOwners: Boolean,
    /**
     * When true, each target that registers `packageName` layout metadata fails at
     * **configuration time** if `src/main/{kotlin|java}/<package>` is missing under the
     * module (F-088). Default **false** so greenfield / sample trees are not forced to
     * scaffold dirs before first generate. Prefer explicit tasks `formaLayoutCheck` /
     * `formaLayoutGenerate` (and root `*All` variants) for the happy path; turn this on
     * in CI or local strict mode only.
     */
    val checkPackageLayoutAtConfiguration: Boolean = false,
    /**
     * Project paths or names that skip **project-dependency type/suffix validation**
     * when they appear as a dependency of a Forma target (F-090 / GH #97).
     *
     * Use for forked-in third-party trees (e.g. ExoPlayer) that live in the monorepo
     * but do not follow Forma target suffixes. Does **not** disable self-type validation
     * on Forma DSL modules and does **not** change the default dependency matrix.
     *
     * Match by Gradle project path (e.g. `:third-party:exoplayer:library-core`) and/or
     * project name. Exact match only (see [matchesDependencyValidationExclusion]).
     * Default empty — strict matrix unchanged.
     */
    val dependencyValidationExclusions: Set<String> = emptySet(),
    /**
     * Project-global AGP [FormaBuildFeatures] defaults (F-091 / GH #88).
     * All flags default **false**. Applied explicitly on every Android library/app
     * target so AGP cannot silently enable unwanted features. Compose project default
     * stays on [compose] (not nested here).
     */
    val buildFeatures: FormaBuildFeatures = FormaBuildFeatures(),
)

/**
 * Exact-match check for [AndroidProjectSettings.dependencyValidationExclusions].
 *
 * @param projectName Gradle [org.gradle.api.Project.getName]
 * @param projectPath Gradle [org.gradle.api.Project.getPath] (e.g. `:foo:bar`)
 * @param exclusions allow-list from project settings; empty ⇒ never excluded
 */
fun matchesDependencyValidationExclusion(
    projectName: String,
    projectPath: String,
    exclusions: Set<String>,
): Boolean {
    if (exclusions.isEmpty()) return false
    return projectName in exclusions || projectPath in exclusions
}

/**
 * Singleton project configuration store, used by internal plugins
 */
object FormaSettingsStore : SettingsStore<AndroidProjectSettings>, PluginInfoStore {
    private lateinit var _settings: AndroidProjectSettings
    override val settings: AndroidProjectSettings get() = _settings

    override fun store(configuration: AndroidProjectSettings) {
        _settings = configuration
    }

    /**
     * Whether settings have been written by [store] (false on JVM-only paths that never
     * call `androidProjectConfiguration`).
     */
    val isSettingsStored: Boolean
        get() = this::_settings.isInitialized

    /**
     * [AndroidProjectSettings.dependencyValidationExclusions] when settings exist;
     * empty set otherwise (null-safe — does not throw).
     */
    fun dependencyValidationExclusionsOrEmpty(): Set<String> =
        if (isSettingsStored) _settings.dependencyValidationExclusions else emptySet()

    /**
     * True when [projectName] or [projectPath] is listed in
     * [AndroidProjectSettings.dependencyValidationExclusions].
     * Safe when settings were never stored (treats as no exclusions).
     */
    fun isExcludedFromDependencyValidation(projectName: String, projectPath: String): Boolean =
        matchesDependencyValidationExclusion(
            projectName = projectName,
            projectPath = projectPath,
            exclusions = dependencyValidationExclusionsOrEmpty(),
        )

    override val plugins: MutableMap<Provider<PluginDependency>, PluginConfiguration> = mutableMapOf()
    override val dependencyPlugins: MutableMap<String, PluginConfiguration> = mutableMapOf()
    override val dependencies: MutableMap<String, DependencyConfiguration> = mutableMapOf()
    override val configurations: MutableMap<String, Provider<PluginDependency>> = mutableMapOf()
}

interface SettingsStore<T : Any> {
    val settings: T
    fun store(configuration: T)
}

data class PluginConfiguration(val plugin: Provider<PluginDependency>, val configuration: String)
data class DependencyConfiguration(val dependency: String, val configuration: String)

interface PluginInfoStore {
    val plugins: MutableMap<Provider<PluginDependency>, PluginConfiguration>
    val dependencyPlugins: MutableMap<String, PluginConfiguration>
    val dependencies: MutableMap<String, DependencyConfiguration>
    val configurations: MutableMap<String, Provider<PluginDependency>>

    fun registerConfiguration(configuration: String, plugin: Provider<PluginDependency>, vararg dependencies: String) {
        registerPlugin(plugin, configuration)

        dependencies.forEach { dependency ->
            registerDependency(dependency, configuration)
        }
    }
    fun registerPlugin(plugin: Provider<PluginDependency>, configuration: String){
        configurations[configuration] = plugin
        plugins[plugin] = PluginConfiguration(plugin, configuration)
    }

    fun registerDependency(dependency: String, configuration: String){
        if (configurations.containsKey(configuration)) {
            dependencyPlugins[dependency] = plugins[configurations[configuration]!!]!!
            dependencies[dependency] = DependencyConfiguration(dependency, configuration)
        } else {
            throw Exception("Configuration $configuration is not registered using registerPlugin")
        }
    }

    fun pluginFor(dependencyName: String): PluginConfiguration? {
        // println("DEP: $dependencyName")
        // println(dependencyPlugins)
        return dependencyPlugins[dependencyName]
    }
}
