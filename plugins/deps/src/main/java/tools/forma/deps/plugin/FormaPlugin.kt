@file:Suppress("unused")

package tools.forma.deps.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * We need empty plugin to be able to publish Framework to Gradle Plugins Registry
 */
class FormaPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        return
    }
}