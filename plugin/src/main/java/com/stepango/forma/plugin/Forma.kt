@file:Suppress("unused")

package com.stepango.forma.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * We need empty plugin to be able to publish Framework to Gradle Plugins Registry
 */
class FormaPlugin : Plugin<Project> {
    override fun apply(target: Project) = Unit
}