@file:Suppress("unused")

package tools.forma.kmp.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * Empty Settings plugin implementation to enable publishing `tools.forma.kmp`
 * to the Gradle Plugin Portal (same pattern as :android / :jvm).
 *
 * Target DSL and MPP apply live in `tools.forma.kmp` package (F-107+).
 */
class FormaPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        return
    }
}
