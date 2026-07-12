plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "deps")

dependencies {
    implementation(project(":validation"))
    implementation(project(":target"))
    implementation(project(":config"))
    implementation(gradleKotlinDsl())

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
