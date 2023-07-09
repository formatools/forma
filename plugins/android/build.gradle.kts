@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma"
version = "0.1.0"

tasks.named("compileKotlin", KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.4.2")
    implementation(embeddedKotlin("gradle-plugin"))
    implementation(project(":deps-core"))
    implementation(project(":target"))
    implementation(project(":validation"))
    implementation(project(":owners"))
    implementation(project(":config"))
}

gradlePlugin {
    website.set("https://forma.tools/")
    vcsUrl.set("https://github.com/formatools/forma.git")
    plugins {
        create("Forma") {
            id = "tools.forma.android"
            displayName = "Forma - Meta Build System with Gradle and Android support"
            description = "Best way to structure your Android Project"
            implementationClass = "tools.forma.android.plugin.FormaPlugin"
            tags.set(
                listOf(
                    "kotlin",
                    "android",
                    "structure",
                    "dependencies",
                    "module",
                    "rules",
                    "project"
                )
            )
        }
    }
}
