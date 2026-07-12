plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Only gradleApi — do NOT put com.gradle.plugin-publish on the buildSrc
    // classpath or subprojects fail with "plugin is already on the classpath
    // with an unknown version" when they apply the same plugin id.
    implementation(gradleApi())
}
