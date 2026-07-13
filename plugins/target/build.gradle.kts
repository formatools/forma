plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "target")

dependencies {
    // F-022: FormaTarget implements TargetRef from core; TargetTemplate remains compat layer
    implementation(project(":core"))
    implementation(gradleApi())
}
