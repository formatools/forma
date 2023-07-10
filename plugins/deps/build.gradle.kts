plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma"
version = "0.0.1"

dependencies {
    implementation(project(":deps-core"))
}
