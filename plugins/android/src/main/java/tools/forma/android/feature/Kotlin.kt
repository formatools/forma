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

fun kotlinFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin",
    pluginExtension = KotlinJvmProjectExtension::class,
    featureConfiguration = {},
    configuration = featureConfiguration()
)

private fun <Extension : Any, FeatureConfiguration : Any> featureConfiguration() =
    { _: Extension, _: FeatureConfiguration, project: Project, configuration: AndroidProjectSettings ->
        defaultConfiguration(
            project,
            configuration
        )
    }

fun kotlinAndroidFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-android",
    pluginExtension = KotlinAndroidProjectExtension::class,
    featureConfiguration = {},
    configuration = featureConfiguration()
)

fun kotlinKaptFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-kapt",
    pluginExtension = KaptExtension::class,
    featureConfiguration = {},
    defaultDependencies = deps("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.2.0".kapt),
    configuration = featureConfiguration()
)

fun Project.kaptConfigurationFeature(): Map<ConfigurationType, () -> Unit> = mapOf(Kapt to {
    applyFeatures(
        kotlinKaptFeatureDefinition()
    )
})
