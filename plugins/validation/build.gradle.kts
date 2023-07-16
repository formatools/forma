plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

group = rootProject.ext["group"] as String
version = rootProject.ext["version"] as String

gradlePlugin {
    website.set(rootProject.ext["website"] as String)
    vcsUrl.set(rootProject.ext["vcsUrl"] as String)
    plugins {
        create(name) {
            id = "$group.$name"
            displayName = rootProject.ext["displayName"] as String
            description = rootProject.ext["description"] as String
            implementationClass = "$id.plugin.FormaPlugin"
            @Suppress("UNCHECKED_CAST")
            tags.set(rootProject.ext["tags"] as List<String>)
        }
    }
}

dependencies {
    implementation(project(":target"))
    implementation(gradleApi())
}
