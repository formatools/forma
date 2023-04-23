plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.gradle.plugin-publish")
}

group = "tools.forma"
version = "0.0.1"

dependencies {
    implementation(project(":target"))
}