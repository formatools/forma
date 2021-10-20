import com.gradle.publish.PublishPlugin

plugins {
    id("com.gradle.plugin-publish") version "0.12.0" apply false
}

subprojects {
    extra["kotlin_dep"] = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21"
    extra["agp_dep"] = "com.android.tools.build:gradle:7.0.0"

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