import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import tools.forma.android.plugin.PluginWrapper

object pluginWrappers {

    fun crashlytics(mappingFileUploadEnabled: Boolean = false) = PluginWrapper<CrashlyticsExtension>(
        "com.google.firebase.crashlytics",
        CrashlyticsExtension::class,
        google.firebase
    ) {
        this.mappingFileUploadEnabled = mappingFileUploadEnabled
    }

    val navigationSafeArgs: PluginWrapper<Any> = PluginWrapper(
        "androidx.navigation.safeargs.kotlin"
    )

}