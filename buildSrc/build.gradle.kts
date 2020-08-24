plugins {
    `kotlin-dsl`
}

val kotlin_version = "1.3.72"

dependencies {
    implementation("com.android.tools.build:gradle:4.1.0-rc01")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
}

repositories {
    jcenter()
    google()
}