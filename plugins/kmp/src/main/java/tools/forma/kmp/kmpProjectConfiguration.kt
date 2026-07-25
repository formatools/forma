package tools.forma.kmp

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.embeddedKotlinVersion
import org.gradle.kotlin.dsl.repositories
import org.gradle.plugin.use.PluginDependency
import tools.forma.config.FormaSettingsStore
import tools.forma.kmp.settings.DEFAULT_KMP_JVM_TARGET
import tools.forma.kmp.settings.KmpPlatforms
import tools.forma.kmp.settings.KmpProjectSettings
import tools.forma.kmp.settings.KmpSettingsStore
import tools.forma.kmp.target.registerKmpDefaults

/**
 * Single project-global entry for Kotlin Multiplatform configuration (F-107 / F-082 spirit).
 *
 * Call once from the **root** `build.gradle.kts` inside `buildscript { }`:
 *
 * ```kotlin
 * buildscript {
 *     // When android platform is on (default), prefer configuring Android first so AGP
 *     // version aligns automatically:
 *     // androidProjectConfiguration(project = rootProject, agpVersion = "9.3.0", ...)
 *     kmpProjectConfiguration(project = rootProject)
 * }
 * ```
 *
 * What it does:
 * - Registers [registerKmpDefaults] (KMP restriction matrix)
 * - Stores [KmpProjectSettings] (platforms jvm+android default **true**, jvmTarget `"11"`)
 * - Puts Kotlin Multiplatform Gradle plugin on the **buildscript classpath only**
 * - When [KmpPlatforms.android] is true: puts AGP (`com.android.tools.build:gradle`) on the
 *   classpath so `com.android.kotlin.multiplatform.library` can be applied by the feature
 *   applicator — **not** applied here
 *
 * **Classpath only — no module apply.** Modules receive plugins via KMP target DSL
 * (`kmpLibrary`, …), never via free-form plugin id lists at call sites.
 *
 * Pure KMP+JVM trees: pass `platforms = KmpPlatforms(jvm = true, android = false)`.
 * Android platform without AGP available fails fast when a KMP DSL applies the android target.
 *
 * @param project root project (reserved for future root tasks; registry is process-wide)
 * @param platforms fleet-wide platform set (not per-module)
 * @param jvmTarget Kotlin/JVM bytecode level for the jvm() target
 * @param kotlinVersion Kotlin Gradle plugin coordinate version (defaults to embedded)
 * @param agpVersion AGP version when android platform on; defaults to Android settings when
 *   `androidProjectConfiguration` already ran, else must be set explicitly if android is on
 *   and no Android settings exist (classpath still optional until apply)
 * @param extraPlugins additional buildscript classpath jars/providers only
 */
fun ScriptHandlerScope.kmpProjectConfiguration(
    project: Project,
    platforms: KmpPlatforms = KmpPlatforms(jvm = true, android = true),
    jvmTarget: String = DEFAULT_KMP_JVM_TARGET,
    kotlinVersion: String = embeddedKotlinVersion,
    agpVersion: String? = null,
    extraPlugins: List<Any> = emptyList(),
) {
    @Suppress("UNUSED_VARIABLE")
    val root = project // keep API parallel to androidProjectConfiguration(project=…)

    val resolvedAgpVersion =
        agpVersion
            ?: if (FormaSettingsStore.isSettingsStored) {
                FormaSettingsStore.settings.agpVersion
            } else {
                null
            }

    val classpathDeps = mutableListOf<Any>()
    // Kotlin MPP plugin (same artifact as kotlin-jvm; multiplatform id lives here).
    classpathDeps.add("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    if (platforms.android) {
        if (resolvedAgpVersion != null) {
            // Same AGP coordinate as androidProjectConfiguration — brings
            // com.android.kotlin.multiplatform.library plugin id onto the classpath.
            classpathDeps.add("com.android.tools.build:gradle:$resolvedAgpVersion")
        }
        // If AGP version still unknown, classpath may already include AGP from a prior
        // androidProjectConfiguration in the same buildscript block (order-dependent).
        // Apply-time checks in the feature applicator fail fast if the plugin is missing.
    }
    classpathDeps.addAll(extraPlugins)

    kmpBuildscriptClasspath(classpathDeps)

    val configuration =
        KmpProjectSettings(
            platforms = platforms,
            jvmTarget = jvmTarget,
            kotlinVersion = kotlinVersion,
            agpVersion = resolvedAgpVersion,
        )
    KmpSettingsStore.store(configuration)
    registerKmpDefaults()
}

/**
 * Buildscript classpath helper local to `:kmp` (do **not** import android's
 * `buildScriptConfiguration` — that would create `:kmp` → `:android`).
 */
internal fun ScriptHandlerScope.kmpBuildscriptClasspath(classpathDeps: List<Any>) {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpathDeps.forEach { dep ->
            when (dep) {
                is Provider<*> ->
                    (dep.get() as? PluginDependency)?.let { plugin ->
                        classpath("${plugin.pluginId}:${plugin.version.strictVersion}")
                    }
                        ?: throw IllegalArgumentException(
                            "kmpProjectConfiguration extraPlugins: only PluginDependency providers are supported"
                        )
                else -> classpath(dep)
            }
        }
    }
}

/** Optional repository block type alias for docs/symmetry (unused in v1 store). */
typealias KmpRepositories = RepositoryHandler.() -> Unit
