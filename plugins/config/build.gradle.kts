plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "config")

dependencies {
    implementation(gradleApi())
}
