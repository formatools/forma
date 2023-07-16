plugins {
    kotlin("jvm")
}

group = "tools.forma"
version = "0.0.1"

dependencies {
    implementation(project(":target"))
    implementation(gradleApi())
}
