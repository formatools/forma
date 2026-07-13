plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

formaPublishedPlugin(name = "deps")

dependencies {
    implementation(project(":validation"))
    implementation(project(":target"))
    // F-022: FormaTarget now implements TargetRef from core; expose for Kotlin hierarchy resolution in consumers of FormaTarget (e.g. applyDependencies)
    implementation(project(":core"))
    implementation(project(":config"))
    implementation(gradleKotlinDsl())

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
