plugins {
    `kotlin-dsl-base`
    id("com.gradle.plugin-publish") version "1.2.1" apply false
    jacoco
}

// Shared Plugin Portal metadata for all subprojects (F-016 / GH #132).
// Subprojects call formaPublishedPlugin(name = …) instead of copying gradlePlugin blocks.
// Local testing: -PformaLocalVersion=0.1.3-LOCAL  (mavenLocal only; never use for Portal)
val publishedVersion =
    (findProperty("formaLocalVersion") as String?)?.takeIf { it.isNotBlank() } ?: "0.1.3"

formaPluginConfiguration {
    group = "tools.forma"
    version = publishedVersion
    website = "https://forma.tools/"
    vcsUrl = "https://github.com/formatools/forma.git"
    displayName = "Forma - Meta Build System with Gradle and Android support"
    description = "Best way to structure your Gradle Project"
    tags = listOf("kotlin", "android", "structure", "target", "rules", "project")
}

// Jacoco reports on unit-tested modules + LINE ≥60% gate on happy-path class set.
// See formaCoverage.kt and docs/TEST-COVERAGE.md.
subprojects {
    configureFormaCoverage()
}
registerFormaAggregateCoverageReport()

/**
 * Publish every plugin module + :core to ~/.m2/repository for consumer testing
 * without includeBuild / Plugin Portal credentials.
 *
 *   ./gradlew publishAllToMavenLocal
 *   ./gradlew publishAllToMavenLocal -PformaLocalVersion=0.1.3-LOCAL
 */
tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description =
        "Publish Forma plugins (android, target, …) and :core to the local Maven cache"
    // :core first so plugin POMs resolve tools.forma:core when installed from mavenLocal
    dependsOn(":core:publishToMavenLocal")
    subprojects
        .filter { it.name != "core" }
        .forEach { sp ->
            dependsOn("${sp.path}:publishToMavenLocal")
        }
}
