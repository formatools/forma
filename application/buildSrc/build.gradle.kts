plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":plugins:android"))
    implementation(project(":plugins:deps-core"))
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
}

repositories {
    mavenCentral()
    google()
}
