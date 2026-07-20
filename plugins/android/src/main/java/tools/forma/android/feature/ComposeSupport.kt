package tools.forma.android.feature

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

/** Enable Compose build feature + Kotlin Compose Compiler plugin (required since Kotlin 2.0). */
internal fun Project.applyKotlinComposeCompilerPlugin() {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
}

/**
 * Turn on Compose for an AGP module extension and apply the Kotlin Compose Compiler plugin.
 */
internal fun CommonExtension.enableCompose(
    project: Project,
    composeCompilerVersion: String,
) {
    project.applyKotlinComposeCompilerPlugin()
    buildFeatures.compose = true
    // Legacy pin — still accepted alongside the Compose Compiler plugin
    composeOptions.kotlinCompilerExtensionVersion = composeCompilerVersion
}
