@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "android")

tasks.named("compileKotlin", KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    // Compile against the same AGP line the sample forces at runtime (see application/settings.gradle.kts).
    // Keep this in lockstep with androidProjectConfiguration(agpVersion=…) consumers (F-003).
    implementation("com.android.tools.build:gradle:8.1.2")
    implementation(embeddedKotlin("gradle-plugin"))
    implementation(project(":target"))
    implementation(project(":validation"))
    implementation(project(":owners"))
    implementation(project(":config"))
    implementation(project(":deps"))
    // F-021: core restriction + target types (pure engine); Android owns concrete type instances + matrix
    implementation(project(":core"))
}

tasks.named<Task>("publishPlugins") {
    dependsOn(
        ":target:publishPlugins",
        ":validation:publishPlugins",
        ":owners:publishPlugins",
        ":config:publishPlugins",
        ":deps:publishPlugins",
    )
}
