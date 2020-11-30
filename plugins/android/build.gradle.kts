plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma.android"
version = "0.0.1"

val kotlin_version: String by extra

dependencies {
    implementation("com.android.tools.build:gradle:4.1.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    implementation("org.funktionale:funktionale-either:1.2")
    api(project(":plugins:deps-core"))
    api(project(":plugins:target"))
    api(project(":plugins:validation"))
}

gradlePlugin {
    plugins {
        create("Forma") {
            id = "tools.forma.android"
            displayName = "Forma - Meta Build System with Gradle and Android support"
            description = "Best way to structure your Android Project"
            implementationClass = "tools.forma.android.plugin.FormaPlugin"
        }
    }
}

pluginBundle {
    website = "https://forma.tools/"
    vcsUrl = "https://github.com/formatools/forma.git"
    tags = listOf(
        "gradle",
        "kotlin",
        "android",
        "plugin",
        "structure",
        "dependencies",
        "module",
        "rules",
        "project"
    )
}
