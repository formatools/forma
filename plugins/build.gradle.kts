plugins {
    `kotlin-dsl-base`
    id("com.gradle.plugin-publish") version "1.2.0" apply false
}

ext {
    set("group", "tools.forma")
    set("version", "0.1.3")
    set("tags", listOf("kotlin", "android", "structure", "target", "rules", "project"))
    set("website", "https://forma.tools/")
    set("vcsUrl", "https://github.com/formatools/forma.git")
    set("displayName", "Forma - Meta Build System with Gradle and Android support")
    set("description", "Best way to structure your Gradle Project")
}

group = ext["group"] as String

version = ext["version"] as String
