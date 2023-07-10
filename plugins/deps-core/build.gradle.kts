plugins {
    kotlin("jvm")
}

group = "tools.forma"
version = "0.0.1"

dependencies {
    implementation(project(":validation"))
    implementation(project(":target"))
    implementation(project(":config"))
    implementation(gradleKotlinDsl())
}
