plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("tools.forma:android:0.0.1")
    implementation("tools.forma:deps-core:0.0.1")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
}

repositories {
    mavenCentral()
    google()
}
