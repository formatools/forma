plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "validation")

dependencies {
    implementation(project(":target"))
    implementation(gradleApi())
}
