@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(
    name = "kmp",
    description = "Kotlin Multiplatform targets for Forma (tools.forma.kmp)",
    extraTags = listOf("kmp", "multiplatform"),
)

tasks.named("compileKotlin", KotlinCompilationTask::class.java) {
    compilerOptions {
        // Parity with :jvm
    }
}

dependencies {
    // KMP platform plugin — NO hard dependency on :android (F-105 / F-107 cycle rule).
    implementation(embeddedKotlin("gradle-plugin"))

    implementation(project(":core"))
    implementation(project(":deps"))
    implementation(project(":validation"))
    implementation(project(":target"))
    implementation(project(":owners"))
    // KmpProjectConfiguration reads FormaSettingsStore for AGP alignment (compileOnly cycle-safe)
    implementation(project(":config"))

    // Optional typed AGP surfaces if needed later; apply path uses string plugin ids + reflection
    // so :kmp never implementation(project(":android")).
    compileOnly("com.android.tools.build:gradle:9.3.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
