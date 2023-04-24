package tools.forma.android.feature

import deps
import kapt
import tools.forma.config.FormaConfiguration
import org.gradle.api.Project
import tools.forma.android.dependencies.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile
import tools.forma.deps.ConfigurationType
import tools.forma.deps.Kapt

private fun defaultConfiguration(project: Project, formaConfiguration: FormaConfiguration) {
    project.tasks.withType(JavaCompile::class.java).configureEach {
        targetCompatibility = formaConfiguration.javaVersionCompatibility.toString()
        sourceCompatibility = formaConfiguration.javaVersionCompatibility.toString()
    }
    project.tasks.withType(KotlinCompile::class.java).configureEach {
        kotlinOptions.jvmTarget = formaConfiguration.javaVersionCompatibility.toString()
    }
}

fun kotlinFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin",
    pluginExtension = KotlinJvmProjectExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8,
    configuration = featureConfiguration()
)

private fun <Extension : Any, FeatureConfiguration : Any> featureConfiguration() =
    { _: Extension, _: FeatureConfiguration, project: Project, configuration: FormaConfiguration ->
        defaultConfiguration(
            project,
            configuration
        )
    }

fun kotlinAndroidFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-android",
    pluginExtension = KotlinAndroidProjectExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8,
    configuration = featureConfiguration()
)

fun kotlinKaptFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-kapt",
    pluginExtension = KaptExtension::class,
    featureConfiguration = {},
    defaultDependencies = deps(kotlin.stdlib_jdk8, "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.2.0".kapt),
    configuration = featureConfiguration()
)

fun Project.kaptConfigurationFeature(): Map<ConfigurationType, () -> Unit> = mapOf(Kapt to {
    applyFeatures(
        kotlinKaptFeatureDefinition()
    )
})
