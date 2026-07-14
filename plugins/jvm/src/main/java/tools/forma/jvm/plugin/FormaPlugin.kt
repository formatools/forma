@file:Suppress("unused")

package tools.forma.jvm.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * Empty Settings plugin implementation to enable publishing `tools.forma.jvm`
 * to the Gradle Plugin Portal (same pattern as :android and sibling facades).
 */
class FormaPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        return
    }
}
