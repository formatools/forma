import com.gradle.publish.PublishPlugin
import java.util.Properties

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.1.0" apply false
}

class FormaRootConfigurationException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception()

subprojects {
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


tasks.register("publishPluginsToMavenLocal") {
    dependsOn(subprojects.map { "${it.path}:publishToMavenLocal" })
}
