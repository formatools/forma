plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish")
}

group = "tools.forma"
version = "0.0.1"

val agp_dep: String by extra
val kotlin_dep: String by extra

dependencies {
    implementation(agp_dep)
    implementation(kotlin_dep)
    implementation(project(":validation"))
    implementation(project(":target"))
}
