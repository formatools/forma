plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish") version "0.12.0"
}

val kotlin_version = "1.4.10"

dependencies {
    implementation("com.android.tools.build:gradle:4.1.0-rc03")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    implementation("org.funktionale:funktionale-either:1.2")
}

repositories {
    google()
    jcenter()
}
