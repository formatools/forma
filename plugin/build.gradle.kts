plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "com.stepango.forma"
version = "0.0.1"

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

/**
 * Workaround from https://github.com/gradle/gradle/issues/1246
 */
val pluginPublishKeysSetup = tasks.register("pluginPublishKeysSetup") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}

tasks.named("publishPlugins").configure {
    dependsOn(pluginPublishKeysSetup)
}

gradlePlugin {
    plugins {
        create("Forma") {
            id = "com.stepango.forma"
            displayName = "Forma - Project Structure Framework"
            description = "Best way to structure your Android Project"
            implementationClass = "com.stepango.forma.plugin.FormaPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/stepango/forma"
    vcsUrl = "https://github.com/stepango/forma.git"
    tags = listOf(
        "gradle",
        "kotlin",
        "android",
        "plugin",
        "structure",
        "dependencies",
        "module",
        "rules",
        "project"
    )
}
