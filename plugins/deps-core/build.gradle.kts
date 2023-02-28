plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
}

group = "tools.forma"

val agp_dep: String by extra
val kotlin_dep: String by extra

dependencies {
    implementation(agp_dep)
    implementation(kotlin_dep)
    implementation(project(":validation"))
    implementation(project(":target"))
}
