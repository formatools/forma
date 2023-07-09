plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.0" apply false
}

class FormaRootConfigurationException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception()
