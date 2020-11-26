@file:Suppress("unused")

package tools.forma.android.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * We need empty plugin to be able to publish Framework to Gradle Plugins Registry
 */
abstract class FormaPlugin : Plugin<Project>