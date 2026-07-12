package tools.forma.android.feature

import deps
import kapt
import tools.forma.config.AndroidProjectSettings
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile
import tools.forma.deps.core.ConfigurationType
import tools.forma.deps.core.Kapt

private fun defaultConfiguration(project: Project, androidProjectSettings: AndroidProjectSettings) {
    project.tasks.withType(JavaCompile::class.java).configureEach {
        targetCompatibility = androidProjectSettings.javaVersionCompatibility.toString()
        sourceCompatibility = androidProjectSettings.javaVersionCompatibility.toString()
    }
    project.tasks.withType(KotlinCompile::class.java).configureEach {
        kotlinOptions.jvmTarget = androidProjectSettings.javaVersionCompatibility.toString()
    }
}

private val sharedFeatureConfiguration:
    (Any, Any, Project, AndroidProjectSettings) -> Unit =
    { _, _, project, configuration -> defaultConfiguration(project, configuration) }

/** Cached — same definition for every pure-JVM target (F-017). */
private val kotlinFeatureDefinitionInstance =
    FeatureDefinition(
        pluginName = "kotlin",
        pluginExtension = KotlinJvmProjectExtension::class,
        featureConfiguration = Unit,
        configuration = sharedFeatureConfiguration
    )

/** Cached — same definition for every Android library / widget target (F-017). */
private val kotlinAndroidFeatureDefinitionInstance =
    FeatureDefinition(
        pluginName = "kotlin-android",
        pluginExtension = KotlinAndroidProjectExtension::class,
        featureConfiguration = Unit,
        configuration = sharedFeatureConfiguration
    )

private val kotlinKaptFeatureDefinitionInstance =
    FeatureDefinition(
        pluginName = "kotlin-kapt",
        pluginExtension = KaptExtension::class,
        featureConfiguration = Unit,
        defaultDependencies = deps("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.2.0".kapt),
        configuration = sharedFeatureConfiguration
    )

fun kotlinFeatureDefinition() = kotlinFeatureDefinitionInstance

fun kotlinAndroidFeatureDefinition() = kotlinAndroidFeatureDefinitionInstance

fun kotlinKaptFeatureDefinition() = kotlinKaptFeatureDefinitionInstance

/**
 * Lazy kapt plugin application when a target declares kapt deps.
 * Map is small and shared; the lambda closes over the [Project] receiver.
 */
fun Project.kaptConfigurationFeature(): Map<ConfigurationType, () -> Unit> =
    mapOf(
        Kapt to {
            applyFeatures(kotlinKaptFeatureDefinition())
        }
    )
