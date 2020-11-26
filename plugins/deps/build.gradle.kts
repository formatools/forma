plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma.deps"
version = "0.0.1"

val kotlin_version: String by extra

dependencies {
    implementation("com.android.tools.build:gradle:4.1.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    api(project(":plugins:deps-core"))
}

gradlePlugin {
    plugins {
        create("Deps") {
            id = "tools.forma.deps"
            displayName = "Forma Deps - Scalable Gradle dependencies framework"
            description = "Escape dependency hell!"
            implementationClass = "tools.forma.deps.plugin.FormaPlugin"
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
