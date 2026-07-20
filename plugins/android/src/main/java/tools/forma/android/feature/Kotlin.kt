package tools.forma.android.feature

import deps
import kapt
import tools.forma.config.AndroidProjectSettings
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile
import tools.forma.deps.core.ConfigurationType
import tools.forma.deps.core.Kapt

private fun defaultConfiguration(project: Project, androidProjectSettings: AndroidProjectSettings) {
    val jvm = androidProjectSettings.javaVersionCompatibility.toString()
    project.tasks.withType(JavaCompile::class.java).configureEach {
        targetCompatibility = jvm
        sourceCompatibility = jvm
    }
    project.tasks.withType(KotlinCompile::class.java).configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.fromTarget(jvm))
    }
}

private val sharedFeatureConfiguration:
    (Any, Any, Project, AndroidProjectSettings) -> Unit =
    { _, _, project, configuration -> defaultConfiguration(project, configuration) }

/**
 * F-019 Phase 1: keep `kotlin-android` + kapt under
 * `android.builtInKotlin=false` + `android.newDsl=false`.
 * Phase 2: built-in Kotlin + migrate kapt→KSP (Dagger) and drop these plugins.
 */
private val kotlinAndroidFeatureDefinitionInstance =
    FeatureDefinition(
        pluginName = "kotlin-android",
        pluginExtension = KotlinAndroidProjectExtension::class,
        featureConfiguration = Unit,
        configuration = sharedFeatureConfiguration
    )

/** Cached — same definition for every pure-JVM target (F-017). */
private val kotlinFeatureDefinitionInstance =
    FeatureDefinition(
        pluginName = "kotlin",
        pluginExtension = KotlinJvmProjectExtension::class,
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
