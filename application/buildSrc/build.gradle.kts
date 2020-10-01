plugins {
    `kotlin-dsl`
}

dependencies {
    api(project(":plugin"))
}

repositories {
    jcenter()
    google()
}