plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":plugins:android"))
    implementation(project(":plugins:deps-core"))
}

repositories {
    jcenter()
    google()
}