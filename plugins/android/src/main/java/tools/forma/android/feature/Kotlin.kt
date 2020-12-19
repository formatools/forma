package tools.forma.android.feature

import tools.forma.android.config.FormaConfiguration
import org.gradle.api.Project
import tools.forma.android.dependencies.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tools.forma.deps.ConfigurationType
import tools.forma.deps.Kapt
import Forma
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

private val configuration: (Any, () -> Unit, Project, FormaConfiguration) -> Unit =
    { _, _, project, formaConfiguration ->
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            kotlinOptions.jvmTarget = formaConfiguration.javaVersionCompatibility.toString()
        }
    }

fun kotlinFeatureDefinition(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = FeatureDefinition(
    formaConfiguration = Forma[configurationKey],
    pluginName = "kotlin",
    pluginExtension = KotlinJvmProjectExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8(configurationKey),
    configuration = configuration
)

fun kotlinAndroidFeatureDefinition(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = FeatureDefinition(
    formaConfiguration = Forma[configurationKey],
    pluginName = "kotlin-android",
    pluginExtension = KotlinAndroidProjectExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8(configurationKey),
    configuration = configuration
)

fun kotlinKaptFeatureDefinition(configurationKey: FormaConfigurationKey = DefaultConfigurationKey) = FeatureDefinition(
    formaConfiguration = Forma[configurationKey],
    pluginName = "kotlin-kapt",
    pluginExtension = KaptExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8(configurationKey),
    configuration = configuration
)

fun Project.kaptConfigurationFeature(configurationKey: FormaConfigurationKey = DefaultConfigurationKey): Map<ConfigurationType, () -> Unit> = mapOf(Kapt to {
    applyFeatures(
        kotlinKaptFeatureDefinition(configurationKey)
    )
})
