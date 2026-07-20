import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import tools.forma.deps.core.PluginWrapper
import tools.forma.deps.core.pluginConfiguration

/**
 * Legacy PluginWrapper factories for the old .withPlugin chain API.
 * @deprecated F-072: sample now uses Path B derived types (navigationRes etc).
 * New usage: targetPlugin(id) + deriveTargetType(...) (see docs/TARGET-PLUGINS.md).
 * These remain only for transitional references / docs examples.
 */
@Deprecated(
    message = "Plugins.* are for the deprecated .withPlugin chain. Prefer type-owned plugins (deriveTargetType / registerTargetPlugin).",
    level = DeprecationLevel.WARNING
)
object Plugins {

    @Deprecated("Use navigationRes derived type (Path B) instead of .withPlugin(Plugins.navigationSafeArgs)", ReplaceWith("/* navigationRes(...) */"), DeprecationLevel.WARNING)
    val navigationSafeArgs: PluginWrapper<Any> = PluginWrapper(
        "androidx.navigation.safeargs.kotlin"
    )

    @Deprecated("Firebase plugins would use a derived firebase* type, not chain", level = DeprecationLevel.WARNING)
    val googleServices = PluginWrapper<Any>("com.google.gms.google-services")

    @Deprecated("Firebase plugins would use a derived firebase* type, not chain", level = DeprecationLevel.WARNING)
    fun crashlytics(mappingFileUploadEnabled: Boolean = false) = PluginWrapper(
        "com.google.firebase.crashlytics",
        google.firebase,
        pluginConfiguration<CrashlyticsExtension> {
            this.mappingFileUploadEnabled = mappingFileUploadEnabled
        }
    )

}
