plugins {
    `kotlin-dsl`
}

dependencies {
    api(project(":plugins:android"))
}

repositories {
    jcenter()
    google()
}