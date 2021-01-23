import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import tools.forma.android.plugin.PluginWrapper
import tools.forma.android.plugin.pluginConfiguration

object Plugins {

    val navigationSafeArgs: PluginWrapper<Any> = PluginWrapper(
        "androidx.navigation.safeargs.kotlin"
    )

    val googleServices = PluginWrapper<Any>("com.google.gms.google-services")

    fun crashlytics(mappingFileUploadEnabled: Boolean = false) = PluginWrapper(
        "com.google.firebase.crashlytics",
        google.firebase,
        pluginConfiguration<CrashlyticsExtension> {
            this.mappingFileUploadEnabled = mappingFileUploadEnabled
        }
    )

}