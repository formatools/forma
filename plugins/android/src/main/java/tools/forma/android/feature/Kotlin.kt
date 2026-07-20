package tools.forma.android.feature

import deps
import kapt
import tools.forma.config.AndroidProjectSettings
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile
import tools.forma.deps.core.ConfigurationType
import tools.forma.deps.core.Kapt
import tools.forma.deps.core.Ksp

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
 * AGP 9 built-in Kotlin: do **not** apply `kotlin-android`.
 * Only wires Java/Kotlin jvmTarget from project settings (F-086).
 */
private val kotlinAndroidFeatureDefinitionInstance =
    FeatureDefinition<Unit, Unit>(
        pluginName = "",
        pluginExtension = null,
        featureConfiguration = Unit,
        configuration = { _, _, project, configuration ->
            defaultConfiguration(project, configuration)
        }
    )

/** Cached — same definition for every pure-JVM target (F-017). */
private val kotlinFeatureDefinitionInstance =
    FeatureDefinition(
        pluginName = "kotlin",
        pluginExtension = KotlinJvmProjectExtension::class,
        featureConfiguration = Unit,
        configuration = sharedFeatureConfiguration
    )

/** Legacy kapt path — still supported if a target declares `.kapt` deps. */
private val kotlinKaptFeatureDefinitionInstance =
    FeatureDefinition(
        pluginName = "kotlin-kapt",
        pluginExtension = KaptExtension::class,
        featureConfiguration = Unit,
        defaultDependencies = deps("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.2.0".kapt),
        configuration = sharedFeatureConfiguration
    )

fun kotlinFeatureDefinition() = kotlinFeatureDefinitionInstance

/** Config-only under AGP 9 built-in Kotlin (no `kotlin-android` plugin). */
fun kotlinAndroidFeatureDefinition() = kotlinAndroidFeatureDefinitionInstance

fun kotlinKaptFeatureDefinition() = kotlinKaptFeatureDefinitionInstance

/**
 * Lazy processor plugins when a target declares kapt/ksp deps.
 * - [Ksp] → apply `com.google.devtools.ksp` (preferred; F-086)
 * - [Kapt] → apply `kotlin-kapt` (legacy; incompatible with built-in Kotlin)
 */
fun Project.processorConfigurationFeatures(): Map<ConfigurationType, () -> Unit> =
    mapOf(
        Ksp to {
            if (!pluginManager.hasPlugin("com.google.devtools.ksp")) {
                pluginManager.apply("com.google.devtools.ksp")
            }
        },
        Kapt to {
            applyFeatures(kotlinKaptFeatureDefinition())
        }
    )

/** @deprecated Use [processorConfigurationFeatures] (includes KSP). */
@Deprecated("Use processorConfigurationFeatures()", ReplaceWith("processorConfigurationFeatures()"))
fun Project.kaptConfigurationFeature(): Map<ConfigurationType, () -> Unit> =
    processorConfigurationFeatures()
