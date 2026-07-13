plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "validation")

dependencies {
    // F-022: core owns validation SPI + pure rules; validation is Gradle compat facade
    implementation(project(":core"))
    implementation(project(":target"))
    implementation(gradleApi())
}
