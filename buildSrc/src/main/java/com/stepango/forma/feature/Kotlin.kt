package com.stepango.forma.feature

import FeatureDefinition
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import kotlin

fun kotlinFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin",
    pluginExtension = KotlinJvmProjectExtension::class,
    featureConfiguration = { },
    defaultDependencies = kotlin.stdlib_jdk8
) { _, _, _, _ -> }

fun kotlinAndroidFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-android",
    pluginExtension = KotlinAndroidProjectExtension::class,
    featureConfiguration = {},
    defaultDependencies = kotlin.stdlib_jdk8
) { _, _, _, _ -> }

fun kotlinKaptFeatureDefinition() = FeatureDefinition(
    pluginName = "kotlin-kapt",
    pluginExtension = KaptExtension::class,
    featureConfiguration = { },
    defaultDependencies = kotlin.stdlib_jdk8
) { _, _, _, _ -> }