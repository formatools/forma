plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma.android"
version = "0.0.1"

val kotlin_dep: String by extra
val agp_dep: String by extra
val either_dep: String by extra

dependencies {
    implementation(agp_dep)
    implementation(kotlin_dep)
    implementation(either_dep)
    implementation(project(":plugins:deps-core"))
    implementation(project(":plugins:target"))
    implementation(project(":plugins:validation"))
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
