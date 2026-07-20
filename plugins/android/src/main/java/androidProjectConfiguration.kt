import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.embeddedKotlinVersion
import org.gradle.kotlin.dsl.repositories
import org.gradle.plugin.use.PluginDependency
import tools.forma.android.utils.register
import tools.forma.android.target.registerAndroidDefaults
import tools.forma.config.AndroidProjectSettings
import tools.forma.config.FormaSettingsStore
import tools.forma.config.PluginInfoStore
import tools.forma.config.SettingsStore

/**
 * The **single supported entry point** for Android project-wide configuration in Forma.
 *
 * Call this exactly once from the **root** `build.gradle.kts` inside a `buildscript { }` block:
 *
 * ```kotlin
 * buildscript {
 *     androidProjectConfiguration(
 *         project = rootProject,
 *         minSdk = 23,
 *         targetSdk = 35,
 *         compileSdk = 35,
 *         agpVersion = "9.3.0",
 *         // ...
 *     )
 * }
 * ```
 *
 * What it does:
 * - Puts AGP + (when relevant) the Kotlin Compose compiler Gradle plugin on the **buildscript classpath**.
 * - Adds any `extraPlugins` **jars / providers to the buildscript classpath only**.
 * - Stores [AndroidProjectSettings] via [Forma.store] (delegates to [FormaSettingsStore]).
 *   Downstream targets read global values from `Forma.settings`.
 * - Registers default Android target types and restriction matrix via [registerAndroidDefaults].
 * - Registers a conventional root `clean` task.
 *
 * **extraPlugins**:
 * Jars (or `Provider<PluginDependency>`) listed here are added to the root **buildscript classpath**.
 * They do **NOT** cause any plugin to be applied to modules.
 * To actually apply an external Gradle plugin to targets, register it against a target type
 * using the type-owned plugin APIs and let the registry auto-apply it:
 * see [docs/TARGET-PLUGINS.md](TARGET-PLUGINS.md) (`targetPlugin`, `deriveTargetType`,
 * `registerTargetPlugin`, Path A vs Path B).
 *
 * This is the **only** supported project configuration path. The previous `Project` receiver
 * overload has been removed (F-082). Do not call configuration from arbitrary `Project` scopes.
 *
 * @param project the root project (typically `rootProject`); used to register the clean task.
 * @param minSdk minimum Android SDK
 * @param targetSdk target Android SDK
 * @param compileSdk SDK version used to compile Android targets
 * @param kotlinVersion Kotlin version (defaults to Gradle's embeddedKotlinVersion)
 * @param agpVersion Android Gradle Plugin version to place on the buildscript classpath
 * @param repositories optional block to configure repositories (applied in buildscript context)
 * @param compose project-wide default for per-target Compose flags (see [AndroidProjectSettings.compose])
 * @param composeCompilerVersion Compose compiler extension version for AGP `composeOptions`
 *   (must match the Kotlin version used)
 * @param javaVersionCompatibility Java language level for source/target compatibility
 * @param mandatoryOwners when true, all targets require an owner declaration
 * @param vectorDrawablesUseSupportLibrary passed through to Android vector drawable config
 * @param extraPlugins list of extra artifacts / plugin providers to add to the **buildscript classpath only**.
 *   See "Classpath vs apply" in TARGET-PLUGINS.md.
 */
fun ScriptHandlerScope.androidProjectConfiguration(
    project: Project,
    minSdk: Int,
    targetSdk: Int,
    compileSdk: Int,
    kotlinVersion: String = embeddedKotlinVersion,
    agpVersion: String,
    repositories: RepositoryHandler.() -> Unit = {},
    compose: Boolean = false,
    composeCompilerVersion: String = DEFAULT_COMPOSE_COMPILER_VERSION,
    javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8, // Java/Kotlin configuration
    mandatoryOwners: Boolean = false,
    vectorDrawablesUseSupportLibrary: Boolean = false,
    extraPlugins: List<Any> = emptyList()
) {
    buildScriptConfiguration(
        this, extraPlugins
                // Add Correct AGP version to build classpath
                + "com.android.tools.build:gradle:$agpVersion"
                // Kotlin 2.0+ Compose Compiler Gradle plugin (required when compose is enabled)
                + "org.jetbrains.kotlin:compose-compiler-gradle-plugin:$kotlinVersion"
    )

    /** Default Android project clean task implementation */
    with(project) {
        tasks.register(
            "clean",
            Delete::class
        ) { delete(layout.buildDirectory) }
    }

    val configuration =
        AndroidProjectSettings(
            minSdk = minSdk,
            targetSdk = targetSdk,
            compileSdk = compileSdk,
            // we don't need check properties for exist, we read it successfully in forma
            // configuration
            kotlinVersion = kotlinVersion,
            agpVersion = agpVersion,
            repositories = repositories,
            javaVersionCompatibility = javaVersionCompatibility,
            mandatoryOwners = mandatoryOwners,
            compose = compose,
            composeCompilerVersion = composeCompilerVersion,
            vectorDrawablesUseSupportLibrary = vectorDrawablesUseSupportLibrary
        )

    Forma.store(configuration)
    registerAndroidDefaults()
}

/** Compose Compiler matching Kotlin 2.3.21 (Gradle 9.6.1 embedded Kotlin). */
const val DEFAULT_COMPOSE_COMPILER_VERSION = "2.3.21"

val buildScriptConfiguration: ScriptHandlerScope.(List<Any>) -> Unit = { classpathDeps ->
    // TODO pass repositories configuration
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpathDeps.forEach {
            when (it) {
                is Provider<*> ->
                    (it.get() as? PluginDependency)?.let { plugin ->
                        classpath("${plugin.pluginId}:${plugin.version.strictVersion}")
                    }
                        ?: throw IllegalArgumentException("Only plugin providers are supported")
                else -> classpath(it)
            }
        }
    }
}

/**
 * Singleton accessor for stored project configuration.
 *
 * Delegates to [FormaSettingsStore] (which holds the single [AndroidProjectSettings] instance
 * written by [androidProjectConfiguration]).
 *
 * Downstream code and targets obtain values via `Forma.settings` (or the delegated
 * `SettingsStore` / `PluginInfoStore` surfaces). There is exactly one global configuration
 * story: root `buildscript { androidProjectConfiguration(...) }` → store → readers.
 */
object Forma :
    SettingsStore<AndroidProjectSettings> by FormaSettingsStore,
    PluginInfoStore by FormaSettingsStore
