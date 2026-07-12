plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "target")

dependencies {
    implementation(gradleApi())
}
