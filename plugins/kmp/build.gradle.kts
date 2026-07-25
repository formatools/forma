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
        // Parity with :jvm; no special flags required for registry-only F-106.
    }
}

dependencies {
    // KMP platform plugin — NO hard dependency on :android (F-105).
    // F-107 will use Kotlin MPP APIs from the embedded Kotlin Gradle plugin.
    implementation(embeddedKotlin("gradle-plugin"))

    implementation(project(":core"))
    implementation(project(":deps"))
    implementation(project(":validation"))
    implementation(project(":target"))
    implementation(project(":owners"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
