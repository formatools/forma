import com.gradle.publish.PublishPlugin

plugins {
    id("com.gradle.plugin-publish") version "0.12.0" apply false
}

val properties = java.util.Properties()
val file = project.rootProject.file("../gradle.properties")
try {
    file.inputStream().use { properties.load(it) }
} catch (e: Throwable) {
    throw Exception(
        "Cannot read gradle.properties file from root project. " +
                "Be sure that you have gradle.properties file in you root dir. " +
                "Expected file path ${file.absolutePath}",
        e
    )
}

// TODO: actually error will not be displayed, find the way to fix it
fun errorNoProperty(propertyName: String): Nothing = error(
    "Be sure that you have property with name $propertyName in you gradle.properties file, stored in ${file.absolutePath}"
)

val kotlinVersion = properties["forma.kotlinVersion"] ?: errorNoProperty("forma.kotlinVersion")
val agpVersion = properties["forma.agpVersion"] ?: errorNoProperty("forma.agpVersion")

subprojects {
    extra["kotlin_dep"] = "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}"
    extra["agp_dep"] = "com.android.tools.build:gradle:$agpVersion"

    repositories {
        google()
        jcenter()
    }

    plugins.whenPluginAdded {
        when (this) {
            is PublishPlugin -> registerPublishingTasks()
        }
    }
}

fun Project.registerPublishingTasks() {
    /**
     * Workaround from https://github.com/gradle/gradle/issues/1246
     */
    val pluginPublishKeysSetup = tasks.register("pluginPublishKeysSetup") {
        doLast {
            val key = System.getenv("GRADLE_PUBLISH_KEY")
            val secret = System.getenv("GRADLE_PUBLISH_SECRET")

            if (key == null || secret == null) throw GradleException(
                "gradlePublishKey and/or gradlePublishSecret are not defined environment variables"
            )

            System.setProperty("gradle.publish.key", key)
            System.setProperty("gradle.publish.secret", secret)
        }
    }

    tasks.named("publishPlugins").configure {
        dependsOn(pluginPublishKeysSetup)
    }
}