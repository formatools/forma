plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "owners")

dependencies {
    implementation(gradleApi())
}
