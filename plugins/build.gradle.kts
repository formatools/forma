plugins {
    `kotlin-dsl-base`
    id("com.gradle.plugin-publish") version "1.2.1" apply false
}

// Shared Plugin Portal metadata for all subprojects (F-016 / GH #132).
// Subprojects call formaPublishedPlugin(name = …) instead of copying gradlePlugin blocks.
formaPluginConfiguration {
    group = "tools.forma"
    version = "0.1.3"
    website = "https://forma.tools/"
    vcsUrl = "https://github.com/formatools/forma.git"
    displayName = "Forma - Meta Build System with Gradle and Android support"
    description = "Best way to structure your Gradle Project"
    tags = listOf("kotlin", "android", "structure", "target", "rules", "project")
}
