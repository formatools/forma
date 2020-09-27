plugins {
    `kotlin-dsl`
}

val kotlin_version = "1.4.10"

dependencies {
    implementation("com.android.tools.build:gradle:4.1.0-rc03")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    implementation("org.funktionale:funktionale-either:1.2")
}

repositories {
    jcenter()
    google()
}