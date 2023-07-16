@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = rootProject.ext["group"] as String
version = rootProject.ext["version"] as String

gradlePlugin {
    website.set(rootProject.ext["website"] as String)
    vcsUrl.set(rootProject.ext["vcsUrl"] as String)
    plugins {
        create(name) {
            id = "$group.$name"
            displayName = rootProject.ext["displayName"] as String
            description = rootProject.ext["description"] as String
            implementationClass = "$id.plugin.FormaPlugin"
            @Suppress("UNCHECKED_CAST")
            tags.set(rootProject.ext["tags"] as List<String>)
        }
    }
}

tasks.named("compileKotlin", KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.4.2")
    implementation(embeddedKotlin("gradle-plugin"))
    implementation(project(":target"))
    implementation(project(":validation"))
    implementation(project(":owners"))
    implementation(project(":config"))
    implementation(project(":deps"))
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
