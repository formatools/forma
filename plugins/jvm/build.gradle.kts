@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "jvm")

tasks.named("compileKotlin", KotlinCompilationTask::class.java) {
    compilerOptions {
        // Keep parity options with sibling plugins if useful; none strictly required for pure JVM.
    }
}

dependencies {
    // Pure JVM platform plugin — NO AGP. Kotlin JVM only.
    implementation(embeddedKotlin("gradle-plugin"))

    // forma-core + supporting facades (deps/validation own the apply + validator facades)
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

tasks.named<Task>("publishPlugins") {
    // If/when wiring a root publish that chains jvm too, list deps here.
    // For F-030 slice, standalone :jvm:publishPlugins works for validation.
}
