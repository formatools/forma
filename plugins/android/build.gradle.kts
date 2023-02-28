plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma"
version = "0.0.1"

val kotlin_dep: String by extra
val agp_dep: String by extra

dependencies {
    implementation(agp_dep)
    implementation(kotlin_dep)
    implementation(project(":deps-core"))
    implementation(project(":target"))
    implementation(project(":validation"))
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
