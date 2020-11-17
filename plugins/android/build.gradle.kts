plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "tools.forma.android"
version = "0.0.1"

val kotlin_version = "1.4.10"

dependencies {
    implementation("com.android.tools.build:gradle:4.1.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    implementation("org.funktionale:funktionale-either:1.2")
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
    website = "https://github.com/formatools/forma"
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
