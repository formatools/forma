package tools.forma.android.feature

import tools.forma.config.AndroidProjectSettings
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile
import tools.forma.deps.core.ConfigurationType
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

fun kotlinFeatureDefinition() = kotlinFeatureDefinitionInstance

/** Config-only under AGP 9 built-in Kotlin (no `kotlin-android` plugin). */
fun kotlinAndroidFeatureDefinition() = kotlinAndroidFeatureDefinitionInstance

/**
 * Lazy processor plugins when a target declares KSP deps.
 * - [Ksp] → apply `com.google.devtools.ksp` (sole annotation-processing path; F-093)
 */
fun Project.processorConfigurationFeatures(): Map<ConfigurationType, () -> Unit> =
    mapOf(
        Ksp to {
            if (!pluginManager.hasPlugin("com.google.devtools.ksp")) {
                pluginManager.apply("com.google.devtools.ksp")
            }
        },
    )
