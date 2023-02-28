plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.gradle.plugin-publish")
}

group = "tools.forma"

dependencies {
    implementation(project(":target"))
}