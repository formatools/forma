plugins {
    `kotlin-dsl`
}

group = "tools.forma"

dependencies {
//    implementation(project(":plugins:android"))
    implementation("tools.forma:android")
    implementation("tools.forma:deps-core")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.4")
}
