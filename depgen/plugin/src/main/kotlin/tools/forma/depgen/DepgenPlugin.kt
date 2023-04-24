package tools.forma.depgen

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.embeddedKotlinVersion

/**
 * Plugin which simplifies dependency management
 */
class DepgenPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.dependencyResolutionManagement {
            versionCatalogs {
                create("libs") {
                    // For test purposes only
                    library("kotlin-stdlib", "org.jetbrains.kotlin:kotlin-stdlib:$embeddedKotlinVersion")
                }
            }
        }
    }
}
