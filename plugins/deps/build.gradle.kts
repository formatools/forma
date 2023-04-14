plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma"
version = "0.0.1"

val agp_dep: String by extra
val kotlin_dep: String by extra

dependencies {
    implementation(agp_dep)
    implementation(kotlin_dep)
    implementation(project(":deps-core"))
}

gradlePlugin {
    website.set("https://forma.tools/")
    vcsUrl.set("https://github.com/formatools/forma.git")
    plugins {
        create("Deps") {
            id = "tools.forma.deps"
            displayName = "Forma Deps - Scalable Gradle dependencies framework"
            description = "Escape dependency hell!"
            implementationClass = "tools.forma.deps.plugin.FormaPlugin"
            tags.set(
                listOf(
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
            )
        }
    }
}