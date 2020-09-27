package com.stepango.forma.feature

import com.stepango.forma.config.FormaConfiguration
import kotlin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private val configuration: (Any, () -> Unit, Project, FormaConfiguration) -> Unit =
    { _, _, project, formaConfiguration ->
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            kotlinOptions.jvmTarget = formaConfiguration.javaVersionCompatibility.toString()
        }
    }

fun kotlinFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin",
    pluginExtension = KotlinJvmProjectExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8,
    configuration = configuration
)

fun kotlinAndroidFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-android",
    pluginExtension = KotlinAndroidProjectExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8,
    configuration = configuration
)

fun kotlinKaptFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-kapt",
    pluginExtension = KaptExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8,
    configuration = configuration
)
