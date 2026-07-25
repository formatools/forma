package tools.forma.kmp.feature

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import tools.forma.config.FormaSettingsStore
import tools.forma.kmp.settings.KmpPlatforms
import tools.forma.kmp.settings.KmpProjectSettings
import tools.forma.kmp.settings.KmpSettingsStore

/**
 * Plugin ids owned by the KMP feature applicator (F-107 spike under AGP 9.3 / Kotlin 2.3).
 *
 * Documented in docs/KMP-TARGETS.md §4.3.
 */
object KmpPluginIds {
    /** Kotlin Multiplatform Gradle plugin. */
    const val KOTLIN_MULTIPLATFORM: String = "org.jetbrains.kotlin.multiplatform"

    /**
     * Preferred Android wiring under AGP 9.3: dedicated KMP library plugin
     * (not classic `com.android.library`).
     *
     * Confirmed present in `com.android.tools.build:gradle:9.3.0`
     * (`META-INF/gradle-plugins/com.android.kotlin.multiplatform.library.properties`).
     */
    const val ANDROID_KMP_LIBRARY: String = "com.android.kotlin.multiplatform.library"
}

/**
 * Pure resolution of effective platforms / jvmTarget for unit tests without a Project.
 */
object KmpFeatureResolution {
    fun resolveSettings(
        store: KmpProjectSettings? = KmpSettingsStore.settingsOrNull()
    ): KmpProjectSettings = store ?: KmpProjectSettings()

    fun platforms(settings: KmpProjectSettings = resolveSettings()): KmpPlatforms =
        settings.platforms

    fun jvmTarget(settings: KmpProjectSettings = resolveSettings()): String = settings.jvmTarget

    /**
     * Fail-fast precondition for the android platform.
     *
     * @return null if ok; error message if android cannot be applied
     */
    fun androidApplyErrorOrNull(
        settings: KmpProjectSettings,
        androidSettingsStored: Boolean,
    ): String? {
        if (!settings.platforms.android) return null
        if (!androidSettingsStored) {
            return "KMP android platform is enabled but androidProjectConfiguration was not called. " +
                "Either configure Android first (recommended monorepo path), or set " +
                "platforms = KmpPlatforms(jvm = true, android = false) in kmpProjectConfiguration " +
                "for a pure KMP+JVM tree."
        }
        return null
    }
}

/**
 * Applies Kotlin Multiplatform (+ optional Android KMP library) and configures platforms
 * from stored [KmpProjectSettings].
 *
 * Call from KMP target DSL after self-validation. Does **not** depend on the `:android`
 * project — uses string plugin ids and reflective AGP extension configuration.
 *
 * @param packageName Forma package / Android namespace for the android KMP library target
 */
fun Project.applyKotlinMultiplatform(packageName: String) {
    val settings = KmpSettingsStore.settingsOrDefaults()
    // First DSL use without kmpProjectConfiguration: persist defaults so later reads agree.
    if (!KmpSettingsStore.isSettingsStored) {
        KmpSettingsStore.store(settings)
    }

    if (settings.platforms.android) {
        KmpFeatureResolution.androidApplyErrorOrNull(
                settings = settings,
                androidSettingsStored = FormaSettingsStore.isSettingsStored,
            )
            ?.let { throw GradleException(it) }
    }

    apply(plugin = KmpPluginIds.KOTLIN_MULTIPLATFORM)

    if (settings.platforms.android) {
        try {
            apply(plugin = KmpPluginIds.ANDROID_KMP_LIBRARY)
        } catch (err: Exception) {
            throw GradleException(
                "KMP android platform is enabled but plugin " +
                    "'${KmpPluginIds.ANDROID_KMP_LIBRARY}' could not be applied. " +
                    "Ensure AGP is on the buildscript classpath via " +
                    "androidProjectConfiguration(agpVersion=…) and/or " +
                    "kmpProjectConfiguration(agpVersion=…). " +
                    "Preferred plugin id (AGP 9.3): ${KmpPluginIds.ANDROID_KMP_LIBRARY}. " +
                    "Cause: ${err.message}",
                err,
            )
        }
        configureAndroidKmpLibrary(packageName)
    }

    val kotlinExt =
        extensions.getByType(KotlinMultiplatformExtension::class.java)
    if (settings.platforms.jvm) {
        kotlinExt.jvm {
            compilerOptions.jvmTarget.set(JvmTarget.fromTarget(settings.jvmTarget))
        }
    }
    // commonMain / commonTest (+ platform mains) come from the KMP plugin and
    // target presets — do not force src/main/kotlin.
}

/**
 * Configure the Android KMP library extension added on the Kotlin multiplatform extension
 * by [KmpPluginIds.ANDROID_KMP_LIBRARY].
 *
 * Extension name under AGP 9.3: tries `android` then `androidLibrary`
 * (AGP KotlinMultiplatformAndroidPlugin constants).
 */
private fun Project.configureAndroidKmpLibrary(packageName: String) {
    val androidSettings = FormaSettingsStore.settings
    val kotlinExt =
        extensions.findByType(KotlinMultiplatformExtension::class.java)
            ?: throw GradleException(
                "Kotlin multiplatform extension not found after plugin apply"
            )

    val host = kotlinExt as ExtensionAware
    val androidExt =
        host.extensions.findByName("android")
            ?: host.extensions.findByName("androidLibrary")
            ?: throw GradleException(
                "Android KMP library extension not found on kotlin { } after applying " +
                    "'${KmpPluginIds.ANDROID_KMP_LIBRARY}'. Expected extension name 'android' " +
                    "or 'androidLibrary'."
            )

    configureAndroidExtensionReflective(
        androidExt = androidExt,
        packageName = packageName,
        compileSdk = androidSettings.compileSdk,
        minSdk = androidSettings.minSdk,
    )
}

private fun configureAndroidExtensionReflective(
    androidExt: Any,
    packageName: String,
    compileSdk: Int,
    minSdk: Int,
) {
    fun invokeSetter(methodName: String, value: Any?) {
        val method =
            androidExt.javaClass.methods.find {
                it.name == methodName && it.parameterTypes.size == 1
            } ?: return
        method.invoke(androidExt, value)
    }

    invokeSetter("setNamespace", packageName)
    invokeSetter("setCompileSdk", compileSdk)
    invokeSetter("setMinSdk", minSdk)
}
