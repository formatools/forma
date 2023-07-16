package tools.forma.config.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * We need empty plugin to be able to publish Framework to Gradle Plugins Registry
 */
class ConfigPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        return
    }
}
